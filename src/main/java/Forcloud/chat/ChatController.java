package Forcloud.chat;

import Forcloud.chat.config.BaseResponse;
import Forcloud.chat.config.CustomKafkaListenerProperty;
//import Forcloud.chat.config.CustomKafkaListenerRegistrar;
import Forcloud.chat.config.MessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final Producer producer;


//    Client가 SEND할 수 있는 경로
//    stompConfig에서 설정한 applicationDestinationPrefixes와 @MessageMapping 경로가 병합됨
//    "/pub/chat/enter"
    // TODO: 입장 메세지 전송 변경 필요
    @MessageMapping(value = "chat.enter.{roomId}")
    public void enter(MessageDto message) throws ExecutionException, InterruptedException, IOException, ParseException {
        // TODO: 우선은 입장하는 순간 토픽이 생성되게 구현, 추후에 게시글 등록 시 바로 채팅방이 생성되며 해당 채팅방에 대한 토픽 생성되게 구현
        log.info("[ChatController] - message: {}",message);
        String topic="room."+message.getRoomId();
        producer.send(topic, message);
    }


    @MessageMapping(value = "chat.message.{roomId}")
    public void message(MessageDto message,@DestinationVariable String roomId) throws ExecutionException, InterruptedException, IOException, ParseException {
        log.info("[ChatController] - message: {}",message);
        String topic="room."+message.getRoomId();
        producer.send(topic, message);
    }


    /**
     * 채팅방 입장 시 채팅 이력 조회
     * @param roomId
     * @return
     * @throws IOException
     */
    @GetMapping("/chat/{roomId}")
    public BaseResponse<List<MessageDto>> getChattingList(@PathVariable(name="roomId") Long roomId) throws IOException, ParseException {
        log.info("getChattingList - roomId: {}",roomId);
        List<MessageDto> response=new ArrayList<>();

        Reader reader= new FileReader("/Users/iminseo/Desktop/chat/src/main/resources/chattings/room."+roomId+".json");
        JSONParser parser=new JSONParser();

        JSONArray jsonArray=(JSONArray) parser.parse(reader);
        for(int i=0;i< jsonArray.size();i++){
            MessageDto dto=new MessageDto();
            JSONObject jsonObject=(JSONObject) jsonArray.get(i);
            log.info("jsonObject: {}",jsonObject);
            dto.setRoomId((Long)jsonObject.get("roomId"));
            dto.setNickName((String)jsonObject.get("nickName"));
            dto.setMsg((String)jsonObject.get("msg"));
            dto.setTimestamp((String)jsonObject.get("timestamp"));
            dto.setMemberId((Long)jsonObject.get("memberId"));
            log.info("dto timestamp: {}",dto.getTimestamp());
            response.add(dto);
        }

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
         //파일 읽기
//        BufferedReader br = new BufferedReader(new FileReader("/Users/iminseo/Desktop/chat/src/main/resources/chattings/room."+roomId+".txt"));
//        List<MessageDto> response=new ArrayList<>();
//        String line ="";
//        //한 줄 씩 읽은 내용 쓰기
//        while( (line=br.readLine()) !=null ) {
//            // TODO: 이렇게 하면 메세지가 띄어쓰기 있으면 잘려버림
//
//            int indexOfStart=line.indexOf("###");
////            log.info("indexOfStart: {}",indexOfStart);
//            int indexOfLast=line.lastIndexOf("###");
//            String[] infos=line.substring(0,indexOfStart-1).split(" ");
//
//            MessageDto msg=new MessageDto();
//            msg.setMsg(line.substring(indexOfStart+3,indexOfLast));
//            msg.setTimestamp(infos[0].split(":")[1]);
//            msg.setNickName(infos[1].split(":")[1]);
//            msg.setMemberId(Long.parseLong(infos[2].split(":")[1]));
//            msg.setRoomId(Long.parseLong(infos[3].split(":")[1]));
//
//            response.add(msg);
//
//        }
        return new BaseResponse<>(response);
    }

    @GetMapping("/chat/chatting/{roomId}")
    public BaseResponse<GetChattingsResponse> getChattingListItemInfo(@PathVariable(name="roomId") Long roomId) throws IOException, ParseException {
        GetChattingsResponse response=new GetChattingsResponse();
        MessageDto dto=new MessageDto();

        Reader reader= new FileReader("/Users/iminseo/Desktop/chat/src/main/resources/chattings/room."+roomId+".json");
        JSONParser parser=new JSONParser();
//        JSONObject obj= (JSONObject) parser.parse(reader);
//        log.info("obj: {}",obj);
        JSONArray jsonArray=(JSONArray) parser.parse(reader);
        log.info("jsonArray: {}",jsonArray);
        for(int i=0;i< jsonArray.size();i++){
            JSONObject jsonObject=(JSONObject) jsonArray.get(i);
            log.info("jsonObject: {}",jsonObject);
            dto.setRoomId((Long)jsonObject.get("roomId"));
            dto.setNickName((String)jsonObject.get("nickName"));
            dto.setMsg((String)jsonObject.get("msg"));
            dto.setTimestamp((String)jsonObject.get("timestamp"));
            dto.setMemberId((Long)jsonObject.get("memberId"));
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        BufferedReader br = new BufferedReader(new FileReader("/Users/iminseo/Desktop/chat/src/main/resources/chattings/room."+roomId+".txt"));
//        String line ="";
//        Long num=0L;
//        GetChattingsResponse response=new GetChattingsResponse();
//        MessageDto dto=new MessageDto();
//        MessageDto msg=new MessageDto();
//        //한 줄 씩 읽은 내용 쓰기
//        while( (line=br.readLine()) !=null ) {
////            log.info("line: {}",line);
//            num++;
//            int indexOfStart=line.indexOf("###");
////            log.info("indexOfStart: {}",indexOfStart);
//            int indexOfLast=line.lastIndexOf("###");
//            String[] infos=line.substring(0,indexOfStart-1).split(" ");
//
//
//            msg.setMsg(line.substring(indexOfStart+3,indexOfLast));
//            msg.setTimestamp(infos[0].split(":")[1]);
//            msg.setNickName(infos[1].split(":")[1]);
//            msg.setMemberId(Long.parseLong(infos[2].split(":")[1]));
//            msg.setRoomId(Long.parseLong(infos[3].split(":")[1]));
//
//        }
//        log.info("room{} count chattings: {}",roomId,num);
        response.setNumber(Long.valueOf(jsonArray.size()));
        response.setLast(dto);
        log.info("response.last: {}",response.getLast());
        return new BaseResponse<>(response);
    }
}
