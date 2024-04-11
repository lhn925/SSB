package sky.Sss.domain.user.service.log;


import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.myInfo.UserActivityLogListDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserActivityLog;
import sky.Sss.domain.user.model.ChangeSuccess;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.repository.log.UserActivityLogRepository;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.utili.auditor.AuditorAwareImpl;
import sky.Sss.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserActivityLogService {

    private final UserActivityLogRepository userActivityLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;
    private final UserQueryService userQueryService;

    private final MessageSource ms;
    private final AuditorAware<String> auditorAware;

    /**
     * 유저 정보 수정 기록 저장
     */
    @Transactional
    public void add(String userId, String chaContent, String chaMethod,
        String userAgent, ChangeSuccess changeSuccess) {
        Optional<User> byUserId = userQueryRepository.findByUserId(userId);
        /*if (uId == null && !byUserId.isEmpty()) {
            uId = byUserId.orElse(null).getId();
        }
*/
        //비 로그인으로 접근시 저장할 userId
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        UserActivityLog userActivityLog = getUserActivityLog(byUserId.orElse(null), chaContent, chaMethod, userAgent,
            changeSuccess,
            Status.ON);
        Optional<UserActivityLog> save = Optional.ofNullable(userActivityLogRepository.save(userActivityLog));
        save.orElseThrow(() -> new RuntimeException());
    }


    /**
     * 유저 정보 변경 로그 생성
     *
     * @return
     */
    public UserActivityLog getUserActivityLog(User user, String chaContent, String chaMethod, String userAgent,
        ChangeSuccess changeSuccess, Status isStatus) {
        return UserActivityLog.createActivityLog(user, locationFinderService, chaContent, chaMethod, userAgent,
            changeSuccess, isStatus);

    }

    public Page<UserActivityLogListDto> getUserActivityLogList(ChangeSuccess changeSuccess,
        Status isStatus,
        LocalDate startDate, LocalDate endDate,
        PageRequest pageRequest, Locale locale) {
        User user = userQueryService.findOne();

        return userActivityLogRepository.getPagedDataByUid(user,
            changeSuccess,
            isStatus.getValue(), startDate,
            endDate, pageRequest).map(u -> new UserActivityLogListDto(ms, locale, u));
    }

    /**
     * 3개월 지난 로그인 기록 전부다 off
     * 3개월이 지난 id count를 구하고 리스트없이 update
     * 데이터 십만개를 테스트 했을때 걸리는 시간 : 683ms
     *
     * @param month
     */
    @Transactional
    public Integer expireActivityOff(Integer month) {
        LocalDate expireDate = LocalDate.now().minusMonths(month);

        Integer count = userActivityLogRepository.expireActivityCount(Status.ON.getValue(), expireDate);

        log.info("count = {}", count);

        Integer result = 0;
        if (count > 0) {
            result = userActivityLogRepository.expireActivityOff(Status.OFF.getValue(), Status.ON.getValue(),
                expireDate);
        }
        return result;
    }

}
