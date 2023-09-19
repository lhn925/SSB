package sky.board.domain.user.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import sky.board.domain.user.service.join.UserJoinService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    LoginController loginController;
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserJoinService userJoinService;

    @PersistenceContext
    private EntityManager em;

    @Test
    public void login() throws Exception {

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession();



        for (int i=0; i< 50; i++) {
            mockMvc.perform(
                post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("userId", "lim222")
                    .param("password", "dlagksmf2")
                    .param("rememberMe", String.valueOf(true))
                    .header("User-Agent", "pc")
            );
        }



    }


    @Test
    public void autoLogin() throws Exception {
        mockMvc.perform(
            get("/").contentType(MediaType.TEXT_HTML)
        );


    }


}