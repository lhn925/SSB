package sky.board.domain.user.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.UserActivityLogRepository;
import sky.board.domain.user.repository.LoginLogRepository;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLogService {

    private final LoginLogRepository loginLogRepository;
    private final UserActivityLogRepository userActivityLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     */
    @Transactional
    public void saveLoginLog(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {

        Long uId = null;
        if (isSuccess.equals(LoginSuccess.SUCCESS)) {
            uId = userQueryRepository.findByUserId(request.getParameter("userId")).getId();
        }
        UserLoginLog userLoginLog = getUserLoginLog(uId, request, isSuccess, isStatus);
        Optional<UserLoginLog> saveLog = Optional.ofNullable(loginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(() -> new RuntimeException());
    }

    /**
     * 유저 정보 수정 기록 저장
     */
    @Transactional
    public void saveActivityLog(Long uId, String userId, String chaContent, String chaMethod,
        HttpServletRequest request, ChangeSuccess changeSuccess) {
        if (uId == null) {
            User findUserId = userQueryRepository.findByUserId(userId);
            uId = findUserId.getId();
        }
        UserActivityLog userActivityLog = getUserActivityLog(uId, chaContent, chaMethod, request, userId, changeSuccess,
            Status.ON);
        userActivityLogRepository.save(userActivityLog);
    }

    public Long getLoginLogCount(String userId, LoginSuccess loginSuccess, Status isStatus) {

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserLoginLog> loginLogPage = loginLogRepository.findByUserIdAndIsSuccessAndIsStatus(userId, loginSuccess,
            isStatus.getValue(),
            pageRequest);
        return loginLogPage.getTotalElements();
    }

    /**
     * 유저 로그인 개체 생성
     *
     * @param request
     * @param isSuccess
     * @return
     */
    public UserLoginLog getUserLoginLog(Long uId, HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        return UserLoginLog.getLoginLog(uId, locationFinderService, request, isSuccess, isStatus);
    }

    /**
     * 유저 정보 변경 로그 생성
     *
     * @return
     */
    public UserActivityLog getUserActivityLog(Long uId, String chaContent, String chaMethod, HttpServletRequest request,
        String userId,
        ChangeSuccess changeSuccess, Status isStatus) {
        return UserActivityLog.getActivityLog(uId, locationFinderService, chaContent, chaMethod, request, userId,
            changeSuccess, isStatus);

    }

    @Transactional
    public void deleteLoginLog(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");
        loginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
    }

}
