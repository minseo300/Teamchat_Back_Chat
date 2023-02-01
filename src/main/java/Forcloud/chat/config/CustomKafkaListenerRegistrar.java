//package Forcloud.chat.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.config.KafkaListenerContainerFactory;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class CustomKafkaListenerRegistrar implements InitializingBean {
//
//    private final CustomKafkaListenerProperties customKafkaListenerProperties;
//
//    private final BeanFactory beanFactory;
//
//    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//    private final KafkaListenerContainerFactory kafkaListenerContainerFactory;
//
//    @Override
//    public void afterPropertiesSet() {
//        customKafkaListenerProperties.getListeners()
//                .forEach(this::registerCustomKafkaListener);
//    }
//    public void registerCustomKafkaListener(String name, CustomKafkaListenerProperty customKafkaListenerProperty) {
//        this.registerCustomKafkaListener(name, customKafkaListenerProperty, false);
//    }
//    @SneakyThrows
//    public void registerCustomKafkaListener(String name, CustomKafkaListenerProperty customKafkaListenerProperty,
//                                            boolean startImmediately) {
//        log.info("[registerCustomKafkaListener]-name: {}",name);
//        String listenerClass = String.join(".", CustomKafkaListenerRegistrar.class.getPackageName(),
//                customKafkaListenerProperty.getListenerClass());
//        CustomMessageListener customMessageListener =
//                (CustomMessageListener) beanFactory.getBean(Class.forName(listenerClass));
//        kafkaListenerEndpointRegistry.registerListenerContainer(
//                customMessageListener.createKafkaListenerEndpoint(name, customKafkaListenerProperty.getTopic()),
//                kafkaListenerContainerFactory, startImmediately);
//    }
//}
