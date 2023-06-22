package sky.board;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
public class BoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardApplication.class, args);
        log.info("이건 main 입니다");
        // 여기는 feature2 테스트
        // 여기는 feature1 테스트
        /*1.
            우선 엔티티를 DTO로 변환하는 방법을 선택한다.
            2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
            3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
            4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접
            사용한다.*/
    }

    /**
     *
     * 생성자 및 수정자의 아이디나 구별값을 db 컬럼에 저장
     *
     * @return
     */
    @Bean
    public AuditorAware<String> auditorProvider () {
        return () -> Optional.of(UUID.randomUUID().toString());
    }

}
