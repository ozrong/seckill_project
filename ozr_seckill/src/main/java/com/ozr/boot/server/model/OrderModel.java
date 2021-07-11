package com.ozr.boot.server.model;

import lombok.Data;
import lombok.Generated;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @Author OZR
 * @Date 2021/6/30 13:23
 */

//交易模型
@Generated
@ToString
@Data
public class OrderModel {
    //订单号
   private String id;

   //用户id
    private Integer userId;

    //商品id
    private Integer itemId;

    //购买的数量
    private Integer amount;

    //正常的商品单价
    private BigDecimal itemPrice;


    //活动的id
    private  Integer promoId;

    //订单金额
    private BigDecimal orderPrice;


}
