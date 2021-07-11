package com.ozr.boot.server;

import com.ozr.boot.error.BusinessException;
import com.ozr.boot.server.model.ItemModel;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author OZR
 * @Date 2021/6/29 14:49
 */

public interface ItemService {
    //创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    //商品列表浏览
    List<ItemModel> listItem();

    //商品详情浏览
    ItemModel getItemById(Integer id);

    //库存扣减
    boolean decreaseStock(Integer itemId,Integer amount)throws BusinessException;

    //库存回补
    boolean increaseStock(Integer itemId,Integer amount);

    //异步更新库存
    boolean asyncDecreaseStock(Integer itemId,Integer amount);

    //商品销量增加
    void increaseSales(Integer itemId,Integer amount)throws BusinessException;


    //item及promo model缓存模型
    ItemModel getItemByIdFromCache(Integer id);

    //初始化库存流水
    String initStockLog(Integer itemId,Integer amount);



}
