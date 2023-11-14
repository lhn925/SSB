package sky.Sss.domain.user.utili.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sky.Sss.domain.user.utili.listener.entitiy.BrowserSession;

@Slf4j
@Component
public class EventListener {


    private static Map<String, BrowserSession> browserSessionMap = new ConcurrentHashMap<>();

    /**
     * 연결시 이벤트
     */
}
