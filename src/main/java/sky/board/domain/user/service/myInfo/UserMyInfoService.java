package sky.board.domain.user.service.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import sky.board.domain.user.dto.UserInfoDto;
import sky.board.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.board.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.board.domain.user.repository.UserQueryRepository;
import sky.board.domain.user.service.UserQueryService;
import sky.board.domain.user.service.join.UserJoinService;
import sky.board.global.file.utili.FileStore;
import sky.board.global.file.dto.UploadFileDto;
import sky.board.global.redis.dto.RedisKeyDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMyInfoService {

    private final Long MONTHS = 1L;
    private final UserJoinService userJoinService;
    private final FileStore fileStore;
    private final UserQueryService userQueryService;

    @Transactional
    public void updateUserName(HttpServletRequest request, UserNameUpdateDto userNameUpdateDto) {
        HttpSession session = request.getSession(false);

        // 변경 가능 여부
        boolean isChange = false;
        User user = userQueryService.findOne(session);

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
            throw new ChangeUserNameIsNotAfterException(userNameModifiedDate.format(dateTimeFormatter));
        }


        if (!isChange) {
            user.updateUserName(userNameUpdateDto.getUserName(), plusMonthsDate);
            UserInfoDto.sessionUserInfoUpdate(session, user);
        }
        userNameUpdateDto.setUserNameModifiedDate(plusMonthsDate);
    }

    @Transactional
    public UploadFileDto updatePicture(HttpServletRequest request, MultipartFile file) {
        HttpSession session = request.getSession(false);
        UserInfoDto userInfoDto = (UserInfoDto) session.getAttribute(RedisKeyDto.USER_KEY);

        UploadFileDto uploadFileDto = null;
        if (!file.isEmpty()) {
            try {
                uploadFileDto = fileStore.storeImageSave(file, fileStore.getUserPictureDir(), userInfoDto.getToken());
                uploadFileDto.setUserId(userInfoDto.getUserId());
                if (uploadFileDto != null) {
                    User user = userQueryService.findOne(session);
                    //기존에 있던 이미지 삭제 없으면 삭제 x
                    user.deletePicture(fileStore);

                    // 서버에 저장되는 파일이름 저장
                    user.updatePicture(uploadFileDto.getStoreFileName());

                    UserInfoDto.sessionUserInfoUpdate(session, user);
                }

            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        } else {
            throw new RuntimeException("error.file.NotBlank");
        }

        return uploadFileDto;
    }


    @Transactional
    public void deletePicture(HttpServletRequest request) throws FileNotFoundException {
        HttpSession session = request.getSession(false);

        User user = userQueryService.findOne(session);

        String pictureUrl = user.getPictureUrl();

        // 프로필 사진이 있는 경우
        if (StringUtils.hasText(pictureUrl)) {
            try {
                //유저 이미지 삭제
                user.deletePicture(fileStore);
                user.updatePicture(null);
                UserInfoDto.sessionUserInfoUpdate(session, user);

            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        } else {
            throw new FileNotFoundException("error.file.delete");
        }
    }

    @Transactional
    public void updateLoginBlocked(UserLoginBlockUpdateDto userLoginBlockUpdateDto, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User user = userQueryService.findOne(session);
        // block 여부 업데이트
        log.info("userLoginBlockUpdateDto = {}", userLoginBlockUpdateDto.getIsLoginBlocked());
        User.changeIsLoginBlocked(user,userLoginBlockUpdateDto);
        // 세션 업데이트
        UserInfoDto.sessionUserInfoUpdate(session,user);
    }


    public UrlResource getPictureImage(String imageName) {
        try {
            return fileStore.getPictureUrlResource(imageName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("error.file.notFind");
        }
    }


}
