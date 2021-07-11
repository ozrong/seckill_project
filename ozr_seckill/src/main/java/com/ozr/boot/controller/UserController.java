package com.ozr.boot.controller;

import com.alibaba.druid.util.StringUtils;
import com.ozr.boot.controller.viewObject.UserVO;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.response.CommonRetrunType;
import com.ozr.boot.server.UserService;
import com.ozr.boot.server.model.UserModel;
import com.ozr.boot.validator.ValidatorImpl;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.validation.Validator;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author OZR
 * @Date 2021/6/28 10:27
 */
@Controller
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*",originPatterns = "*")
//@RequestMapping("/user") //指定在这个contorller下的请求需要使用/user/xxxx才能访问
public class UserController  extends BaseController{
    @Autowired
    UserService userService;


    @Autowired
    HttpServletRequest httpServletRequest;
    /*
    * 在springboot里面，明明是到哪里模式，这个HttpServletRequest怎么区分是哪个访问的尼？？
    * 使用bean包装的HttpServletRequest本质是一个process(进程)
    * 在每个线程中处理自己的事
    *  */

    @Autowired
    RedisTemplate redisTemplate;

    /**
     *
     * @param telphone
     * @param password 这个密码是没加密的 用户输入进来的
     * @return
     * @throws BusinessException
     */
    //用户登录接口
    @ResponseBody
    @PostMapping("/login")
    public CommonRetrunType login(@RequestParam("telphone")String telphone,
                                  @RequestParam("password") String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

      //入参校验
      if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)
         || org.apache.commons.lang3.StringUtils.isEmpty(password)){
          throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"密码或用户名错误");
      }

      //用户登录校验
       UserModel userModel =  userService.validataLogin(telphone,this.encodeByMD5(password));
      //将登陆凭证加入到用户登录成功的session中
//        this.httpServletRequest.getSession().setAttribute("is_LOGIN",true);
//        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);
//        return CommonRetrunType.creat(null);

        //使用token

        //生成登录凭证（token生成UUID）  保存到redis中
        String token_UUID = UUID.randomUUID().toString();
        token_UUID=token_UUID.replace("-","");
//        建立用户登录状态和token之间的联系key-value
        redisTemplate.opsForValue().set(token_UUID,userModel);
        //有效时间
        redisTemplate.expire(token_UUID,1, TimeUnit.HOURS);

        //下发token
        return CommonRetrunType.creat(token_UUID);



    }



    //用户获取otp短信接口
    @ResponseBody
    @PostMapping("/getotp")
    public CommonRetrunType getOtp(@RequestParam("telphone")String telphone){
        //按一定的规则生成OTP验证码
        Random random = new Random();
        int randomNum = random.nextInt(999999);//[0-999999]
        randomNum+=100000;
        String OTPCode = String.valueOf(randomNum);
        //将OTP验证码同对应的手机号关联
        //正常使用redis来做这一步部分（key:value）
        //我们暂时使用HttpSesion来存一下
        httpServletRequest.getSession().setAttribute(telphone,OTPCode);//将手机号与otpcode关联

        //将OTP验证码通过短信发送给用户 省略了 需要第三方的服务
        System.out.println("otp:"+OTPCode+" telphone:"+telphone);

        return CommonRetrunType.creat(null);

    }

    //用户注册接口
    @ResponseBody
    @PostMapping("/register")
    public CommonRetrunType register(@RequestParam(name="telphone")String telphone,
                                     @RequestParam(name="otpCode")String otpCode,
                                     @RequestParam(name="name")String name,
                                     @RequestParam(name="gender")Integer gender,
                                     @RequestParam(name="age")Integer age,
                                     @RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        //验证手机号和对应的otp是否符合
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!com.alibaba.druid.util.StringUtils.equals(otpCode,inSessionOtpCode)){
            //为什么使用这个里面的equals
            //这里面做了参数的判空
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }
        //用户注册流程
        UserModel userModel  = new UserModel();
        userModel.setName(name);
        userModel.setAge(age);
        userModel.setTelphone(telphone);
        userModel.setGender(gender);
        userModel.setRegisterModa("byTelphone");

        //password是明文，是不可以的
        //使用MD5加密在存入数据库
        userModel.setEncrptPassword(this.encodeByMD5(password));

        userService.register(userModel);
        return CommonRetrunType.creat(null,"success");


    }
    public String encodeByMD5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64Encoder = new BASE64Encoder();
        String encodeStr = base64Encoder.encode(md5.digest((str.getBytes("utf-8"))));
        return encodeStr;
    }








    //    @GetMapping("get")
//    public UserModel getUser(@RequestParam("id") Integer id){
//        UserModel userModel = userService.getUserById(id);
//        return userById;
//    }
    @ResponseBody
    @GetMapping("/get")
    public CommonRetrunType getUser(@RequestParam("id") Integer id) throws BusinessException {
        UserModel userModel = userService.getUserById(id);
        //假如这个用户不存在 ，就抛出
        if (userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        //将核心领域的模型用户对象转化为可供UI使用的viewObject
        UserVO userVO = convertFromModel(userModel);
        return CommonRetrunType.creat(userVO);
    }
    private UserVO convertFromModel(UserModel userModel) {
        if (userModel == null) return null;
        else {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(userModel, userVO);
            return userVO;
        }
    }
}



