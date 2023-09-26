package sky.Sss.domain.user.utili;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
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

    public static String getClientIpAddressIfServletRequestExist() {

        if (Objects.isNull(RequestContextHolder.getRequestAttributes())) {
            return "0.0.0.0";
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        for (String header: IP_HEADER_CANDIDATES) {
            String ipFromHeader = request.getHeader(header);
            if (Objects.nonNull(ipFromHeader) && ipFromHeader.length() != 0 && !"unknown".equalsIgnoreCase(ipFromHeader)) {
                String ip = ipFromHeader.split(",")[0];
                log.info("getClientIpAddressIfServletRequestExist = {}", ip);
                return ip;
            }
        }

        return "218.239.21.150";
//        return request.getRemoteAddr();
    }



}