package Forcloud.chat;

import Forcloud.chat.config.ConsumerConfig;
import Forcloud.chat.config.MessageDto;
//import Forcloud.chat.config.TopicConfig;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.aspectj.bridge.Message;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.Buffer;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class Producer {

    private final KafkaTemplate<String, MessageDto> kafkaTemplate;
    private final BeanFactory beanFactory;
    private final Consumer consumer;
    public void send(String topic, MessageDto messageDto) throws ExecutionException, InterruptedException, IOException, ParseException {
        log.info("[Producer] - topic: {}",topic);
        // 토픽 존재 확인
        Properties prop = new Properties();
        prop.put("bootstrap.servers", "localhost:9092");

        AdminClient admin = AdminClient.create(prop);
        boolean topicExists = admin.listTopics().names().get().
                stream().anyMatch(topicName -> topicName.equalsIgnoreCase(topic));
        log.info("topic exists: {}",topicExists);

        if (!topicExists) { // 토픽이 존재하지 않으면 토픽 생성
            log.info("[Producer]-TOPIC DOESN'T EXIST");
            try (final AdminClient adminClient = AdminClient.create(prop)) {
                try {
                    // Define topic - topic, partitions,replicas
                    NewTopic newTopic = new NewTopic(topic, 1, (short) 1);
                    // Create topic, which is async call.
                    final CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));

                    // Since the call is Async, Lets wait for it to complete.
                    createTopicsResult.values().get(topic).get();

                    // create consumer for the topic
                    consumer.createConsumer(topic);

                    log.info("[Producer]-created and started kafka consumer for topic {}", topic);

                } catch (InterruptedException | ExecutionException e) {
                    if (!(e.getCause() instanceof TopicExistsException))
                        throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        log.info("topic : " + topic);
        log.info("[SEND] roomId: {}, memberId: {}, msg: {}, timestamp: {}, nickname: {}, msgType: {}",
                messageDto.getRoomId(), messageDto.getMemberId(), messageDto.getMsg(), messageDto.getTimestamp(),messageDto.getNickName(), messageDto.getMsgType());

        // 파일인 경우 MessageDto.msg를 base64로 변환
        if(messageDto.getMsgType().equals("file")){
//            log.info("it is file");
            String filepath="/home/centos/Backend-chatting/chattings/"+messageDto.getRoomId()+"/"+messageDto.getMsg();
            File file = new File(filepath);
            byte[] data = new byte[(int) file.length()];
            try (FileInputStream stream = new FileInputStream(file)) {
                stream.read(data, 0, data.length);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String extension=messageDto.getMsg().substring(messageDto.getMsg().lastIndexOf(".")+1);
            String original= messageDto.getMsg();
            if(extension.equals("jpg")||extension.equals("jpeg")||extension.equals("png")){
                String base64data = "data:image/"+extension+";base64,"+Base64.getEncoder().encodeToString(data);
                messageDto.setMsg(base64data);
                messageDto.setMsgType("img");
            }
            else{
                String base64data = Base64.getEncoder().encodeToString(data);
                messageDto.setMsg(base64data);
                messageDto.setMsgType("file");
            }
//            log.info("original: {}",original.split("_")[1]);
            messageDto.setOriginalFileName(original);
        }
        else log.info("it is not file");

        kafkaTemplate.send(topic, messageDto);
        saveMessage(topic,messageDto);
    }

    public void saveMessage(String topic,MessageDto messageDto) throws IOException, ParseException {
        JSONParser parser=new JSONParser();
        File file=new File("/home/centos/Backend-chatting/chattings/"+topic+".json");
        JSONArray jsonArray=new JSONArray();
        String jsonStr = null;
        JSONObject data = new JSONObject();

        data.put("timestamp", messageDto.getTimestamp());
        data.put("nickName", messageDto.getNickName());
        data.put("memberId", messageDto.getMemberId());
        data.put("roomId", messageDto.getRoomId());
        data.put("msgType",messageDto.getMsgType());
        if(messageDto.getMsgType().equals("file")||messageDto.getMsgType().equals("img")){
            data.put("msg",messageDto.getOriginalFileName());
        }
        else data.put("msg", messageDto.getMsg());
        data.put("originalFileName",messageDto.getOriginalFileName());

        if(file.exists()) {
            Reader reader=new FileReader("/home/centos/Backend-chatting/chattings/" + topic + ".json");

//            JSONObject jsonObject=(JSONObject) parser.parse(reader);
//            log.info("jsonObject: {}",jsonObject);
            jsonArray=(JSONArray) parser.parse(reader);
//            log.info("jsonArray: {}",jsonArray);
            jsonArray.add(data);
//            log.info("after add data to jsonArray: {}",jsonArray);

        }
        else{
            JSONObject object=new JSONObject();
            jsonArray.add(data);
            object.put("messages",jsonArray);
        }
        jsonStr= jsonArray.toJSONString();
        BufferedWriter bw=new BufferedWriter(new FileWriter("/home/centos/Backend-chatting/chattings/"+topic+".json",false));
        bw.write(jsonStr);
        bw.flush();



        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        String msg="time:"+messageDto.getTimestamp()+" nickname:"+messageDto.getNickName()+" memberId:"+messageDto.getMemberId()+" roomId:"+messageDto.getRoomId()+" ###"+messageDto.getMsg()+"###\n";
//
//        BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/iminseo/Desktop/chat/src/main/resources/chattings/"+topic+".txt",true));
//        bw.write(msg);
//        bw.flush();

    }

}