package sky.Sss.domain.user.utili.Interceptor;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

/**
 * 보류
 */
@Slf4j
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    /**
     *
     *
     * 웹 소켓이 연결이 되었을 때 연결 요청에 대한 전처리 및 후처리를 담당하는 핸드셰이크 인터셉터를 만들어 요청의 세션 정보를 같이 넘겨줌
     *
     *
     * @param request the current request
     * @param response the current response
     * @param wsHandler the target WebSocket handler
     * @param attributes the attributes from the HTTP handshake to associate with the WebSocket
     * session; the provided attributes are copied, the original map is not used.
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession();

            servletRequest.getHeaders();
            log.info("session.getId() = {}", session.getId());
            attributes.put("sessionId", session.getId());
        }
        return true;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
        Exception exception) {

    }
}
