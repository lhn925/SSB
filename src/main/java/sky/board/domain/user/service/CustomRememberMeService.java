package sky.board.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CustomRememberMeService extends TokenBasedRememberMeServices {


    private static final String PARAMETER = "rememberMe";
    @Autowired
    public CustomRememberMeService(
        UserDetailsService userDetailsService) {
        super(PARAMETER, userDetailsService);
        super.setParameter(PARAMETER);
        super.setCookieName(PARAMETER);
    }
    public CustomRememberMeService(UserDetailsService userDetailsService,
        RememberMeTokenAlgorithm encodingAlgorithm) {
        super(PARAMETER, userDetailsService, encodingAlgorithm);
    }

}
