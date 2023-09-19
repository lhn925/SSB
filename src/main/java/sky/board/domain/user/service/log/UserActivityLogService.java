package sky.board.domain.user.service.log;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserActivityLogListDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.log.UserActivityLogRepository;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.service.UserQueryService;
import sky.board.global.auditor.AuditorAwareImpl;
import sky.board.global.locationfinder.service.LocationFinderService;
import sky.board.global.redis.dto.RedisKeyDto;

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
    public void save(Long uId, String userId, String chaContent, String chaMethod,
        HttpServletRequest request, ChangeSuccess changeSuccess) {
        Optional<User> byUserId = userQueryRepository.findByUserId(userId);
        if (uId == null && !byUserId.isEmpty()) {
            uId = byUserId.orElse(null).getId();
        }

        //비 로그인으로 접근시 저장할 userId
        AuditorAwareImpl.changeUserId(auditorAware, userId);
        UserActivityLog userActivityLog = getUserActivityLog(uId, chaContent, chaMethod, request, changeSuccess,
            Status.ON);
        Optional<UserActivityLog> save = Optional.ofNullable(userActivityLogRepository.save(userActivityLog));
        save.orElseThrow(() -> new RuntimeException());
    }


    /**
     * 유저 정보 변경 로그 생성
     *
     * @return
     */
    public UserActivityLog getUserActivityLog(Long uId, String chaContent, String chaMethod, HttpServletRequest request,
        ChangeSuccess changeSuccess, Status isStatus) {
        return UserActivityLog.getActivityLog(uId, locationFinderService, chaContent, chaMethod, request,
            changeSuccess, isStatus);

    }

    public Page<UserActivityLogListDto> getUserActivityLogList(HttpServletRequest request, ChangeSuccess changeSuccess,
        Status isStatus,
        LocalDate startDate, LocalDate endDate,
        PageRequest pageRequest) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);
        User findUser = userQueryService.findOne(userInfoDto.getUserId(), userInfoDto.getToken());
        Page<UserActivityLogListDto> userActivityLogListDtoPage = userActivityLogRepository.getPagedDataByUid(findUser.getId(), changeSuccess,
            isStatus.getValue(), startDate,
            endDate, pageRequest).map(u -> new UserActivityLogListDto(ms,request,u));
        return userActivityLogListDtoPage;
    }
}
