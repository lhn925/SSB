package sky.Sss.domain.track.service;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.track.service.track.TrackService;


@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest
class ServiceTest {


    @Autowired
    TrackService trackService;

    String key;

    @BeforeAll
    void init() {
        System.out.println("trackService.getClass() = " + trackService.getClass());
        key = "안녕하세요";
    }
    
    
    @Test
    public void test1() {

        System.out.println("test1 trackService.getClass() = " + trackService.getClass());
        System.out.println(" test1 key = " + key);
    // given
    
    // when
    
    // then
    
    }

    @Test
    public void test2() {

        System.out.println("test2 trackService.getClass() = " + trackService.getClass());
        System.out.println("test2 key = " + key);
        // given

        // when

        // then

    }

}