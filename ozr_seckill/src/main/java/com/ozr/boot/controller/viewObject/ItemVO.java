package com.ozr.boot.controller.viewObject;

import lombok.Data;
import org.joda.time.DateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @Author OZR
 * @Date 2021/6/29 14:29
 */
@Data
public class ItemVO {
    private Integer id;
    //商品名称

    private String title;
    //商品价格

    private BigDecimal price;
    //商品的库存

    private Integer stock;
    //商品的描述

    private String description;
    //商品的销量
    private Integer sales;
    //商品描述图片的url

    private String imgurl;

    //商品是不是有秒杀活动
    //0 没有活动
    //1 秒杀活动待开始
    //2 秒杀活动进行中
    //3 秒杀活动以结束
    private  Integer promoStatus;


    //秒杀活动的价格
    private  BigDecimal promPrice;

    //活动开始时间
    private String startDate;

    //活动结束id
    private  Integer promoId;


}
