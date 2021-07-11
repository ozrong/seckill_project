package com.ozr.boot.server.impl;

import com.ozr.boot.dao.PromoDaoMapper;
import com.ozr.boot.dataObject.PromoDao;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.server.ItemService;
import com.ozr.boot.server.PromoService;
import com.ozr.boot.server.UserService;
import com.ozr.boot.server.model.ItemModel;
import com.ozr.boot.server.model.PromoModel;
import com.ozr.boot.server.model.UserModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author OZR
 * @Date 2021/6/30 20:47
 */
@Service
public class PrimoServiceImpl implements PromoService {

    @Autowired
    PromoDaoMapper promoDaoMapper;

    @Autowired
    ItemService itemService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserService userService;

    @Override
    public PromoModel getPromoByItemId(Integer id) {
        //获取商品秒杀活动信息
        PromoDao promoDao = promoDaoMapper.selectByItemId(id);

        PromoModel promoModel = convertModelFromDao(promoDao);
        //这个商品没有秒杀活动。直接返回null
        if(promoModel == null){
            return null;
        }

        //判断秒杀活动是否开始
        DateTime now = DateTime.now();

        //是不是当前时间在指定的时间promoModel.getStartDate()之后，是活动未开始
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);

            //是不是当前时间在指定的时间promoModel.getEndDate()之后，是活动已经结束
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            //其他情况就是活动已经开始
            promoModel.setStatus(2);
        }


        return promoModel;
    }

    @Override
    public void publishPromo(Integer promoId) {
        PromoDao promoDao = promoDaoMapper.selectByPrimaryKey(promoId);
        if(promoDao == null || promoDao.getId()==0){
            return;
        }
        ItemModel itemModel = itemService.getItemById(promoDao.getItemId());
        //同步到redis
        redisTemplate.opsForValue().set("promo_item_stock_"+itemModel.getId(),itemModel.getStock());

        //设置大闸的限制，也就是秒杀令牌的数量
        //限制为当前活动商品数据的5倍
        redisTemplate.opsForValue().set("promo_door_count_"+promoId,itemModel.getStock()*5);


    }

    /**
     * 验证用户和item,生成秒杀令牌
     * */
    @Override
    public String generateSecondKillToken(Integer promoId,Integer userId,Integer itemId) {

        //判断是否售罄
        if(redisTemplate.hasKey("item_stock_inValid_"+itemId)){
           return null;
        }

        //首先判断还可以生成令牌吗
        long count = redisTemplate.opsForValue().increment("promo_door_count_"+promoId,-1);
        if(count < 0){
            //减完1为负了 那么就不在生成令牌
            return null;
        }

        //获取商品秒杀活动信息
        PromoDao promoDao = promoDaoMapper.selectByPrimaryKey(promoId);
        PromoModel promoModel = convertModelFromDao(promoDao);
        //这个商品没有秒杀活动。直接返回null
        if(promoModel == null){
            return null;
        }
        //判断秒杀活动是否开始
        DateTime now = DateTime.now();
        //是不是当前时间在指定的时间promoModel.getStartDate()之后，是活动未开始
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
            //是不是当前时间在指定的时间promoModel.getEndDate()之后，是活动已经结束
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            //其他情况就是活动已经开始
            promoModel.setStatus(2);
        }
        //校验用户和item 是不是存在
        ItemModel itemModel = itemService.getItemByIdFromCache(itemId);
        if(itemModel == null){
            return  null;
        }
        UserModel userModel = userService.getUserByIdFromCache(userId);
        if(userModel == null){
            return  null;
        }
        if(promoModel.getStatus()!=2){
           //状态不会2 就是不让生成秒杀令牌
            return null;
        }
        //活动开始生成秒杀令牌
        String secondKillToken = UUID.randomUUID().toString().replace("-","");
        //存入redis
        redisTemplate.opsForValue().set("promo_token"+promoId+"userId_"+userId+"itemId_"+itemId,secondKillToken);
        //设置有效时间
        redisTemplate.expire("promo_token"+promoId+"userId_"+userId+"itemId_"+itemId,5, TimeUnit.MINUTES);
        return secondKillToken;
    }

    private PromoModel convertModelFromDao(PromoDao promoDao) {
        if(promoDao == null){
            return null;
        }
        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDao,promoModel);

        promoModel.setPromItemPrice(new BigDecimal(promoDao.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDao.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDao.getEndDate()));
        return promoModel;
    }
}
