package sky.Sss.domain.user.service.login;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.myInfo.UserLoginListDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.login.UserLoginStatus;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.repository.login.UserLoginStatusRepository;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisService;

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
    private final UserQueryService userQueryService;
    private final LocationFinderService locationFinderService;
    private final RedisService redisService;

    /**
     * 로그인 시
     * 로그인 아이디 관리를 위해
     * 저장
     */
    @Transactional
    public void save(String userAgent, JwtTokenDto jwtTokenDto, UserDetails userDetails, String sessionId) {

        if (userDetails == null) {
            throw new IllegalArgumentException("code.error");
        }
        String userId = userDetails.getUsername();

        log.info("userId = {}", userId);

        User user = userQueryService.findOne(userId);

        // UserLoginStatus 생성 후
        UserLoginStatus saveLoginStatus = UserLoginStatus.getLoginStatus(locationFinderService,
            userAgent,
            user, jwtTokenDto, sessionId);

        /**
         * Status에 새로 저장하기 전에 현재 세션을 가진
         * 기기들 전부 loginStatus OFF로 바꿔줌으로 인해서
         * 겹치는 문제 제거
         */
        List<UserLoginStatus> findStatus = userLoginStatusRepository.
            findList(user, userId, Status.ON.getValue(), Status.ON.getValue(), sessionId);

        if (findStatus.size() > 0) { // 기존에 있던 현재 세션아이디를 가진 Status 다 로그아웃
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), sessionId);
            for (UserLoginStatus status : findStatus) {
                if (redisService.hasRedis(status.getRedisToken())) {
                    redisService.delete(status.getRedisToken());
                }
            }
        }

        //새롭게 저장
        UserLoginStatus save = userLoginStatusRepository.save(saveLoginStatus);
        Optional.ofNullable(save).orElseThrow(() -> new IllegalArgumentException("error"));
    }

    /**
     * 로그인 기기 관리 페이지에 뿌려줄 데이터 검색
     *
     * @param loginStatus
     */
    public Page getUserLoginStatusList(String sessionId, Status loginStatus, PageRequest pageRequest) {
        User user = userQueryService.findOne();

        Page pagingLoginStatus = userLoginStatusRepository.findByUidAndLoginStatus(user,
            loginStatus.getValue(), pageRequest).map(u -> new UserLoginListDto(sessionId, u));
        return pagingLoginStatus;
    }


    @Transactional
    public void updateLoginStatus(HttpServletRequest request, String userId, Status loginStatus, Status isStatus) {
        HttpSession session = request.getSession(false);
        updateStatus(session, userId, loginStatus, isStatus);
    }

    private void updateStatus(HttpSession session, String userId, Status loginStatus, Status isStatus) {
        String sessionId = session.getId();// 세션 아이디
//        해당 세션 정보 가져옴
        User user = userQueryService.findOne(userId);

        userId = user.getUserId();

        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findList(user, userId, sessionId);
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, loginStatus.getValue(), isStatus.getValue(), sessionId);
        }
    }


    /**
     * 세션값에 에 해당되는 기기를 강제로 로그아웃
     *
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void logoutDevice(String redisToken, Status loginStatus, Status isStatus,String sessionId) {
//        해당 세션 정보 가져옴
        User user = userQueryService.findOne();
        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findList(user,
            user.getUserId(),
            loginStatus.getValue(), isStatus.getValue(), redisToken);
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), redisToken);
            removeLoginToken(findStatusList,sessionId);
        }
    }


    /**
     * 레디스에 redisToken이 있는지 확인
     * refreshToken DB에 유효한지 확인
     *
     * @param userId
     * @param redisToken
     * @param refreshToken
     * @param loginStatus
     * @param isStatus
     * @return
     */

    public UserLoginStatus findOne(String userId, String redisToken, String refreshToken, Status loginStatus,
        Status isStatus) {

        User user = userQueryService.findOne(userId);
        Optional<UserLoginStatus> findLoginStatus = userLoginStatusRepository.findOne(user, userId, redisToken,
            refreshToken,
            loginStatus.getValue(),
            isStatus.getValue());

        UserLoginStatus userLoginStatus = findLoginStatus.orElse(null);

        return userLoginStatus;
    }

    /**
     * status update
     *
     * @param userId
     * @param refreshToken
     * @param loginStatus
     * @param isStatus
     * @return
     */
    @Transactional
    public UserLoginStatus update(String userId, String refreshToken, Status loginStatus, Status isStatus) {
        User user = userQueryService.findOne(userId);

        Optional<UserLoginStatus> findLoginStatus = userLoginStatusRepository.findOne(user, userId,
            refreshToken, Status.ON.getValue(), Status.ON.getValue());
        findLoginStatus.ifPresent(userLoginStatus ->
            UserLoginStatus.loginStatusUpdate(userLoginStatus, loginStatus, isStatus)
        );
        return findLoginStatus.orElse(null);
    }

    /**
     * status update
     *
     * @param userId
     * @param refreshToken
     * @param loginStatus
     * @param isStatus
     * @return
     */
    @Transactional
    public UserLoginStatus update(String userId, String redisToken, String refreshToken, Status loginStatus,
        Status isStatus) {
        User user = userQueryService.findOne(userId);
        Optional<UserLoginStatus> findLoginStatus = userLoginStatusRepository.findOne(user, userId, redisToken,
            refreshToken, Status.ON.getValue(), Status.ON.getValue());
        findLoginStatus.ifPresent(userLoginStatus ->
            UserLoginStatus.loginStatusUpdate(userLoginStatus, loginStatus, isStatus)
        );

        return findLoginStatus.orElse(null);
    }
    /**
     * redis에서 expire된 session off
     *
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void expireRedisSessionKeyOff(String sessionId, Status loginStatus,
        Status isStatus) {
        userLoginStatusRepository.updateSession(loginStatus.getValue(), isStatus.getValue(), sessionId);
    }
    /**
     * 현재 접속하고 있는 세션제외 전부다 로그아웃 및 레디스 삭제
     *
     * @param userId
     */
    @Transactional
    public void removeAllLoginStatus(String userId,String sessionId) {
        User user = userQueryService.findOne(userId);

        // 로그인 되어 있는 기기 검색
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatus(
            user,
            Status.ON.getValue());
        /**
         * 로그인 기기 로그아웃
         */
        // 로그인 되어 있는기기가 있을 경우
        logoutStatus(user, userLoginStatusList,sessionId);
    }
    private void logoutStatus(User user, List<UserLoginStatus> userLoginStatusList,String sessionId) {
        if (userLoginStatusList.size() != 0) {
            removeLoginToken(userLoginStatusList,sessionId);
            Integer integer = userLoginStatusRepository.updateAll(user, Status.OFF.getValue(),
                Status.OFF.getValue());

            log.info("integer = {}", integer);
            if (integer <= 0) {
                throw new IllegalStateException();
            }
        }
    }
    /**
     * 레디스 세션 삭제
     *
     * @param userLoginStatusList
     */
    private void removeLoginToken(List<UserLoginStatus> userLoginStatusList,String sessionId) {
        for (UserLoginStatus userLoginStatus : userLoginStatusList) {
            // 레디스 토큰 값 삭제
            if (redisService.hasRedis(userLoginStatus.getRedisToken())) {
                redisService.delete(userLoginStatus.getRedisToken());
            }
            // 세션 삭제
            // 현재 접속하고 있는 세션 제외
            if (!userLoginStatus.getSessionId().equals(sessionId) && redisService.hasRedis(RedisKeyDto.SESSION_KEY + userLoginStatus.getSessionId())) {
                redisService.delete(RedisKeyDto.SESSION_KEY + userLoginStatus.getSessionId());
            }

        }
    }
}
