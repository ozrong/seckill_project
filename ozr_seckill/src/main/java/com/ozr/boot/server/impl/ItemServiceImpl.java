package com.ozr.boot.server.impl;

import com.ozr.boot.dao.ItemDaoMapper;
import com.ozr.boot.dao.ItemStockMapper;
import com.ozr.boot.dao.PromoDaoMapper;
import com.ozr.boot.dao.StockLogDaoMapper;
import com.ozr.boot.dataObject.ItemDao;
import com.ozr.boot.dataObject.ItemStockDao;
import com.ozr.boot.dataObject.PromoDao;
import com.ozr.boot.dataObject.StockLogDao;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.mp.MqProducer;
import com.ozr.boot.server.ItemService;
import com.ozr.boot.server.PromoService;
import com.ozr.boot.server.model.ItemModel;
import com.ozr.boot.server.model.PromoModel;
import com.ozr.boot.validator.ValidationResult;
import com.ozr.boot.validator.ValidatorImpl;
import org.apache.ibatis.annotations.Param;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author OZR
 * @Date 2021/6/29 14:50
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    ValidatorImpl validator;

    @Autowired
    ItemDaoMapper itemDaoMapper;

    @Autowired
    ItemStockMapper itemStockMapper;
    @Autowired
    PromoService promoService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    MqProducer mqProducer;
    @Autowired
    StockLogDaoMapper stockLogDaoMapper;


    @Override
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }

        //转化itemmodel->dataobject
        ItemDao itemDao = this.convertItemDOFromItemModel(itemModel);

        //写入数据库
        itemDaoMapper.insertSelective(itemDao);

        itemModel.setId(itemDao.getId());

        ItemStockDao itemStockDao = this.convertItemStockDOFromItemModel(itemModel);

        itemStockMapper.insertSelective(itemStockDao);

        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    private ItemStockDao convertItemStockDOFromItemModel(ItemModel itemModel) {
        if(itemModel == null){
            return  null;
        }
        ItemStockDao itemStockDao = new ItemStockDao();
        itemStockDao.setItemId(itemModel.getId());
        itemStockDao.setStock(itemModel.getStock());
        return  itemStockDao;

    }

    private ItemDao convertItemDOFromItemModel(ItemModel itemModel) {
        if(itemModel == null){
            return  null;
        }
        ItemDao itemDao = new ItemDao();
        BeanUtils.copyProperties(itemModel,itemDao);
        itemDao.setPrice(itemModel.getPrice().doubleValue());
        return itemDao;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDao> itemDaos = itemDaoMapper.listItem();
        List<ItemModel> itemModelList = itemDaos.stream().map(itemDao -> {
            ItemStockDao itemStockDao = itemStockMapper.selectByItemId(itemDao.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemStockDao, itemDao);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDao itemDao = itemDaoMapper.selectByPrimaryKey(id);
        if(itemDao == null) return null;
        //操作获取库存
        ItemStockDao itemStockDao = itemStockMapper.selectByItemId(id);
        //将pojo->model
        ItemModel itemModel = convertModelFromDataObject(itemStockDao, itemDao);


        //获取是不是有秒杀活动

        PromoModel promoModel = promoService.getPromoByItemId(id);
        if(promoModel != null && promoModel.getStatus() !=3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;



    }

    private ItemModel convertModelFromDataObject(ItemStockDao itemStockDao, ItemDao itemDao) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDao,itemModel);
        itemModel.setPrice(new BigDecimal(itemDao.getPrice()));
        itemModel.setStock(itemStockDao.getStock());
        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
//        int i =  itemStockMapper.decreaseStock(itemId,amount);
        //优化，减redis中的缓存，而不是直接对数据库进行操作
        long i = redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount*-1);
        /**
         * increment("key",num)就是将key对应的value 加num,在返回这个运算后的value
         *
         */
        if(i>0){
            //注意是大于等于0  等于0刚好卖完
            //发送更新库存的消息
//            boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
//            if(!mqResult){
//                //消息没有发送成功，就把缓存中的库存加回去
//                redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount);
////                在结束这笔交易
//                return false;
//             }
            return true;
        }else if(i==0){
            //售罄，将该物品标记为售罄
            redisTemplate.opsForValue().set("item_stock_inValid_"+itemId,"true");
            return true;

        }else {
            //库存都减为负了，把刚刚减的加回去
            increaseStock(itemId,amount);
            return false;
        }
    }

    //库存回补
    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount);
        return true;
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
        return mqResult;
    }


    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {

        itemDaoMapper.increaseSales(itemId,amount);
    }

    //初始化库存流水
    @Transactional
    @Override
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDao stockLogDao = new StockLogDao();
        stockLogDao.setAmount(amount);
        stockLogDao.setItemId(itemId);
        stockLogDao.setStatus(1);
        stockLogDao.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDaoMapper.insertSelective(stockLogDao);
        return stockLogDao.getStockLogId();
    }

    @Override
    public ItemModel getItemByIdFromCache(Integer id) {
        //先查redis
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_"+id);
        if(itemModel == null){
            //redis没有查数据库
            itemModel = this.getItemById(id);
            //存进redis
            redisTemplate.opsForValue().set("item_validate_"+id,itemModel);
            redisTemplate.expire("item_validate_"+id,10, TimeUnit.MINUTES);
        }
        return itemModel;
    }
}
