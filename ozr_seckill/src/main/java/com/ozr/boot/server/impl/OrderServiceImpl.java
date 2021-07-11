package com.ozr.boot.server.impl;

import com.ozr.boot.dao.OrderDaoMapper;
import com.ozr.boot.dao.SequenceDaoMapper;
import com.ozr.boot.dao.StockLogDaoMapper;
import com.ozr.boot.dataObject.OrderDao;
import com.ozr.boot.dataObject.SequenceDao;
import com.ozr.boot.dataObject.StockLogDao;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.server.ItemService;
import com.ozr.boot.server.OrderService;
import com.ozr.boot.server.UserService;
import com.ozr.boot.server.model.ItemModel;
import com.ozr.boot.server.model.OrderModel;
import com.ozr.boot.server.model.UserModel;
import lombok.SneakyThrows;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author OZR
 * @Date 2021/6/30 13:39
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    SequenceDaoMapper sequenceDaoMapper;
    @Autowired
    OrderDaoMapper orderDaoMapper;
    @Autowired
    StockLogDaoMapper stockLogDaoMapper;



    @Override
    @Transactional
    public OrderModel creatOrder(Integer itemId, Integer userId,Integer promoId, Integer amount,String stockLogId) throws BusinessException {
        //1.校验下的单状态,下单的商品是否存在，数量够不够
//        ItemModel itemModel= itemService.getItemById(itemId);
       //getItemByIdFromCache先读取redis，redis没有在读取数据库，并同步到redis
        ItemModel itemModel = itemService.getItemByIdFromCache(itemId);
        if(itemModel == null){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"物品不存在");
        }

////        UserModel userModel = userService.getUserById(userId);
//        UserModel userModel = userService.getUserByIdFromCache(userId);
//        if(userModel == null){
//            throw  new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户信息不存在");
//        }
//秒杀令牌生成就说明了以上的校验都是通过的



        if(amount <0 && amount > 99){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数量信息不正确");
        }
        //校验活动信息
//        if(promoId != null){
//            //教验活动是不是对应的这个商品
//            if(promoId != itemModel.getPromoModel().getId()){
//                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动不匹配");
//            }else if (itemModel.getPromoModel().getStatus()!=2){
//                //校验活动是否进行中
//                throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动未开始");
//            }
//        }
        //2.落单减存(我们采用)
        //  下单就减库存
        //
        // 或者支付减存
        //支付了再减库存
        //这个减库存只是减了redis的库存
        boolean result = itemService.decreaseStock(itemId, amount);
        if(!result){
            throw new BusinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        //3 订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setAmount(amount);
        orderModel.setItemId(itemId);
        orderModel.setPromoId(promoId);
        orderModel.setUserId(userId);
        //有活动就取活动的价格
        if(promoId !=null){
            orderModel.setItemPrice(itemModel.getPromoModel().getPromItemPrice());
        }else {
            orderModel.setItemPrice(itemModel.getPrice());
        }
        orderModel.setOrderPrice(orderModel.getItemPrice().multiply(new BigDecimal(amount)));
        //生成订单号
        orderModel.setId(generateOrderNO());

        //model->dao
        OrderDao orderDao =  this.convertDaoFromModel(orderModel);
        orderDaoMapper.insertSelective(orderDao);

        //商品销量加1
        itemService.increaseSales(itemId,amount);


        //设置库存流水状态为成功
        StockLogDao stockLogDao = stockLogDaoMapper.selectByPrimaryKey(stockLogId);
        if(stockLogDao == null ){
            throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
        }
        stockLogDao.setStatus(2);
        stockLogDaoMapper.updateByPrimaryKey(stockLogDao);


//        try{
//           int i= 10/0;
//        }catch (Exception e){
//            System.out.println("sdsdsdsd");
//            throw  new BusinessException(EmBusinessError.UNKNOW_ERROR,"....................");
//        }

//        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
//            //这个方法会在最近的@Transactional标记的方法成功return之后才会执行
//            @SneakyThrows
//            @Override
//            public void afterCommit() {
//                /**
//                 * 所有的订单步骤都成功了，才异步减数据库的库存
//                *存在问题，这个订单成功的返回了执行
//                *问题一 如果消息发送失败了，数据库不会删除库存redis也会回补库存，但这笔订单成功了 ，库存却没有减，出事了
//                *问题二 如果下单在扣减redis之后出现了错误，redis减了，下单不能成功，就到不了这儿，数据库库存不会减，但是redis的库存也不能回补
//                *       出现数据不一致的问题
//                */
//                boolean mqResult = itemService.asyncDecreaseStock(itemId, amount);
////                if(!mqResult){
////                    itemService.increaseStock(itemId,amount);
////                    throw new BusinessException(EmBusinessError.MQ_SEND_FAAIL);
////                }
//            }
//        });
        //4.返回前端
        return orderModel;
    }


   @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String generateOrderNO() {
        //订单号定义为16位
        StringBuilder orderNO = new StringBuilder();
        //8位年月日
        LocalDateTime now = LocalDateTime.now();

        String date = now.format(DateTimeFormatter.ISO_DATE).replace("-","");
         orderNO.append(date);

        //6位自增的的数字
        //在数据库里面单独一个表来存储数字，在上面加

        SequenceDao sequenceDao = sequenceDaoMapper.getSequenceByName("order");
        int sequence = sequenceDao.getCurrentValue();
        sequenceDao.setCurrentValue(sequence+sequenceDao.getStep());
        sequenceDaoMapper.updateByPrimaryKeySelective(sequenceDao);
        String No = String.valueOf(sequence);
        for(int i=0;i<6-No.length();i++){
            orderNO.append("0");
        }
        orderNO.append(No);
        //2位分库分表位  //暂时写死
        orderNO.append("00");
       return orderNO.toString();

    }

    private OrderDao convertDaoFromModel(OrderModel orderModel) {
        if(orderModel == null) return  null;
        OrderDao orderDao = new OrderDao();
        BeanUtils.copyProperties(orderModel,orderDao);
        orderDao.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        orderDao.setItemPrice(orderModel.getItemPrice().doubleValue());
        return orderDao;
    }
}
