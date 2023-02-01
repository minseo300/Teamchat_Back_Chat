package Forcloud.chat;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.simple.parser.JSONParser;
@Service
@Slf4j
public class SchedulerService {
    SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");

    @Scheduled(cron = "0 0 0 * * *")
    public void run() throws IOException, ParseException, java.text.ParseException {
        JSONParser parser=new JSONParser();

        Calendar c=Calendar.getInstance();
        Date today=sdf.parse(sdf.format(c.getTime()));
        File dir=new File("/Users/iminseo/Desktop/chat/src/main/resources/chattings");

        String[] rooms=dir.list((r,name)->name.endsWith(".json"));
//        log.info("room[1]: {}",rooms[1]);
        for(int i=0;i< rooms.length;i++){
            log.info("room[i]: {}",rooms[i]);
            Reader reader=new FileReader("/Users/iminseo/Desktop/chat/src/main/resources/chattings/"+rooms[i]);

            JSONArray jsonArray=(JSONArray) parser.parse(reader);
//            log.info("jsonArray size: {}",jsonArray.size());
            JSONObject first=(JSONObject) jsonArray.get(0);
            String roomNum=String.valueOf(first.get("roomId"));
            String fileName= fn_Yesterday();
            BufferedWriter bw =new BufferedWriter(new FileWriter("/Users/iminseo/Desktop/chat/src/main/resources/chattings/"+roomNum+"-"+ fileName +".txt",true));
            for(int j=0;j< jsonArray.size();j++){
                JSONObject jsonObject=(JSONObject) jsonArray.get(j);
                log.info("jsonObject: {}",jsonObject);
                String dateStr=(String)jsonObject.get("timestamp");
                Date date=sdf.parse(dateStr.substring(0,7));
                log.info("date: {}",date);
                log.info("today: {}",today);
                if(date.compareTo(today)<0){
                    String msg="["+jsonObject.get("nickName")+"] "+ jsonObject.get("msg")+"\n";
                    log.info("msg: {}",msg);
                    bw.write(msg);
                    bw.flush();
                }
                else break;
            }
        }

    }
    public String fn_Yesterday() {

        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, -1);
        String yesterday = sdf.format(cal.getTime());

        return yesterday;
    }
}
