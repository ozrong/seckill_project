package com.ozr.boot.server.model;

import lombok.Data;
import lombok.ToString;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author OZR
 * @Date 2021/6/30 19:57
 */
@Data
@ToString
public class PromoModel implements Serializable {

 private static final long serialVersionUID = -932709638323377152L;
 private Integer id;
   //活动名字
    private String PromoName;

    //开始时间
    private DateTime  startDate;


    //结束时间
    private  DateTime  endDate;

    //秒杀活动的商品
    private Integer itemId;

    //秒杀活动价格
    private BigDecimal promItemPrice;

    //活动的状态
    // 1表示未开始
    //2 已经开始
    // 3 表示已经结束
    private Integer status;


}
