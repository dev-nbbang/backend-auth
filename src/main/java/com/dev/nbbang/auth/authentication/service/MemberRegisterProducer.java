package com.dev.nbbang.auth.authentication.service;

import com.dev.nbbang.auth.authentication.dto.request.MemberAdditionalInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberRegisterProducer {
    private final String NBBANG_EXCHANGE = "nbbang.exchange";
    private final String MEMBER_REGISTER_ROUTING_KEY = "member.register.route";
    private final String MEMBER_REGISTER_QUEUE = "member.register.queue";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void sendAdditionalInformation(MemberAdditionalInformation additionalInformation) throws JsonProcessingException {
        log.debug("[MEMBER REGISTER QUEUE] Auth Service -> Member Service");
//        String message = objectMapper.writeValueAsString(additionalInformation);

        log.info("[MEMBER_REGISTER_QUEUE] message : " + additionalInformation);

        rabbitTemplate.convertAndSend(NBBANG_EXCHANGE, MEMBER_REGISTER_ROUTING_KEY, additionalInformation);
    }
}
