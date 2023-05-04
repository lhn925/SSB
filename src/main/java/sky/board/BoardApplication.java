package sky.board;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class BoardApplication {

    public static void main (String[] args) {
        SpringApplication.run(BoardApplication.class, args);
        log.info("이건 main 입니다");
        // 여기는 feature2 테스트
        // 여기는 feature1 테스트
        // 이것 또한 롤백테스트입니다

        //없어지면 내책임 아님
    }

}
