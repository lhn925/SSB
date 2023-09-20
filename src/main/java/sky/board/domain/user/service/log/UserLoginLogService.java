package sky.board.domain.user.service.log;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Locale.IsoCountryCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.myInfo.UserLoginLogListDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginLog;
import sky.board.domain.user.exception.LoginBlockException;
import sky.board.domain.user.model.LoginSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.log.LoginLogRepository;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.service.UserQueryService;
import sky.board.global.auditor.AuditorAwareImpl;
import sky.board.global.locationfinder.dto.UserLocationDto;
import sky.board.global.locationfinder.service.LocationFinderService;
import sky.board.global.redis.dto.RedisKeyDto;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLoginLogService {

    private final LoginLogRepository loginLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;
    private final AuditorAware auditorAware;
    private final UserQueryService userQueryService;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     */
    @Transactional
    public void save(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {

        Long uId = null;
        String userId = request.getParameter("userId");
        Optional<User> findOne = userQueryRepository.findByUserId(userId);

        User user = findOne.orElse(null);
        if (user != null) {
            uId = user.getId();
        }
        //비 로그인으로 접근시 저장할 userId
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        UserLoginLog userLoginLog = getUserLoginLog(uId, request, isSuccess, isStatus);
        Optional<UserLoginLog> saveLog = Optional.ofNullable(loginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(() -> new RuntimeException());
    }


    /**
     * 로그인 로그 출력
     *
     * @param request
     * @param startDate
     * @param endDate
     * @param pageRequest
     * @return
     */
    public Page<UserLoginLogListDto> getUserLoginLogList(HttpServletRequest request, LocalDate startDate,
        LocalDate endDate,
        PageRequest pageRequest) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        Page<UserLoginLogListDto> loginLogPageable = loginLogRepository.getLoginLogPageable(userInfoDto.getUserId(),
                LoginSuccess.SUCCESS, Status.ON.getValue(), startDate, endDate, pageRequest)
            .map(UserLoginLogListDto::new);

        return loginLogPageable;
    }


    public Long getCount(String userId, LoginSuccess loginSuccess, Status isStatus) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserLoginLog> loginLogPage = loginLogRepository.getLoginLogPageable(userId, loginSuccess,
            isStatus.getValue(),
            pageRequest);
        return loginLogPage.getTotalElements();
    }

    /**
     * 3개월 지난 로그인 기록 전부다 off
     * 3개월이 지난 id count를 구하고 리스트없이 update
     * 데이터 십만개를 테스트 했을때 걸리는 시간 : 683ms
     * @param month
     */
    @Transactional
    public Integer expireLoginLogOff(Integer month) {
        LocalDate expireDate = LocalDate.now().minusMonths(month);

        Integer count = loginLogRepository.expireLoginLogCount(Status.ON.getValue(), expireDate);

        log.info("count = {}", count);

        Integer result = 0;
        if (count > 0) {
           result = loginLogRepository.expireLoginLogOff(Status.OFF.getValue(), Status.ON.getValue(), expireDate);
        }
        return result;
    }

    @Transactional
    public void delete(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");
        loginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
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
     * 해외 로그인 여부 확인
     *
     * @param request
     * @param userId
     * @throws UsernameNotFoundException
     * @throws IOException
     * @throws GeoIp2Exception
     */
    public void isLoginBlockChecked(HttpServletRequest request, String userId)
        throws UsernameNotFoundException, IOException, GeoIp2Exception {
        User findByUser = userQueryService.findOne(userId);
        if (findByUser.getIsLoginBlocked()) {
            UserLocationDto userLocationDto = locationFinderService.findLocation();
            if (!userLocationDto.getCountryName().equals(Locale.KOREA.getCountry())) {
                throw new LoginBlockException("login.error.blocked");
            }
        }
    }


}
