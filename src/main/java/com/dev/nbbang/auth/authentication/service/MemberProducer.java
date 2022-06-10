package com.dev.nbbang.auth.authentication.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MemberProducer {
    private final String TOPIC = "new-member-register";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    @Transactional
    public void sendRecommendIdAndOttId(KafkaSendRequest kafkaSendRequest) throws JsonProcessingException {
        log.info("[MemberProducer] Auth Service -> Member Service");
        String sendMessage = objectMapper.writeValueAsString(kafkaSendRequest);

        log.info("[MemberProducer] sendMessage : " + sendMessage);
        try {
            SendResult<String, String> result = kafkaTemplate.send(TOPIC, sendMessage).get();
            log.info("[MemberProducer] After Send Message result : "+ result.getRecordMetadata());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Getter
    @NoArgsConstructor
    static class KafkaSendRequest {
        private String memberId;
        private String recommendMemberId;
        private List<Integer> ottId;

        @Builder
        public KafkaSendRequest(String memberId, String recommendMemberId, List<Integer> ottId) {
            this.memberId = memberId;
            this.recommendMemberId = recommendMemberId;
            this.ottId = ottId;
        }

        public static KafkaSendRequest create(String memberId, String recommendMemberId, List<Integer> ottId) {
            return KafkaSendRequest.builder()
                    .memberId(memberId)
                    .recommendMemberId(recommendMemberId)
                    .ottId(ottId)
                    .build();
        }
    }
}
