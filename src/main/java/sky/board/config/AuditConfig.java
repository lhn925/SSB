package sky.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import sky.board.global.auditor.AuditorAwareImpl;

@Configuration
public class AuditConfig {
    /**
     * 생성자 및 수정자의 아이디나 구별값을 db 컬럼에 저장
     *
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }
}
