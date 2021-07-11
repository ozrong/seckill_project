package com.ozr.boot.server;

import com.ozr.boot.server.model.PromoModel;

/**
 * @Author OZR
 * @Date 2021/6/30 20:45
 */
public interface PromoService {

    //获取将开始的秒杀，正在进行的秒杀
    PromoModel getPromoByItemId(Integer id);

    //发布活动的方法
    void publishPromo(Integer promoId);


    //生成秒杀令牌
    String generateSecondKillToken(Integer promoId,Integer userId,Integer itemId);
}
