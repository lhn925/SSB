package sky.board.domain.user.service.login;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.RememberCookie;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.repository.login.UserLoginStatusRepository;
import sky.board.domain.user.utili.CustomCookie;
import sky.board.domain.user.utili.UserTokenUtil;
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
@Transactional(readOnly = true)
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
    @Transactional
    public void save(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        if (userInfoDto == null) {
            throw new IllegalArgumentException("code.error");
        }
        String userId = userInfoDto.getUserId();

        User user = User.getOptionalUser(userQueryRepository.findByUserId(userId));

        // UserLoginStatus 생성 후
        UserLoginStatus userLoginStatus = UserLoginStatus.getLoginStatus(locationFinderService,
            request,
            user);

        //저장
        UserLoginStatus save = userLoginStatusRepository.save(userLoginStatus);
        Optional.ofNullable(save).orElseThrow(() -> new IllegalArgumentException("error"));
    }


    @Transactional
    public void updateLoginStatus(HttpServletRequest request, String userId, Status loginStatus, Status isStatus) {
        HttpSession session = request.getSession(false);
        String sessionId = session.getId();// 세션 아이디
//        해당 세션 정보 가져옴

        User user = User.getOptionalUser(userQueryRepository.findByUserId(userId));

        userId = user.getUserId();

        log.info("sessionId = {}", sessionId);
        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findSessionList(user, userId, sessionId);
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, loginStatus.getValue(), isStatus.getValue(), sessionId);
        }
    }
    @Transactional
    public void updateRememberLoginStatus(HttpServletRequest request, String userId, Status loginStatus, Status isStatus) {
        HttpSession session = request.getSession(false);
        String rememberValue = CustomCookie.readCookie(request.getCookies(), RememberCookie.KEY.getValue());
//        해당 세션 정보 가져옴

        User user = User.getOptionalUser(userQueryRepository.findByUserId(userId));

        userId = user.getUserId();

        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findRememberList(user, userId, rememberValue);
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.updateRemember(user, loginStatus.getValue(), isStatus.getValue(), rememberValue);
        }
    }

    @Transactional
    public void removeAllLoginStatus(String userId, String sessionId) {
        User user = User.getOptionalUser(userQueryRepository.findByUserId(userId));

        // 로그인 되어 있는 기기 검색
        // 현재 접속하고 있는 세션 제외
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatusAndSessionNot(
            user,
            Status.ON.getValue(), sessionId);
        /**
         * 로그인 기기 로그아웃
         */
        // 로그인 되어 있는기기가 있을 경우
        removeStatus(user, userLoginStatusList);
    }

    @Transactional
    public void removeAllLoginStatus(HttpServletRequest request) {

        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        User user = User.getOptionalUser(userQueryRepository.findOne(userInfoDto.getUserId(),userInfoDto.getToken()));

        // 로그인 되어 있는 기기 검색
        // 현재 접속하고 있는 세션 제외
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatus(
            user,
            Status.ON.getValue());
        /**
         * 로그인 기기 로그아웃
         */
        // 로그인 되어 있는기기가 있을 경우
        removeStatus(user, userLoginStatusList);
    }
    private void removeStatus(User user, List<UserLoginStatus> userLoginStatusList) {
        if (userLoginStatusList.size() != 0) {
            for (UserLoginStatus userLoginStatus : userLoginStatusList) {
                // 해당 기기의 세션 값 삭제

                if (redisService.hasRedis(RedisKeyDto.SESSION_KEY + userLoginStatus.getSession())) {
                    redisService.deleteSession(userLoginStatus.getSession());
                }
                // 해당 기기의 rememberMe redis 값 삭제
                if (StringUtils.hasText(userLoginStatus.getRemember()) &&
                    redisService.hasRedis(RedisKeyDto.REMEMBER_KEY + hashing(userLoginStatus.getRemember()))) {
                    redisService.deleteRemember(hashing(userLoginStatus.getRemember()));
                }
            }
            Integer integer = userLoginStatusRepository.updateAll(user, Status.OFF.getValue(), Status.OFF.getValue());

            if (integer <= 0) {
                throw new IllegalStateException();
            }
        }
    }

    /**
     * value 값 해석
     *
     * @return
     */
    private String hashing(String rememberMe) {
        String[] cookie = rememberMe.split(":");
        rememberMe = UserTokenUtil.hashing(cookie[0].getBytes(), cookie[1]);
        return rememberMe;
    }
}
