package sky.board.domain.user.service.log;


import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.UserActivityLog;
import sky.board.domain.user.model.ChangeSuccess;
import sky.board.domain.user.model.Status;
import sky.board.domain.user.repository.log.UserActivityLogRepository;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.global.locationfinder.service.LocationFinderService;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserActivityLogService {

    private final UserActivityLogRepository userActivityLogRepository;
    private final LocationFinderService locationFinderService;
    private final UserQueryRepository userQueryRepository;

    /**
     * 유저 정보 수정 기록 저장
     */
    @Transactional
    public void save(Long uId, String userId, String chaContent, String chaMethod,
        HttpServletRequest request, ChangeSuccess changeSuccess) {
        if (uId == null) {
            User findUserId = userQueryRepository.findByUserId(userId);
            uId = findUserId.getId();
        }
        UserActivityLog userActivityLog = getUserActivityLog(uId, chaContent, chaMethod, request, userId, changeSuccess,
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
        String userId,
        ChangeSuccess changeSuccess, Status isStatus) {
        return UserActivityLog.getActivityLog(uId, locationFinderService, chaContent, chaMethod, request, userId,
            changeSuccess, isStatus);

    }
}
