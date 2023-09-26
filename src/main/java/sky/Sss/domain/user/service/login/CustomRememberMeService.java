package sky.Sss.domain.user.service.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;
import sky.Sss.domain.user.model.RememberCookie;

@Slf4j
@Service
public class CustomRememberMeService extends TokenBasedRememberMeServices {


    @Autowired
    public CustomRememberMeService(
        UserDetailsService userDetailsService) {
        super(RememberCookie.KEY.getValue(), userDetailsService);
        super.setParameter(RememberCookie.KEY.getValue());
        super.setCookieName(RememberCookie.KEY.getValue());
    }
    public CustomRememberMeService(UserDetailsService userDetailsService,
        RememberMeTokenAlgorithm encodingAlgorithm) {
        super(RememberCookie.KEY.getValue(), userDetailsService, encodingAlgorithm);
    }

}
