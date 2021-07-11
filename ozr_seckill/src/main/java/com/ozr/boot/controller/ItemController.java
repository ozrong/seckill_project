package com.ozr.boot.controller;

import com.ozr.boot.controller.viewObject.ItemVO;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.response.CommonRetrunType;

import com.ozr.boot.server.ItemService;
import com.ozr.boot.server.PromoService;
import com.ozr.boot.server.impl.CacheServiceImpl;
import com.ozr.boot.server.model.ItemModel;
import com.ozr.boot.server.model.PromoModel;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author OZR
 * @Date 2021/6/29 15:15
 */
@RestController
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*",originPatterns = "*")
public class ItemController extends BaseController {
    @Autowired
    ItemService itemService;

    @Autowired
    RedisTemplate redisTemplate;

     @Autowired
    CacheServiceImpl cacheService;
     @Autowired
    PromoService promoService;
    //创建商品
    @PostMapping("/creat")
    public CommonRetrunType creatItem(@RequestParam(name = "title")String title,
                                      @RequestParam(name = "description")String description,
                                      @RequestParam(name = "price") BigDecimal price,
                                      @RequestParam(name = "stock")Integer stock,
                                      @RequestParam(name = "imgUrl")String imgUrl) throws BusinessException {

        //封装service层请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setDescription(description);
        itemModel.setStock(stock);
        itemModel.setImgurl(imgUrl);
        itemModel.setPrice(price);

        ItemModel item = itemService.createItem(itemModel);
        ItemVO itemVO = convertVOFromModel(item);


        return CommonRetrunType.creat(itemVO);
    }

    private ItemVO convertVOFromModel(ItemModel itemModel){
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

    //商品详情页浏览
    @GetMapping("/getItem")
    public CommonRetrunType getItem(@RequestParam("id") Integer id){
        ItemModel itemModel = null;
        //首先查看本地缓存
        itemModel = (ItemModel)cacheService.getFromCommonCache("item_" + id);
        if(itemModel == null){
            //本地缓存没有，查redis
            // 取redis中看有没有缓存这个item的详情
            itemModel =(ItemModel) redisTemplate.opsForValue().get("item_" + id);

            if(itemModel == null){
                //如果redis也没有，查访问数据库
                itemModel = itemService.getItemById(id);
                //将itemMOdel放入redis
                redisTemplate.opsForValue().set("item_" + id,itemModel);
                //设置缓存时间
                redisTemplate.expire("item_"+id,10, TimeUnit.MINUTES);
            }
            //保存一份数据到本地缓存
            cacheService.setCommonCache("item_"+id,itemModel);
        }
        ItemVO itemVO = convertVOFromModel(itemModel);
        //不空表示  有活动，添加响应的字段
        if(itemModel.getPromoModel() != null){
            PromoModel promoModel = itemModel.getPromoModel();
            itemVO.setPromoStatus(promoModel.getStatus());
            itemVO.setPromoId(promoModel.getId());
            itemVO.setStartDate(promoModel.getStartDate().toString(DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")));
            itemVO.setPromPrice(promoModel.getPromItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }
        return  CommonRetrunType.creat(itemVO);
    }

    //商品列表浏览
    @GetMapping("/list")
    public CommonRetrunType getList(){
        List<ItemModel> itemModels = itemService.listItem();
        //使用stream apiJ将list内的itemModel转化为ITEMVO;
        List<ItemVO> collect = itemModels.stream().map(itemModel -> {
            ItemVO itemVO = this.convertVOFromModel(itemModel);
            return itemVO;

        }).collect(Collectors.toList());
        return  CommonRetrunType.creat(collect);
    }



    //使用这个请求当发布活动
    @GetMapping("/publishPromo")
    public CommonRetrunType publishPromo(@RequestParam("id") Integer id){
        promoService.publishPromo(id);
        return  CommonRetrunType.creat(null);
    }







}
