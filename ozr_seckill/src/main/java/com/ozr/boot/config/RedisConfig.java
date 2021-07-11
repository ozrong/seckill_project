package com.ozr.boot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.ozr.boot.serializer.JodaDateTimeJsonDeSerializer;
import com.ozr.boot.serializer.JodaDateTiomeJsonSerializer;
import org.joda.time.DateTime;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Component;

/**
 * @Author OZR
 * @Date 2021/7/4 20:26
 */
@Component
///maxInactiveIntervalInSeconds = 3600 让session保留3600秒 默认是30分钟（1800秒）
//maxInactiveIntervalInSeconds: 设置 Session 失效时间，使用 Redis Session 之后，
// 原 Spring Boot 的 server.session.timeout 属性不再生效
//经过上面的配置后，Session调用就会自动去Redis存取。另外，想要达到Session共享的目的，只需要在其他的系统上做同样的配置即可
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 3600)
public class RedisConfig {

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //改掉key的序列化方式
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        //value的序列方式 json
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //定制DateTime数据的序列化方法
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        //DateTime使用JodaDateTiomeJsonSerializer序列化
        simpleModule.addSerializer(DateTime.class,new JodaDateTiomeJsonSerializer());
        ////DateTime使用JodaDateTimeJsonDeSerializer反序列化
        simpleModule.addDeserializer(DateTime.class,new JodaDateTimeJsonDeSerializer());
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(simpleModule);

        //指定的规则放入到jackson2JsonRedisSerializer中才有效
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

        return redisTemplate;
    }


}
