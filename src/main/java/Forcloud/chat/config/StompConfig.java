package Forcloud.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final StompHandler stompHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat") // 웹소켓 서버의 엔드포인트
                .setAllowedOrigins("http://210.109.61.179:3000") // cors 오류 방지 TODO: origin 변경
                .withSockJS();
//        registry.addEndpoint("/stomp/chat")
//                .setAllowedOriginPatterns("http://*.*.*.*:8081", "http://*:8081") //안해도 무관
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        // prefix로 붙은 메시지들은 @MessageMapping이 붙은 method로 바운드된다.
        // 클라이언트로부터 메시지를 받을 api의 prefix를 설정함 -> publish
        registry.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        registry.setApplicationDestinationPrefixes("/pub");

        //registry.enableSimpleBroker("/sub");
        // topic - 1:N, queue - 1:1 -> prefix로 붙은 destination의 클라이언트에게 메세지 전송
        // 메모리 기반 메시지 브로커가 해당 api를 구독하고 있는 클라이언트에게 메시지를 전달함 -> to subscriber
//        registry.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue");
        registry.enableSimpleBroker("/sub");
    }

    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors((ChannelInterceptor) stompHandler);
    }

}
