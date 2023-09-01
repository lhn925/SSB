package sky.board.domain.user.service.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserMyInfoDto;
import sky.board.domain.user.service.UserQueryService;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMyInfoService {


    private final UserQueryService userQueryService;




    @Transactional
    public Boolean update(HttpServletRequest request,UserMyInfoDto userMyInfoDto) {
        HttpSession session = request.getSession();
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        return false;
    }



}
