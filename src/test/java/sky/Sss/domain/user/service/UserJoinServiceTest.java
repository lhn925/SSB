package sky.Sss.domain.user.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sky.Sss.domain.user.controller.JoinController;
import sky.Sss.domain.user.dto.join.UserJoinAgreeDto;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.service.join.UserJoinService;


@SpringBootTest
@WebMvcTest(JoinController.class)
class UserJoinServiceTest {



    // 가짜 객체생성
    @MockBean
    UserJoinService userJoinService;

    @Autowired
    private MockMvc mockMvc;


    
    @Test
    public void userJoinAgreeDto() throws Exception {

        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/join/agree"));
        MvcResult mvcResult = resultActions.andReturn();

    }
    
    


    @Test
    void join() throws Exception {
        UserJoinPostDto userJoinDto = new UserJoinPostDto();
/*

        userJoinDto.setEmail("2221312512@daum.net");
        userJoinDto.setUserName("아이유입니1다2");
        userJoinDto.setNotification_enabled(false);
        userJoinDto.setUserId("dlagksmf0171");
        userJoinDto.setPassword("dlagksmf2");
        Long join1 = userJoinService.join(userJoinDto);

        UserJoinDto userJoinDto1 = new UserJoinDto();

        userJoinDto1.setEmail("222112@daum.net");
        userJoinDto1.setUserName("아이입니다2");
        userJoinDto1.setNotification_enabled(false);
        userJoinDto1.setUserId("agksmf071");
        userJoinDto1.setPassword("dlagksmf2");
        Long join2 = userJoinService.join(userJoinDto1);

        UserJoinDto userJoinDto2 = new UserJoinDto();
        userJoinDto2.setEmail("2212512@daum.net");
        userJoinDto2.setUserName("1아이유입니다2");
        userJoinDto2.setNotification_enabled(false);
        userJoinDto2.setUserId("dksmf071");
        userJoinDto2.setPassword("dlagksmf2");
        Long join3 = userJoinService.join(userJoinDto2);
*/

        for (int i = 0; i < 10; i++) {
            UserJoinPostDto userJoinDto4 = new UserJoinPostDto();
            userJoinDto4.setEmail(i + "2512@daum.net");
            userJoinDto4.setUserName(i + "유입니다2");
            userJoinDto4.setUserId(i + "dksmf071");
            userJoinDto4.setPassword(i + "dlagksmf2");
            userJoinService.join(userJoinDto4);
        }
    }

    @Test
    void join2() throws Exception {
        UserJoinPostDto userJoinDto = new UserJoinPostDto();


    }

    @Test
    void checkId() {
    }



}