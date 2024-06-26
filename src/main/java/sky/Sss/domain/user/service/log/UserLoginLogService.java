package sky.Sss.domain.user.service.log;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.myInfo.UserLoginLogListDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.login.UserLoginLog;
import sky.Sss.domain.user.exception.LoginBlockException;
import sky.Sss.domain.user.model.LoginSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.repository.log.UserLoginLogRepository;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.utili.auditor.AuditorAwareImpl;
import sky.Sss.global.locationfinder.dto.UserLocationDto;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserLoginLogService {

    private final UserLoginLogRepository userLoginLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;
    private final AuditorAware auditorAware;
    private final UserQueryService userQueryService;

    // 로그인 기록은 최근 90일까지의 기록을 최대 1,000 건 까지 제공합니다.

    /**
     * 로그인 기록 저장
     */
    @Transactional
    public void add(String userAgent, String userId, long uid, LoginSuccess isSuccess,
        Status isStatus) {

        //비 로그인으로 접근시 저장할 userId
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        UserLoginLog userLoginLog = getUserLoginLog(uid, userAgent, isSuccess, isStatus);

        Optional<UserLoginLog> saveLog = Optional.of(userLoginLogRepository.save(userLoginLog));
        saveLog.orElseThrow(RuntimeException::new);
    }


    /**
     * 로그인 로그 출력
     *
     * @param startDate
     * @param endDate
     * @param pageRequest
     * @return
     */
    public Page<UserLoginLogListDto> getUserLoginLogList(LocalDate startDate,
        LocalDate endDate,
        PageRequest pageRequest) {
        User user = userQueryService.findOne();

        return userLoginLogRepository.getLoginLogPageable(user.getUserId(),
                LoginSuccess.SUCCESS, Status.ON.getValue(), startDate, endDate, pageRequest)
            .map(UserLoginLogListDto::new);
    }


    public Long getCount(String userId, LoginSuccess loginSuccess, Status isStatus) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<UserLoginLog> loginLogPage = userLoginLogRepository.getLoginLogPageable(userId, loginSuccess,
            isStatus.getValue(),
            pageRequest);
        return loginLogPage.getTotalElements();
    }

    /**
     * 3개월 지난 로그인 기록 전부다 off
     * 3개월이 지난 id count를 구하고 리스트없이 update
     * 데이터 십만개를 테스트 했을때 걸리는 시간 : 683ms
     *
     * @param month
     */
    @Transactional
    public Integer expireLoginLogOff(Integer month) {
        LocalDate expireDate = LocalDate.now().minusMonths(month);

        Integer count = userLoginLogRepository.expireLoginLogCount(Status.ON.getValue(), expireDate);
        Integer result = 0;
        if (count > 0) {
            result = userLoginLogRepository.expireLoginLogOff(Status.OFF.getValue(), Status.ON.getValue(), expireDate);
        }
        return result;
    }

    public void delete(HttpServletRequest request, LoginSuccess isSuccess, Status isStatus) {
        String userId = request.getParameter("userId");
        userLoginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
    }

    @Transactional
    public void delete(String userId, LoginSuccess isSuccess, Status isStatus) {
        userLoginLogRepository.isStatusUpdate(userId, isSuccess, isStatus.getValue());
    }

    /**
     * 유저 로그인 개체 생성
     *
     * @param isSuccess
     * @return
     */
    public UserLoginLog getUserLoginLog(Long uId, String userAgent, LoginSuccess isSuccess, Status isStatus) {
        return UserLoginLog.getLoginLog(uId, locationFinderService, userAgent, isSuccess, isStatus);
    }

    /**
     * 해외 로그인 여부 확인
     *
     * @param
     * @throws UsernameNotFoundException
     */
    public void isLoginBlockChecked(boolean isLoginBlock) throws LoginBlockException {
        if (isLoginBlock) {
            UserLocationDto userLocationDto = locationFinderService.findLocation();
            if (!userLocationDto.getCountryName().equals(Locale.KOREA.getCountry())) {
                throw new LoginBlockException("login.error.blocked");
            }
        }
    }


}
