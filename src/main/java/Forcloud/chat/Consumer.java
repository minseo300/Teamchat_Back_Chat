package Forcloud.chat;

//import Forcloud.chat.config.CustomKafkaListenerRegistrar;
import Forcloud.chat.config.ConsumerConfig;
import Forcloud.chat.config.MessageDto;
        import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Consumer {
    private final ConsumerFactory<String,MessageDto> consumerFactory;
    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final SimpMessagingTemplate template;
    private final ConsumerConfig consumerConfig;
    private static Map<String, ConcurrentMessageListenerContainer<String, MessageDto>> consumersMap = new HashMap<>();

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    @PostConstruct
    public void startConsumers() throws ExecutionException, InterruptedException {
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "localhost:9092");


        AdminClient admin = AdminClient.create(prop);
        Set<String> topics=admin.listTopics().names().get();
        for(String topic: topics){
            log.info("[Start consumer] - topic: {}",topic);
            createConsumer(topic);
        }
    }

    public void createConsumer(String topic){
        ConcurrentMessageListenerContainer<String,MessageDto> container=consumerConfig.messageListenerContainer(topic);
        container.start();
        consumersMap.put(topic,container);
    }


}
