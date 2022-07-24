package com.dev.nbbang.auth.global.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQProducerConfig {
    private final String NBBANG_EXCHANGE = "nbbang.exchange";  // 메세지는 항상 Exchange를 통과해 큐로 전달
    private final String MEMBER_REGISTER_ROUTING_KEY = "member.register.route";
    private final String MEMBER_REGISTER_QUEUE = "member.register.queue";

    @Bean
    public Queue queue() {
        return new Queue(MEMBER_REGISTER_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(NBBANG_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue queue , DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(MEMBER_REGISTER_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        return rabbitTemplate;
    }
}
