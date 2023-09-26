package sky.Sss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableJpaAuditing
@EnableJpaRepositories
/**
 * DEFAULT : 가장 기본으로 모든 Repository를 즉시 인스턴스화 시킵니다. Repository가 많은 경우 스프링 시작 시간이 지연되는 단점이 있습니다.
 * LAZY : 모든 Repository를 프록시로 만들어두고, 처음 사용할 때 실제 인스턴스로 만들어 줍니다.
 * DEFERRED : 기본적으로 LAZY와 동일하지만 비동기적으로 작업하고, ContextRefreshedEvent에 의해 Repository가 초기화 되어 검증되도록 진행합니다.
 */
@SpringBootApplication
@EnableScheduling
public class SssApplication {

    public static void main(String[] args) {
        SpringApplication.run(SssApplication.class, args);
        log.info("이건 main 입니다");
        /*1.
            우선 엔티티를 DTO로 변환하는 방법을 선택한다.
            2. 필요하면 페치 조인으로 성능을 최적화 한다. 대부분의 성능 이슈가 해결된다.
            3. 그래도 안되면 DTO로 직접 조회하는 방법을 사용한다.
            4. 최후의 방법은 JPA가 제공하는 네이티브 SQL이나 스프링 JDBC Template을 사용해서 SQL을 직접
            사용한다.*/
    }



}
