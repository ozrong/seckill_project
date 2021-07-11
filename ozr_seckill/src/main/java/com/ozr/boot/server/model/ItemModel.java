package com.ozr.boot.server.model;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author OZR
 * @Date 2021/6/29 14:29
 */
@Data
@ToString
public class ItemModel implements Serializable {

    private static final long serialVersionUID = 975546272433946501L;
    private Integer id;
    //商品名称
    @NotBlank(message = "商品名称不能为空")
    private String title;
    //商品价格
    @NotNull(message = "商品价格不能为空")
    @Min(value = 0,message = "商品价格必须大于0")
    private BigDecimal price;
    //商品的库存
    @NotNull(message = "库存不能不填")
    private Integer stock;
    //商品的描述
    @NotBlank(message = "商品描述信息不能为空")
    private String description;
    //商品的销量
    private Integer sales;
    //商品描述图片的url
    @NotBlank(message = "商品图片信息不能为空")
    private String imgurl;

    //使用聚合模型，添加一个秒杀活动
    //如果promoModel不是null,就还有一个秒杀活动
    private  PromoModel promoModel;

}
