package sky.board.domain.user.service.login;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.UserInfoSessionDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.repository.login.UserLoginStatusRepository;
import sky.board.global.locationfinder.service.LocationFinderService;
import sky.board.global.redis.dto.RedisKeyDto;
import sky.board.global.redis.service.RedisService;

/**
 * 사용자가 로그인 성공한
 * <p>
 * 정보를 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserLoginStatusService {


    private final UserLoginStatusRepository userLoginStatusRepository;
    private final UserQueryRepository userQueryRepository;
    private final LocationFinderService locationFinderService;
    private final RedisService redisService;

    /**
     * 로그인 시
     * 로그인 아이디 관리를 위해
     * 저장
     */
    public void save(HttpServletRequest request) throws LoginException {
        HttpSession session = request.getSession(false);
        UserInfoSessionDto userInfoSessionDto = (UserInfoSessionDto) session.getAttribute(RedisKeyDto.USER_KEY);

        if (userInfoSessionDto == null) {
            throw new LoginException("code.error");
        }
        String userId = userInfoSessionDto.getUserId();

        Optional<User> findUser = userQueryRepository.findByUserId(userId);

        User user = findUser.orElseThrow(() -> new UsernameNotFoundException("sky.userId.notFind"));

        // UserLoginStatus 생성 후
        UserLoginStatus userLoginStatus = UserLoginStatus.getLoginStatus(userInfoSessionDto, locationFinderService,
            request,
            user);
        //저장
        UserLoginStatus save = userLoginStatusRepository.save(userLoginStatus);
        Optional.ofNullable(save).orElseThrow(() -> new LoginException("error"));
    }

    public void delete(String userId) {
        Optional<User> findUser = userQueryRepository.findByUserId(userId);
        User user = findUser.orElseThrow(() -> new UsernameNotFoundException("sky.userId.notFind"));

        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatus(user,
            Status.ON.getValue());

        /**
         * 로그인 기기 로그아웃
         */
        if (userLoginStatusList.size() != 0) {
            for (UserLoginStatus userLoginStatus : userLoginStatusList) {
                // 해당 기기의 세션 값 삭제
                redisService.sessionDeleteData(userLoginStatus.getSession());

                // 해당 기기의 rememberMe redis 값 삭제
                if (StringUtils.hasText(userLoginStatus.getRemember())) {
                    redisService.rememberDeleteData(userLoginStatus.getRemember());
                }

                // status값 변경
                UserLoginStatus.loginStatusUpdate(userLoginStatus);
            }
        }
    }


}
