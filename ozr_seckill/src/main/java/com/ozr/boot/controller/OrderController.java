package com.ozr.boot.controller;

import com.google.common.base.Verify;
import com.google.common.util.concurrent.RateLimiter;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.mp.MqProducer;
import com.ozr.boot.response.CommonRetrunType;
import com.ozr.boot.server.ItemService;
import com.ozr.boot.server.OrderService;
import com.ozr.boot.server.PromoService;
import com.ozr.boot.server.model.OrderModel;
import com.ozr.boot.server.model.UserModel;
import com.ozr.boot.util.CodeUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.BufferOverflowException;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @Author OZR
 * @Date 2021/6/30 16:07
 */
@Controller
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*",originPatterns = "*")
public class OrderController  extends  BaseController{
    @Autowired
    OrderService orderService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MqProducer mqProducer;

    @Autowired
    ItemService itemService;
    @Autowired
    PromoService promoService;


    private ExecutorService executorService; //线程池

    private RateLimiter orderCreateRateLimiter;

    @PostConstruct
    public void init(){
        //生成以个20个线程的线程池给了executorService
        executorService= Executors.newFixedThreadPool(20);

        orderCreateRateLimiter = RateLimiter.create(100);
        /**
         * 1s通过100个请求，超过就拒绝
         */
    }


    @ResponseBody
    @PostMapping("/createorder")
    public CommonRetrunType createorder(@RequestParam("itemId")Integer itemId,
                                        @RequestParam("amount")Integer amount,
                                        //非必须参数
                                        @RequestParam(value = "promoId",required = false)Integer promoId,
                                        @RequestParam(value = "secondKillToken",required = false)String secondKillToken
                                       ) throws BusinessException {

        //seesion的方式，校验用户是否登录
//        Boolean is_login = (Boolean)httpServletRequest.getSession().getAttribute("is_LOGIN");
//
//        if(is_login == null || !is_login.booleanValue()) {
//            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
//        }
//        UserModel userModel =(UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");




        //前端传进来的
        String token_UUID = httpServletRequest.getParameterMap().get("token_UUID")[0];
        if(token_UUID == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }
   //在redis上去获取去
        UserModel userModel =(UserModel) redisTemplate.opsForValue().get(token_UUID);
        //等于null就是会话过期了
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }

        //        校验秒杀令牌
        if(promoId != null){
            //有活动才校验
            String secondKillTokenRedis =(String) redisTemplate.opsForValue().get("promo_token"+promoId+"userId_"+userModel.getId()+"itemId_"+itemId);
            if(secondKillTokenRedis==null){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"令牌校验失败，不能下单");
            }
            if(!StringUtils.equals(secondKillToken,secondKillTokenRedis)){
                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"令牌校验失败，不能下单");
            }
        }

//        进入交易流程之前查看这个item是不是售罄的状态
//        对应的key存在就说明这个item售罄了
//         放入到秒杀令牌的生成之中去
//           这里还是要判断一下
//        因为多个用户都拿到了令牌，当时是有库存的，后面有人买了，让库存没有了，还是会有用户有令牌
//        但是将校验在令牌中也交校验一次的话会减少令牌的生成，缓解压力
        //
        if(redisTemplate.hasKey("item_stock_inValid_"+itemId)){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

       //我们把下面的操作交给那20个线程来进行
        //也就是我们同时处理20个线程，多了就排队
        //等价于创建了一个拥塞窗口为20的等待队列，用于队列化泄洪
        Future<Object> future = executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                //下单之前添加库存流水记录 再去进行下单事务型消息
                String stockLogId = itemService.initStockLog(itemId, amount);


                //OrderModel orderModel = orderService.creatOrder(itemId, userModel.getId(),promoId, amount);
                //不直接的创建订单了
                //而是使用事务性消息创建订单
                boolean result = mqProducer.transactionAsyncReduceStock(itemId, userModel.getId(), promoId, amount, stockLogId);
                if (!result) {
                    throw new BusinessException(EmBusinessError.UNKNOW_ERROR, "下单失败");
                }
                return null;
            }
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        }


        return CommonRetrunType.creat(null);
    }
    //获取验证码
    @GetMapping("generateVerifyCode")
    @ResponseBody
    public void generateVerifyCode(HttpServletResponse response) throws BusinessException, IOException {
        if(!orderCreateRateLimiter.tryAcquire()){
            throw new BusinessException(EmBusinessError.RATELIMITER);
        }


        String token_UUID = httpServletRequest.getParameterMap().get("token_UUID")[0];
        if(token_UUID == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆");
        }
        //在redis上去获取去
        UserModel userModel =(UserModel) redisTemplate.opsForValue().get(token_UUID);
        //等于null就是会话过期了
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆");
        }

        Map<String,Object> map = CodeUtil.generateCodeAndPic();
        System.out.println(map.get("code"));
//        写入这个响应体里面，将图片直接返回到前端
        ImageIO.write((RenderedImage) map.get("codePic"), "jpeg", response.getOutputStream());
        redisTemplate.opsForValue().set("verify_code_"+userModel.getId(),map.get("code"));
        redisTemplate.expire("verify_code_"+userModel.getId(),5,TimeUnit.MINUTES);
    }



    //生成秒杀令牌
    @ResponseBody
    @PostMapping("/generateToken")
    public CommonRetrunType generateToken(@RequestParam("itemId")Integer itemId,
                                        @RequestParam(value = "promoId")Integer promoId,
                                        @RequestParam("verifycode")String verifycode ) throws BusinessException {
        //前端传进来的
        String token_UUID = httpServletRequest.getParameterMap().get("token_UUID")[0];
        if(token_UUID == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }
        //在redis上去获取去
        UserModel userModel =(UserModel) redisTemplate.opsForValue().get(token_UUID);
        //等于null就是会话过期了
        if(userModel == null){
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登陆，不能下单");
        }
        //校验验证码
        String verifyCodeRedis =(String) redisTemplate.opsForValue().get("verify_code_" + userModel.getId());
        if(StringUtils.isEmpty(verifycode)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "非法请求");
        }
        if(!StringUtils.equals(verifycode,verifyCodeRedis)){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "验证码错误");
        }

        //生成秒杀令牌
        String secondKillToken = promoService.generateSecondKillToken(promoId, userModel.getId(), itemId);
        if(secondKillToken == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "令牌生成失败");
        }
        return CommonRetrunType.creat(secondKillToken);
    }
}
