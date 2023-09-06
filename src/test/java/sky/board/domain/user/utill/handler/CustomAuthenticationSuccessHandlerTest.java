package sky.board.domain.user.utill.handler;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class CustomAuthenticationSuccessHandlerTest {


    @Test
    public void test() {

/*        HashMap<String, Object> session = new HashMap();
        JSONObject jo1 = new JSONObject();
        session.put("login", jo1);

        JSONObject jsonObject = new JSONObject();

        UserAccount hi = UserAccount.builder()
            .password("1234")
            .username("hi").build();

        jsonObject.put("userDetail",hi);
        jo1.put("SESss111", jsonObject);

        UserAccount h2 = UserAccount.builder()
            .password("1235")
            .username("hi").build();

*//*
        session.toString() = {"SESss111":
        {"userDetail":sky.board.domain.user.dto.login.UserAccount [Username=hi, Password=[PROTECTED], Enabled=true, url=null, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=null]}}
        *//*
        System.out.println("session.toString() = " + session.get("login"));*/


    }


}