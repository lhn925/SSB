package sky.Sss.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import sky.Sss.domain.user.utili.Interceptor.HttpHandshakeInterceptor;
import sky.Sss.domain.user.utili.handler.webSocket.SessionHandler;
import sky.Sss.domain.user.utili.handler.webSocket.WebSocketErrorHandler;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final SessionHandler sessionHandler;
    private final WebSocketErrorHandler webSocketErrorHandler;
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        // 발행자가 /topic 의 경로로 메시지를 주면 구독자들에게 전달
        // 단일 연결 queue
        registry.setApplicationDestinationPrefixes("/app"); // 발행자가 /app 경로로 메시지를 주면
        //가공을 해서 구독자들에게 전달
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 커넥션을 맺는 경로 설정
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/webSocket").setAllowedOriginPatterns("http://localhost:3000").withSockJS();
        registry.setErrorHandler(webSocketErrorHandler).addEndpoint("/webSocket").setAllowedOriginPatterns("http://localhost:3000");
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(sessionHandler);
    }
}
