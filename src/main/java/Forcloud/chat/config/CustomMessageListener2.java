package Forcloud.chat.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class CustomMessageListener2 implements MessageListener<String,MessageDto> {


    @Override
    public void onMessage(ConsumerRecord<String, MessageDto> data) {

    }
}
