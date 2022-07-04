package com.dev.nbbang.auth.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@RefreshScope
public class KafkaProducerConfig {
    private final String host;
    private final String port;

    // 카프라 Bootstrap Server Config 프로퍼티 파일에서 가져오기
    public KafkaProducerConfig(@Value("${kafka.host}") String host, @Value("${kafka.port}") String port) {
        this.host = host;
        this.port = port;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        // 프로퍼티 설정
        System.out.println("host: "+host);
        System.out.println("port: "+port);
        Map<String, Object> properties = new HashMap<>();

        // Bootstrap Server는 일반적으로 9092번 포트를 사용한다.
//        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, host + ":" + port);
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "129.154.222.27" + ":" + "9092");

        // Key, Value Serialize 프로퍼티 설정
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
