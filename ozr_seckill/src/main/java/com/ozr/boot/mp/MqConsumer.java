package com.ozr.boot.mp;

import com.alibaba.fastjson.JSON;
import com.ozr.boot.dao.ItemStockMapper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @Author OZR
 * @Date 2021/7/7 15:29
 */
@Component
public class MqConsumer {
    private DefaultMQPushConsumer consumer;
    @Value("${mq.nameserver.addr}")
    private String nameAddr;
    @Value("${mq.topicname}")
    private String topicName;

    @Autowired
    ItemStockMapper itemStockMapper;

    @PostConstruct
    public void init() throws MQClientException {
        consumer = new DefaultMQPushConsumer("stock_consumer_group");
        consumer.setNamesrvAddr(nameAddr);
        //监听那些消息,
        //.subscribe(topicName,"*"); 表示 topicName这个topic里面的所有的消息
        consumer.subscribe(topicName,"*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> list,
                    ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                //获取消息
                //为啥是第一条？？？？
                Message message = list.get(0);
                String jsonString = new String(message.getBody());
                Map<String,Object> map = JSON.parseObject(jsonString, Map.class);
                Integer itemId =(Integer) map.get("itemId");
                Integer amount = (Integer) map.get("amount");
                //对数据库进行操作
                itemStockMapper.decreaseStock(itemId,amount);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
    }
}
