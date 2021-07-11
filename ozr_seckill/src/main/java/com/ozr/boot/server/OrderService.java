package com.ozr.boot.server;

import com.ozr.boot.error.BusinessException;
import com.ozr.boot.server.model.OrderModel;

/**
 * @Author OZR
 * @Date 2021/6/30 13:37
 */
public interface OrderService {


//创建订单的时候需要考虑是不是活动期间
//推荐使用 1,通过前端url上传过来秒杀活动id，然后下单接口内校验对应id是否属于对应商品且活动已开始
          //2.直接在下单接口内判断对应的商品是否存在秒杀活动，若存在进行中的则以秒杀价格下单，每次都要进行判断增加了数据库的压力


    OrderModel creatOrder(Integer itemId,Integer userId,Integer promoId, Integer amount,String stockLogId) throws BusinessException;

}
