package com.dev.nbbang.auth.global.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQProducerConfig {
    private final String NBBANG_EXCHANGE = "nbbang.exchange";  // 메세지는 항상 Exchange를 통과해 큐로 전달
    private final String MEMBER_REGISTER_ROUTING_KEY = "member.register.route";
    private final String MEMBER_REGISTER_QUEUE = "member.register.queue";

    @Bean
    public List<Declarable> exchangeBinding() {
        Queue memberRegisterQueue = new Queue(MEMBER_REGISTER_QUEUE, true);

        DirectExchange directExchange = new DirectExchange(NBBANG_EXCHANGE);

        return List.of(memberRegisterQueue, directExchange, BindingBuilder.bind(memberRegisterQueue).to(directExchange).with(MEMBER_REGISTER_ROUTING_KEY));
    }
}
