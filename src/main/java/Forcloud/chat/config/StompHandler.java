package Forcloud.chat.config;

import Forcloud.chat.Consumer;
import Forcloud.chat.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompHandler extends ChannelInterceptorAdapter {
//    private final Logger logger = getLogger(this.getClass());

//    private final Producer producer;
    public void postSend(Message message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        switch (accessor.getCommand()) {
            case SEND:
//                log.info("SEND Message: {}",message);
            case SUBSCRIBE:
                log.info("SUBSCRIBE Message: {}",message);
                break;
            case CONNECT:
                log.info("CONNECT Message: {}",message);
                // 유저가 Websocket으로 connect()를 한 뒤 호출됨
                break;
            case DISCONNECT:
                // 유저가 Websocket으로 disconnect() 를 한 뒤 호출됨 or 세션이 끊어졌을 때 발생함(페이지 이동~ 브라우저 닫기 등)
                break;


            default:
                break;
        }

    }
}
