package sky.Sss.global.openapi.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiExamCaptchaNkeyServiceTest {


    @Autowired
    ApiExamCaptchaNkeyService apiExamCaptchaNkeyService;


    @Test
    public void 키발급_이미지발급() throws JsonProcessingException {

        Map apiExamCaptchaNkey = apiExamCaptchaNkeyService.getApiExamCaptchaNkey();

        System.out.println("apiExamCaptchaNkey = " + apiExamCaptchaNkey.toString());



        String apiExamCaptchaImage = apiExamCaptchaNkeyService.getApiExamCaptchaImage(
            (String) apiExamCaptchaNkey.get("key"));

        System.out.println("apiExamCaptchaImage = " + apiExamCaptchaImage);
    }


/*
    result = {"result":false,"responseTime":15.36}
    result = {"result":true,"responseTime":15.36}
    result = {"result":false,"errorMessage":"Invalid key.","errorCode":"CT001"}
*/
    @Test
    public void 키값인증() throws IOException {
        Map result = apiExamCaptchaNkeyService.getApiExamCaptchaNkeyResult("fr2foLQK2Rlyf6SN", "1ACU9");

        System.out.println("result = " + result.get("result"));

    }

}