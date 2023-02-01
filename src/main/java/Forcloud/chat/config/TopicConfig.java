//package Forcloud.chat.config;
//
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.kafka.clients.admin.AdminClient;
//import org.apache.kafka.clients.admin.AdminClientConfig;
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Scope;
//import org.springframework.kafka.config.TopicBuilder;
//import org.springframework.kafka.core.KafkaAdmin;
//import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//import java.util.Set;
//import java.util.concurrent.ExecutionException;
//
//@Component
//@RequiredArgsConstructor
//@Getter
//@Slf4j
//public class TopicConfig {
//    private Map<String, ConcurrentMessageListenerContainer<String, MessageDto>> consumersMap = new HashMap<>();
//    private final ConsumerConfig consumerConfig;
////
////    @PostConstruct
////    public void getTopics() throws ExecutionException, InterruptedException {
////        Properties prop = new Properties();
////        prop.setProperty("bootstrap.servers", "localhost:9092");
////
////        AdminClient admin = AdminClient.create(prop);
////        Set<String> topics=admin.listTopics().names().get();
////        for(String topic: topics){
////            log.info("[Start consumer] - topic: {}",topic);
////            ConcurrentMessageListenerContainer<String,MessageDto> container=consumerConfig.messageListenerContainer(topic);
////            container.start();
////            consumersMap.put(topic,container);
////        }
////    }
//
//}
