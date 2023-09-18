package sky.board.domain.user.service.login;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserLoginListDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.login.UserLoginStatusRepository;
import sky.board.domain.user.service.UserQueryService;
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
    private final UserQueryService userQueryService;
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

        User user = userQueryService.findOne(userId);

        // UserLoginStatus 생성 후
        UserLoginStatus saveLoginStatus = UserLoginStatus.getLoginStatus(locationFinderService,
            request,
            user);

        /**
         * Status에 새로 저장하기 전에 현재 세션을 가진 기기들 전부 loginStatus OFF로 바꿔줌으로 인해서
         * 겹치는 문제 제거
         */
        List<UserLoginStatus> findStatus = userLoginStatusRepository.
            findList(user, userId, session.getId(), Status.ON.getValue(), Status.ON.getValue());
        if (findStatus.size() > 0) { // 기존에 있던 현재 세션아이디를 가진 Status 다 로그아웃
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), session.getId());
        }

        //새롭게 저장
        UserLoginStatus save = userLoginStatusRepository.save(saveLoginStatus);
        Optional.ofNullable(save).orElseThrow(() -> new IllegalArgumentException("error"));
    }

    /**
     * 로그인 기기 관리 페이지에 뿌려줄 데이터 검색
     *
     * @param request
     * @param loginStatus
     */
    public Page getUserLoginStatusList(HttpServletRequest request, Status loginStatus, PageRequest pageRequest) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        User user = userQueryService.findOne(userInfoDto.getUserId(), userInfoDto.getToken());

        Page pagingLoginStatus = userLoginStatusRepository.findByUidAndLoginStatus(user,
            loginStatus.getValue(), pageRequest).map(u -> new UserLoginListDto(request.getSession().getId(), u));

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
     * @param request
     * @param sessionId
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void logoutDevice(HttpServletRequest request, String sessionId, Status loginStatus, Status isStatus) {
//        해당 세션 정보 가져옴
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        String userId = userInfoDto.getUserId();
        User user = userQueryService.findOne(userId, userInfoDto.getToken());
        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findList(user,
            userInfoDto.getUserId(),
            sessionId, loginStatus.getValue(), isStatus.getValue());
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), sessionId);
            removeRedisSession(findStatusList);
        }
    }


    /**
     *  이전에 사용했던 remember 세션 로그아웃
     * @param userId
     * @param sessionId
     * @param rememberId
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void logoutRememberLoginStatus(String userId, String sessionId, String rememberId, Status loginStatus,
        Status isStatus) {

        User user = userQueryService.findOne(userId);

        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findList(user,
            user.getUserId(),
            sessionId, rememberId, loginStatus.getValue(), isStatus.getValue());
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), sessionId, rememberId);
            removeRedisSession(findStatusList);
        }
    }

    /**
     *  redis에서 expire된 remember off
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void expireRedisRememberKeyOff(String rememberId,Status loginStatus,
        Status isStatus) {
        userLoginStatusRepository.updateRemember(loginStatus.getValue(), isStatus.getValue(), rememberId);
    }
    /**
     *  redis에서 expire된 session off
     * @param loginStatus
     * @param isStatus
     */
    @Transactional
    public void expireRedisSessionKeyOff(String sessionId,Status loginStatus,
        Status isStatus) {
        userLoginStatusRepository.updateSession(loginStatus.getValue(), isStatus.getValue(), sessionId);
    }


    /**
     * 현재 접속하고 있는 세션제외 전부다 로그아웃 및 레디스 삭제
     *
     * @param userId
     * @param sessionId
     */
    @Transactional
    public void removeAllLoginStatus(String userId, String sessionId) {
        User user = userQueryService.findOne(userId);

        // 로그인 되어 있는 기기 검색
        // 현재 접속하고 있는 세션 제외
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatusAndSessionNot(
            user,
            Status.ON.getValue(), sessionId);
        /**
         * 로그인 기기 로그아웃
         */
        // 로그인 되어 있는기기가 있을 경우
        removeStatusAndNotSession(user, sessionId, userLoginStatusList);
    }

    private void removeStatus(User user, List<UserLoginStatus> userLoginStatusList) {
        if (userLoginStatusList.size() != 0) {
            removeRedisSession(userLoginStatusList);
            Integer integer = userLoginStatusRepository.updateAll(user, Status.OFF.getValue(), Status.OFF.getValue());

            if (integer <= 0) {
                throw new IllegalStateException();
            }
        }
    }

    private void removeStatusAndNotSession(User user, String session, List<UserLoginStatus> userLoginStatusList) {
        if (userLoginStatusList.size() != 0) {
            removeRedisSession(userLoginStatusList);
            Integer integer = userLoginStatusRepository.updateAllAndNotSession(user, session, Status.OFF.getValue(),
                Status.OFF.getValue());
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
    private void removeRedisSession(List<UserLoginStatus> userLoginStatusList) {
        for (UserLoginStatus userLoginStatus : userLoginStatusList) {
            // 해당 기기의 세션 값 삭제
            if (redisService.hasRedis(RedisKeyDto.SESSION_KEY + userLoginStatus.getSession())) {
                redisService.deleteSession(userLoginStatus.getSession());
            }
            // 해당 기기의 rememberMe redis 값 삭제
            if (StringUtils.hasText(userLoginStatus.getRemember()) &&
                redisService.hasRedis(RedisKeyDto.REMEMBER_KEY + userLoginStatus.getRemember())) {
                redisService.deleteRemember(userLoginStatus.getRemember());
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
