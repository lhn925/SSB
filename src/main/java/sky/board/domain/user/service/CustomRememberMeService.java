package sky.board.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;
import sky.board.domain.user.model.RememberCookie;

@Slf4j
@Service
public class CustomRememberMeService extends TokenBasedRememberMeServices {


    @Autowired
    public CustomRememberMeService(
        UserDetailsService userDetailsService) {
        super(RememberCookie.NAME.getValue(), userDetailsService);
        super.setParameter(RememberCookie.NAME.getValue());
        super.setCookieName(RememberCookie.NAME.getValue());
    }
    public CustomRememberMeService(UserDetailsService userDetailsService,
        RememberMeTokenAlgorithm encodingAlgorithm) {
        super(RememberCookie.NAME.getValue(), userDetailsService, encodingAlgorithm);
    }

}
