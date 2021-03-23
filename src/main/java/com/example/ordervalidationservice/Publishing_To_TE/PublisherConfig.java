package com.example.ordervalidationservice.Publishing_To_TE;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericToStringSerializer;


@Configuration
public class PublisherConfig {


    @Bean
    public JedisConnectionFactory connectionFactory(){
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<String,Object> template(){
        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        return template;
    }

    @Bean
    public ChannelTopic topic(){
        return new ChannelTopic("pubsub-with-elvis");
    }
}
