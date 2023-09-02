package sky.board.domain.user.service.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Formatter;
import java.util.Optional;
import javax.security.auth.login.LoginException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.login.CustomUserDetails;
import sky.board.domain.user.dto.myInfo.UserMyInfoDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.exception.DuplicateCheckException;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMyInfoService {


    private final Long MONTHS = 1L;
    private final UserQueryRepository userQueryRepository;
    private final UserJoinService userJoinService;

    @Transactional
    public void userNameUpdate(HttpServletRequest request, UserNameUpdateDto userNameUpdateDto) {
        HttpSession session = request.getSession();

        // 변경 가능 여부
        boolean isChange = false;
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        Optional<User> optionalUser = userQueryRepository.findOne(userInfoDto.getUserId(), userInfoDto.getToken());

        User user = User.getOptionalUser(optionalUser);

        LocalDateTime userNameModifiedDate = user.getUserNameModifiedDate();

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        // 1개월 후
        LocalDateTime plusMonthsDate = now.plusMonths(MONTHS);

        // 중복 확인
        userJoinService.checkUserName(userNameUpdateDto.getUserName());

        // 유저 네임을 가입하고나서 처음 변경 할경우 바로 변경 가능
        if (userNameModifiedDate != null && !now.isAfter(userNameModifiedDate)) {
            // 1개월이 넘지못했다면
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd E HH:mm");
            throw new IllegalArgumentException(userNameModifiedDate.format(dateTimeFormatter));
        }

        isChange = true;
        if (isChange) {
            user.updateUserName(userNameUpdateDto.getUserName(), plusMonthsDate);
            UserDetails userDetails = User.UserBuilder(user);
            // username 업데이트
            UserInfoDto userInfo = UserInfoDto.createUserInfo((CustomUserDetails) userDetails);
            session.setAttribute(RedisKeyDto.USER_KEY,userInfo);
        }
        userNameUpdateDto.setUserNameModifiedDate(plusMonthsDate);
    }


}
