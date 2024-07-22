package sky.Sss.domain.user.service.login;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.myInfo.UserLoginListDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.login.UserLoginStatus;
import sky.Sss.domain.user.exception.RefreshTokenNotFoundException;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.repository.login.UserLoginStatusRepository;
import sky.Sss.domain.user.service.MsgTemplateService;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.utili.jwt.JwtTokenDto;
import sky.Sss.domain.user.utili.jwt.TokenProvider;
import sky.Sss.global.locationfinder.service.LocationFinderService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisQueryService;
import sky.Sss.global.utili.auditor.AuditorAwareImpl;
import sky.Sss.global.ws.dto.LogOutWebSocketDto;

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
    private final RedisQueryService redisQueryService;
    private final MsgTemplateService msgTemplateService;
    private final PasswordEncoder passwordEncoder;
    private final AuditorAware auditorAware;

    /**
     * 로그인 시
     * 로그인 아이디 관리를 위해
     * 저장
     */
    @Transactional
    public void add(String userAgent, JwtTokenDto jwtTokenDto, String userId, long uid, String sessionId) {

        if (uid == 0) {
            throw new IllegalArgumentException("login.error");
        }
        User user = User.builder().id(uid).build();
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
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        if (findStatus.size() > 0) { // 기존에 있던 현재 세션아이디를 가진 Status 다 로그아웃
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), sessionId);
            for (UserLoginStatus status : findStatus) {
                if (redisQueryService.hasRedis(status.getRedisToken())) {
                    redisQueryService.delete(status.getRedisToken());
                }
            }
        }
        //새롭게 저장
        UserLoginStatus save = userLoginStatusRepository.save(saveLoginStatus);
        Optional.of(save).orElseThrow(() -> new IllegalArgumentException("error"));
    }

    /**
     * 로그인 기기 관리 페이지에 뿌려줄 데이터 검색
     *
     * @param loginStatus
     */
    public Page<UserLoginListDto> getUserLoginStatusList(String sessionId, Status loginStatus, int offset, int size) {

        Sort sort = Sort.by(
            Order.desc("id")
        );

        PageRequest pageRequest = PageRequest.of(offset, size,sort);
        User user = userQueryService.findOne();
        Page<UserLoginListDto> paging = userLoginStatusRepository.findByUidAndLoginStatus(user,
            loginStatus.getValue(), sessionId, pageRequest).map(u -> new UserLoginListDto(sessionId, u));

        boolean sizeOut = paging.getTotalPages() < offset;

        // 요청한 offset total 범위를 넘었을경우
        // offset = totalpage - 1
        if (sizeOut && paging.getContent().isEmpty()) {
            PageRequest newPageRequest = PageRequest.of(paging.getTotalPages() - 1, size,sort);
            paging = userLoginStatusRepository.findByUidAndLoginStatus(user,
                loginStatus.getValue(), sessionId, newPageRequest).map(u -> new UserLoginListDto(sessionId, u));
        }

        return paging;
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
    public void logoutDevice(String password, String logoutSessionId, Status loginStatus, Status isStatus,
        String sessionId) {
//        해당 세션 정보 가져옴
        User user = userQueryService.getEntityUser();
        boolean matches = passwordEncoder.matches(password.trim(), user.getPassword());

        if (!matches) {
            throw new BadCredentialsException("pw.authMatches.mismatch");
        }

        List<UserLoginStatus> findStatusList = userLoginStatusRepository.findList(user,
            user.getUserId(),
            loginStatus.getValue(), isStatus.getValue(), logoutSessionId);
        if (findStatusList.size() > 0) {
            userLoginStatusRepository.update(user, Status.OFF.getValue(), Status.OFF.getValue(), logoutSessionId);
            removeLoginToken(findStatusList, sessionId);
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
     * webSocket ID update
     *
     * @param userId
     * @param redisToken
     */
    @Transactional
    public void wsIdUpdate(String userId, String redisToken, String wsId) {
        User user = userQueryService.findOne(userId);
        Integer result = userLoginStatusRepository.wsUpdate(user, wsId, RedisKeyDto.REDIS_LOGIN_KEY + redisToken);
        log.info("result = {}", result);
        if (result == 0) {
            throw new MessageDeliveryException("JWT");
        }
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
     * all 로그아웃 레디스
     *
     * @param userId
     */
    @Transactional
    public void removeAllStatus(String userId, String sessionId) {
        User user = userQueryService.findOne(userId);

        // 로그인 되어 있는 기기 검색
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatus(
            user,
            Status.ON.getValue());
        /**
         * 로그인 기기 로그아웃
         */
        // 로그인 되어 있는기기가 있을 경우
        updateAllStatus(user, userLoginStatusList, sessionId);
    }

    /**
     * 현재 접속하고 있는 세션제외 전부다 로그아웃 및 레디스 삭제
     *
     * @param userId
     */
    @Transactional
    public void removeStatusNotSession(String userId, String sessionId) {
        User user = userQueryService.findOne(userId);
        // 로그인 되어 있는 기기 검색
        List<UserLoginStatus> userLoginStatusList = userLoginStatusRepository.findAllByUidAndLoginStatus(
            user,
            Status.ON.getValue());
        /**
         * 로그인 기기 로그아웃
         */
        updateStatus(user, userLoginStatusList, sessionId);
    }

    private void updateStatus(User user, List<UserLoginStatus> userLoginStatusList, String sessionId) {
        if (userLoginStatusList.size() != 0) {
            removeLoginToken(userLoginStatusList, sessionId);
            Integer integer = userLoginStatusRepository.updateAllNotSession(user, Status.OFF.getValue(),
                Status.OFF.getValue(), sessionId);
            if (integer <= 0) {
                throw new IllegalStateException();
            }
        }
    }

    private void updateAllStatus(User user, List<UserLoginStatus> userLoginStatusList, String sessionId) {
        if (userLoginStatusList.size() != 0) {
            removeAllLoginToken(userLoginStatusList);
            Integer integer = userLoginStatusRepository.updateAll(user, Status.OFF.getValue(),
                Status.OFF.getValue());
            if (integer <= 0) {
                throw new IllegalStateException();
            }
        }
    }


    @Transactional
    public void tokenValidate(String refreshToken, Jws<Claims> claimsJws) throws IOException {
        // 레디스에 없을 경우 유효한 jwt 토큰으로 판단 x
        String redisToken = (String) claimsJws.getBody().get(TokenProvider.REDIS_TOKEN_KEY);
        String userId = (String) claimsJws.getBody().get("sub");
        // 전달된 토큰 존재 여부 확인
        boolean isToken = StringUtils.hasText(redisToken);

        redisToken = RedisKeyDto.REDIS_LOGIN_KEY + redisToken;
        // 레디스 존재 여부 확인
        boolean isRedis = redisQueryService.hasRedis(redisToken);
        try {
            // 토큰이 아예 전달이 안된 경우
            // 전달은 됐지만 레디스에 토큰이 없는 경우
            if (!isRedis || !isToken) {
                update(userId, redisToken, refreshToken, Status.OFF, Status.OFF);
                throw new RefreshTokenNotFoundException();
            }
            // 레디스에 저장한 아이디와 전달한 아이디가 서로 다른 경우
            String redisUserId = redisQueryService.getData(redisToken);
            if (!redisUserId.equals(userId)) {
                throw new RefreshTokenNotFoundException();
            }
            // DB 존재 여부 확인
            // 레디스에는 있지만 디비에는 없는 경우
            // 레디스에서 삭제
            UserLoginStatus findStatus = findOne(userId, redisToken,
                refreshToken, Status.ON, Status.ON);
            if (findStatus == null) {
                redisQueryService.delete(redisToken);
                throw new RefreshTokenNotFoundException();
            }

        } catch (RefreshTokenNotFoundException e) {
            throw new IOException();
        }

    }

    /**
     * 특정유저의 현재를 제외한 모드 레디스 세션 삭제
     *
     * @param userLoginStatusList
     */
    private void removeLoginToken(List<UserLoginStatus> userLoginStatusList, String sessionId) {
        for (UserLoginStatus userLoginStatus : userLoginStatusList) {
            // 세션 삭제
            // 현재 접속하고 있는 세션 제외
            if (!userLoginStatus.getSessionId().equals(sessionId)) {
                // 레디스 토큰 값 삭제
                removeRedisToken(userLoginStatus);
            }

        }
    }

    /**
     * 특정유저의 모든 레디스 세션 삭제
     *
     * @param userLoginStatusList
     */
    private void removeAllLoginToken(List<UserLoginStatus> userLoginStatusList) {
        for (UserLoginStatus userLoginStatus : userLoginStatusList) {
            // 레디스 토큰 값 삭제
            removeRedisToken(userLoginStatus);
        }
    }

    private void removeRedisToken(UserLoginStatus userLoginStatus) {
        if (redisQueryService.hasRedis(userLoginStatus.getRedisToken())) {
            redisQueryService.delete(userLoginStatus.getRedisToken());
        }
        msgTemplateService.convertAndSend("/topic/logout/" + userLoginStatus.getRefreshToken(),
            new LogOutWebSocketDto());

        redisQueryService.delete(RedisKeyDto.REDIS_SESSION_KEY + userLoginStatus.getSessionId());
    }
}
