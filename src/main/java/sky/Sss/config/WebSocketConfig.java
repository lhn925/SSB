package sky.Sss.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import sky.Sss.global.ws.utili.handler.WebSocketSessionHandler;
import sky.Sss.global.ws.utili.handler.WebSocketErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketSessionHandler webSocketSessionHandler;
    private final WebSocketErrorHandler webSocketErrorHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {

        /**
         * 매핑된 스프링 컨트롤러를 안거치고 브로커에게 직접 접근하겠다는 뜻인데 주로 클라이언트가
         * subscribe를 할때 이 prefix를 사용하는 것으로 보인다. 계속 설명하지만
         * /topic 이라는 prefix를 쓰면 브로커에게 직접 전달되는데
         * 이 경우 브로커가 직접 받아서 subscriber들 관리를 하는 것 같다.
         "topic/.." --> publish-subscribe (1:N)
         "queue/" --> point-to-point (1:1)
         */
        registry.enableSimpleBroker("/topic", "/queue");

        // 매핑된 스프링 컨트롤러를 안거치고 브로커에게 직접 접근하겠다는 뜻인데 주로 클라이언트가
        // 발행자가 /topic 의 경로로 메시지를 주면 구독자들에게 전달
        // 단일 연결 queue

        registry.setApplicationDestinationPrefixes("/app"); // 발행자가 /app 경로로 메시지를 주면
        /**
         * 해당 request는 @messagemapping된 스프링 컨트롤러로 흘러가고 컨트롤러에서 메세지를 수신한 후 여러 작업들을 처리한 후에
         * /topic이라는 prefix를 통해 브로커에게 전달하면 브로커는
         * STOMP MESSAGE 메소드를 이용해서 특정 토픽을 구독하는 구독자들에게 reponse를 보낸다.
         */
        //가공을 해서 구독자들에게 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 커넥션을 맺는 경로 설정
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/webSocket")
            .setAllowedOriginPatterns("http://localhost:3000").withSockJS();
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/webSocket")
            .setAllowedOriginPatterns("http://localhost:3000");

        // 서버에서 전달에 필요한 경로 설정
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/webSocket").setAllowedOrigins("http://localhost:8080").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(webSocketSessionHandler);
    }
}
