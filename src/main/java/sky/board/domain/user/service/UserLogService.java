package sky.board.domain.user.service;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.UserLoginLog;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.LoginLogRepository;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLogService {

    private final LoginLogRepository loginLogRepository;
    private final LocationFinderService locationFinderService;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     */
    @Transactional
    public void saveLoginLog(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        UserLoginLog userLoginLog = getUserLoginLog(request, isSuccess, isStatus);
        Optional<UserLoginLog> saveLog = Optional.ofNullable(loginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(() -> new RuntimeException());
    }


    public Long getLoginLogCount(String userId, LoginSuccess loginSuccess, Status isStatus) {

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserLoginLog> loginLogPage = loginLogRepository.findByUserIdAndIsSuccessAndIsStatus(userId, loginSuccess,
            isStatus.getValue(),
            pageRequest);
        log.info("loginLogPage.getTotalElements() = {}", loginLogPage.getTotalElements());
        log.info("loginLogPage.getSize() = {}", loginLogPage.getSize());
        return loginLogPage.getTotalElements();
    }

    /**
     * 유저 로그 DTO 생성
     *
     * @param request
     * @param isSuccess
     * @return
     */
    public UserLoginLog getUserLoginLog(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");

        UserLocationDto userLocationDto = null;
        try {
            userLocationDto = locationFinderService.findLocation();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (GeoIp2Exception e) {
            throw new RuntimeException(e);
        }
        UserLoginLog userLoginLog = UserLoginLog.builder()
            .ip(userLocationDto.getIpAddress()) //ip 저장
            .countryName(userLocationDto.getCountryName()) // iso Code 저장
            .latitude(userLocationDto.getLatitude()) // 위도
            .longitude(userLocationDto.getLongitude()) // 경도
            .isSuccess(isSuccess) // 실패 여부 확인
            .userAgent(UserLoginLog.isDevice(request)) // 기기 저장
            .userId(userId) //유저아이디 저장
            .isStatus(isStatus)
            .build();
        return userLoginLog;
    }

    @Transactional
    public void deleteLoginLog(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");
        loginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
    }

}
