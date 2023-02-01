package Forcloud.chat.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ConsumerConfig {

    private final SimpMessagingTemplate template;


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, MessageDto> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, MessageDto> consumerFactory() {
        // 임의로 생성한 객체를 카프카에서 해독하기 위한 하위 4줄
        JsonDeserializer<MessageDto> deserializer = new JsonDeserializer<>(MessageDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);


        return new DefaultKafkaConsumerFactory<>(consumerConfigurations(), new StringDeserializer(), deserializer);
    }

    @Bean
    public Map<String, Object> consumerConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstants.KAFKA_BROKER);
        configurations.put(org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG, KafkaConstants.GROUP_ID);
        configurations.put(org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configurations.put(org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configurations.put(org.apache.kafka.clients.consumer.ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        return configurations;
    }


    public ConcurrentMessageListenerContainer<String, MessageDto> messageListenerContainer(String topic) {

        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(new MyMessageListener());

        JsonDeserializer<MessageDto> deserializer = new JsonDeserializer<>(MessageDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        ConsumerFactory<String, MessageDto> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerConfigurations(),new StringDeserializer(),deserializer);
        ConcurrentMessageListenerContainer<String, MessageDto> listenerContainer = new ConcurrentMessageListenerContainer<>(consumerFactory, containerProperties);
        listenerContainer.setAutoStartup(false);

        return listenerContainer;
    }


    class MyMessageListener implements MessageListener<String, MessageDto> {

        @Override
        public void onMessage(ConsumerRecord<String, MessageDto> data) {
            log.info("MyMessageListener : "+data.value());

            String des="/sub/chat/"+data.value().getRoomId();
            log.info("[Consume] - des: {}",des);
            template.convertAndSend(des,data.value());

            // TODO: 채팅 내용 파일에 저장
        }
    }
}
