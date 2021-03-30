package com.example.ordervalidationservice.Publishing_To_TE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class PublisherController {

    @Autowired
    private RedisTemplate template;

    @Autowired
    private ChannelTopic topic;


    @PostMapping("/publish")
    public String publish(@RequestBody OrderDto order) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String newOrderInString = mapper.writeValueAsString(order);
        template.convertAndSend(topic.getTopic(),newOrderInString);
        return "Event has been published";
    }
}
