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
import java.util.*;
import java.util.concurrent.ExecutionException;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;

import org.json.simple.parser.JSONParser;
import org.springframework.web.multipart.MultipartFile;

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

        Reader reader= new FileReader("/home/centos/Backend-chatting/chattings/room."+roomId+".json");
        JSONParser parser=new JSONParser();

        JSONArray jsonArray=(JSONArray) parser.parse(reader);
        for(int i=0;i< jsonArray.size();i++){
            MessageDto dto=new MessageDto();
            JSONObject jsonObject=(JSONObject) jsonArray.get(i);
//            log.info("jsonObject: {}",jsonObject);
            dto.setRoomId((Long)jsonObject.get("roomId"));
            dto.setNickName((String)jsonObject.get("nickName"));
            dto.setMsg((String)jsonObject.get("msg"));
            dto.setTimestamp((String)jsonObject.get("timestamp"));
            dto.setMemberId((Long)jsonObject.get("memberId"));
            dto.setMsgType((String)jsonObject.get("msgType"));
            dto.setOriginalFileName((String)jsonObject.get("originalFileName"));
            if(((String)jsonObject.get("msgType")).equals("file")||((String)jsonObject.get("msgType")).equals("img")){
//                log.info("dto.originalfilename: {}",dto.getOriginalFileName());
                String filepath="/home/centos/Backend-chatting/chattings/"+dto.getRoomId()+"/"+dto.getOriginalFileName();
                File file = new File(filepath);
                byte[] data = new byte[(int) file.length()];
                try (FileInputStream stream = new FileInputStream(file)) {
                    stream.read(data, 0, data.length);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                String extension=dto.getMsg().substring(dto.getMsg().lastIndexOf(".")+1);
                String original= dto.getMsg();
                if(extension.equals("jpg")||extension.equals("jpeg")||extension.equals("png")){
                    String base64data = "data:image/"+extension+";base64,"+Base64.getEncoder().encodeToString(data);
//                    log.info("base64data: {}",base64data);
                    dto.setMsg(base64data);
                }
                else{
                    String base64data = Base64.getEncoder().encodeToString(data);
                    dto.setMsg(base64data);
                }
            }



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

        Reader reader= new FileReader("/home/centos/Backend-chatting/chattings/room."+roomId+".json");
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
            dto.setMsgType((String)jsonObject.get("msgType"));
            dto.setOriginalFileName((String)jsonObject.get("originalFileName"));
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

    // 파일 전송
    @PostMapping("/chat/file")
    public BaseResponse<PostFileResponse> uploadFile(@RequestParam("file")MultipartFile file, @RequestParam("memberId")Long memberId,@RequestParam(name="roomId")Long roomId, @RequestParam("timestamp")String timestamp, @RequestParam("nickName")String nickName) throws IOException {
        String path="/home/centos/Backend-chatting/chattings/"+roomId;
        log.info("file: {}",file.getOriginalFilename());
        log.info("roomId: {}",roomId);
        log.info("memberId: {}",memberId);

        String fileId = (new Date().getTime()) + "" + (new Random().ints(1000, 9999).findAny().getAsInt())+"_"+file.getOriginalFilename(); // 현재 날짜와 랜덤 정수값으로 새로운 파일명 만들기
        String originName = file.getOriginalFilename(); // ex) 파일.jpg
        String fileExtension = originName.substring(originName.lastIndexOf(".") + 1); // ex) jpg
        log.info("fileExtension: {}",fileExtension);
        originName = originName.substring(0, originName.lastIndexOf(".")); // ex) 파일
        long fileSize = file.getSize(); // 파일 사이즈

        File fileSave = new File(path, fileId); // ex) fileId.jpg
        if(!fileSave.exists()) { // 폴더가 없을 경우 폴더 만들기
            fileSave.mkdirs();
        }

        file.transferTo(fileSave); // fileSave의 형태로 파일 저장

        PostFileResponse response=new PostFileResponse(fileId,roomId,memberId,timestamp,nickName);

        return new BaseResponse<>(response);

    }

    @GetMapping("/chat/file/{roomId}")
    public BaseResponse<GetFilesResponse> getFiles(@PathVariable(name="roomId")Long roomId){
        File dir=new File("/home/centos/Backend-chatting/chattings/"+roomId);
        List<GetMinute> minuteList=new ArrayList<>();
        List<GetFile> fileList=new ArrayList<>();
        GetFilesResponse response=new GetFilesResponse();
        String[] files=dir.list();
        log.info("files: {}",files);
        for(int i=0;i<files.length;i++){
            String filepath="/home/centos/Backend-chatting/chattings/"+roomId+"/"+files[i];
            File file = new File(filepath);
            byte[] data = new byte[(int) file.length()];
            try (FileInputStream stream = new FileInputStream(file)) {
                stream.read(data, 0, data.length);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            String base64data = Base64.getEncoder().encodeToString(data);
            if(files[i].startsWith(roomId+"-")){
                GetMinute minute=new GetMinute();
                minute.setName(files[i]);
                minute.setRoomId(roomId);
                minute.setBase64(base64data);
                minuteList.add(minute);
            }
            else{
                GetFile uploadedFile=new GetFile();
                uploadedFile.setName(files[i]);
                uploadedFile.setBase64(base64data);
                uploadedFile.setRoomId(roomId);
                fileList.add(uploadedFile);
            }
        }
        response.setFilesList(fileList);
        response.setMinutesList(minuteList);

        return new BaseResponse<>(response);
    }
}
