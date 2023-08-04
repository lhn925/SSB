package sky.board.domain.user.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.repository.LoginLogRepository;
import sky.board.domain.user.utill.HttpReqRespUtils;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLogService {

    private final LoginLogRepository loginLogRepository;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     *
     */
    @Transactional
    public void saveLoginLog(HttpServletRequest request, LoginSuccess isSuccess) {
        UserLoginLog userLoginLog = getUserLoginLog(request, isSuccess);
        Optional<UserLoginLog> saveLog = Optional.ofNullable(loginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(() -> new RuntimeException());
    }

    public static UserLoginLog getUserLoginLog(HttpServletRequest request, LoginSuccess isSuccess) {
        String userId = request.getParameter("userId");
        String clientIp = HttpReqRespUtils.getClientIpAddressIfServletRequestExist();
        UserLoginLog userLoginLog = UserLoginLog.builder()
            .ip(clientIp)
            .locale(request.getLocale()) // 지역 코드 저장?
            .isSuccess(isSuccess) // 실패 여부 확인
            .userAgent(UserLoginLog.isDevice(request)) // 기기 저장
            .userId(userId) //유저아이디 저장
            .build();
        return userLoginLog;
    }

}
