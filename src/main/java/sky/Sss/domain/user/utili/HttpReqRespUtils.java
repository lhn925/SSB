package sky.Sss.domain.user.utili;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ip 추출
 */
@Slf4j
public class HttpReqRespUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };

    //    @Async
    public static String getClientIpAddressIfServletRequestExist() {
        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            return "0.0.0.0";
        }
        //RequestContextHolder는 Spring 프레임워크 전 구간에서 HttpServletRequest에 접근할 수 있게 도와주는 구현체
        HttpServletRequest request = getHttpServletRequest();

        String ip = "218.239.21.150";
        // 로컬 구분

        if (!request.getRemoteAddr().equals("127.0.0.1")) {
            for (String header : IP_HEADER_CANDIDATES) {
                String ipFromHeader = request.getHeader(header);

                if (Objects.nonNull(ipFromHeader) && ipFromHeader.length() != 0 && !"unknown".equalsIgnoreCase(
                    ipFromHeader)) {
                    ip = ipFromHeader.split(",")[0];
                }
            }
        }
        return ip;
    }

    public static HttpServletRequest getHttpServletRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        return request;
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }


}