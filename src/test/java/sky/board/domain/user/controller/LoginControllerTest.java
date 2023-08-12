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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.service.UserJoinService;

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
        mockMvc.perform(
            post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("userId", "0221325")
                .param("password", "0221325")
                .param("rememberMe", String.valueOf(true))
                .header("User-Agent", "pc")
        );


    }


    @Test
    public void autoLogin() throws Exception {
        Cookie cookie = new Cookie("rememberMe",
            "eb3c49fe-efa6-4898-8799-6c5d45457c26:5adbb752f89427fe3dba");
        MockHttpServletResponse response = new MockHttpServletResponse();
        mockMvc.perform(
            get("/login").contentType(MediaType.TEXT_HTML)
                .cookie(cookie).header("User-Agent", "pc")
        );


    }


}