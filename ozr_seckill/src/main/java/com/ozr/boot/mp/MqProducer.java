package com.ozr.boot.mp;


import com.alibaba.fastjson.JSON;
import com.ozr.boot.dao.StockLogDaoMapper;
import com.ozr.boot.dataObject.StockLogDao;
import com.ozr.boot.error.BusinessException;
import com.ozr.boot.error.EmBusinessError;
import com.ozr.boot.server.OrderService;
import lombok.SneakyThrows;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author OZR
 * @Date 2021/7/7 15:29
 */
@Component
public class MqProducer {
    @Autowired
    OrderService  orderService;

    private DefaultMQProducer producer;
    private TransactionMQProducer transactionMQProducer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    StockLogDaoMapper stockLogDaoMapper;
    @Autowired
    RedisTemplate redisTemplate;


    @PostConstruct
    public  void init() throws MQClientException {
        //做MQProducer的初始化
        producer=  new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(nameAddr);
        producer.start();
        transactionMQProducer = new TransactionMQProducer("transaction_producer_group");
        transactionMQProducer.setNamesrvAddr(nameAddr);
        transactionMQProducer.start();
        transactionMQProducer.setTransactionListener(new TransactionListener() {

            @SneakyThrows
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                //这里才是真正的执行交易的地方
                Map argsMap = (Map)args;
                Integer itemId=(Integer) argsMap.get("itemId");
                Integer userId =(Integer) argsMap.get("userId");
                Integer promoId =(Integer) argsMap.get("promoId");
                Integer amount=(Integer)  argsMap.get("amount");
                String stockLogId= (String) argsMap.get("stockLogId");
                try {
                    orderService.creatOrder(itemId, userId,promoId, amount,stockLogId);
                    //creatOrder里面只考虑redis里面的库存，不在关系数据库里面的数据能不能减掉，这个就交给了消息中间件来做
                    /**
                     * 如果这个方法没有抛出异常（BusinessException），但是也没有成功，
                     * 假如执行完订单操作后，会出现问题，那么就不会返回一个明确的状态，
                     * 则LocalTransactionState的状态将会是UNKNOW，
                     * UNKNOW就会定期的去调用checkLocalTransaction这个方法
                     * */
                } catch (BusinessException e) {
                    e.printStackTrace();
                    StockLogDao stockLogDao = stockLogDaoMapper.selectByPrimaryKey(stockLogId);
                    if(stockLogDao == null){
                        throw new BusinessException(EmBusinessError.UNKNOW_ERROR);
                    }
                    stockLogDao.setStatus(3);
                    stockLogDaoMapper.updateByPrimaryKey(stockLogDao);
                    //既然会回滚就把redis的库存加回去
                    redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount);
                    //回滚
                    return  LocalTransactionState.ROLLBACK_MESSAGE;
                }
                     //将消息状态改为commit
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt message) {
                /**
                 *   根据库存是否扣减成功，来判断返回具体的状态还是继续的UNKNOW
                 *   假如消息中间件10分钟都没收到这条消息的明确状态，肯定会回调这个方法。
                 *   就会去判断库存到底扣减成功了没？？？
                 */
                //这个和consumer是一样的，这儿也可以拿到producer发送的消息
                String jsonString = new String(message.getBody());
                Map<String,Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId =(Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                String stockLogId = (String) map.get("stockLogId");
                //就需要根据这个去判断库存是不是扣减成功了
                //根据库存流水来判断
                StockLogDao stockLogDao = stockLogDaoMapper.selectByPrimaryKey(stockLogId);
                if(stockLogDao == null){
                    return LocalTransactionState.UNKNOW;
                }
                if(stockLogDao.getStatus() == 1){
                    return LocalTransactionState.UNKNOW;
                }else if(stockLogDao.getStatus() == 2){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }else{
                    return  LocalTransactionState.ROLLBACK_MESSAGE;
                }
            }
        });
    }
    //扣减库存的消息  事务性
    public boolean transactionAsyncReduceStock(Integer itemId, Integer userId,Integer promoId, Integer amount,String stockLogId){
        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId",itemId);
        bodyMap.put("amount",amount);
        bodyMap.put("stockLogId",stockLogId);
        Map<String,Object> argsMap = new HashMap<>();
        argsMap.put("itemId",itemId);
        argsMap.put("amount",amount);
        argsMap.put("userId",userId);
        argsMap.put("promoId",promoId);
        argsMap.put("stockLogId",stockLogId);
        Message message = new Message(topicName,"increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));

        TransactionSendResult transactionSendResult = null;
        try {
            transactionSendResult = transactionMQProducer.sendMessageInTransaction(message, argsMap);
        } catch (MQClientException e) {
            e.printStackTrace();
            return false;
        }
        if (transactionSendResult.getLocalTransactionState() == LocalTransactionState.ROLLBACK_MESSAGE){
            return  false;
        }else if(transactionSendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE){
            return true;
        }else{
            return false;
        }
    }
    //扣减库存的消息  非事务性
    public boolean asyncReduceStock(Integer itemId,Integer amount){
        Map<String,Object> bodyMap = new HashMap<>();
        bodyMap.put("itemId",itemId);
        bodyMap.put("amount",amount);
        Message message = new Message(topicName,"increase",
                JSON.toJSON(bodyMap).toString().getBytes(Charset.forName("UTF-8")));
        try {
            producer.send(message);
        } catch (MQClientException e) {
            System.out.println(e);
            return false;
        } catch (RemotingException e) {
            System.out.println(e);
            return false;
        } catch (MQBrokerException e) {
            System.out.println(e);
            return false;
        } catch (InterruptedException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
