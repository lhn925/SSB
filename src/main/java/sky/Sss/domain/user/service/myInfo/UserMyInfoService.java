package sky.Sss.domain.user.service.myInfo;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.join.UserJoinService;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.file.dto.UploadFileDto;
import sky.Sss.global.redis.dto.RedisKeyDto;

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
    public void updateUserName(UserNameUpdateDto userNameUpdateDto, BindingResult bindingResult)
        throws DuplicateCheckException  {
        // 변경 가능 여부
        boolean isChange = false;
        User user = userQueryService.findOne();

        LocalDateTime userNameModifiedDate = user.getUserNameModifiedDate();

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        // 1개월 후
        LocalDateTime plusMonthsDate = now.plusMonths(MONTHS);

        // 중복 확인
        userJoinService.checkUserName(userNameUpdateDto.getUserName(),bindingResult);

        // 유저 네임을 가입하고나서 처음 변경 할경우 바로 변경 가능
        if (userNameModifiedDate != null && !now.isAfter(userNameModifiedDate)) {
            // 1개월이 넘지못했다면
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd E HH:mm");
            throw new ChangeUserNameIsNotAfterException(userNameModifiedDate.format(dateTimeFormatter));
        }

        if (!isChange) {
            user.updateUserName(userNameUpdateDto.getUserName(), plusMonthsDate);
            UserInfoDto.createUserInfo(user);
        }
        userNameUpdateDto.setUserNameModifiedDate(plusMonthsDate);
    }

    @Transactional
    public UploadFileDto updatePicture(MultipartFile file) {
        User user = userQueryService.findOne();
        UploadFileDto uploadFileDto = null;
        if (!file.isEmpty()) {
            try {
                uploadFileDto = fileStore.storeImageSave(file, fileStore.getUserPictureDir(), user.getToken());
                uploadFileDto.setUserId(user.getUserId());
                if (uploadFileDto != null) {
                    //기존에 있던 이미지 삭제 없으면 삭제 x
                    user.deletePicture(fileStore);
                    // 서버에 저장되는 파일이름 저장
                    user.updatePicture(uploadFileDto.getStoreFileName());
                    UserInfoDto.createUserInfo(user);
                }

            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        } else {
            throw new RuntimeException("file.error.NotBlank");
        }

        return uploadFileDto;
    }


    @Transactional
    public void deletePicture() throws FileNotFoundException {

        User user = userQueryService.findOne();

        String pictureUrl = user.getPictureUrl();

        // 프로필 사진이 있는 경우
        if (StringUtils.hasText(pictureUrl)) {
            try {
                //유저 이미지 삭제
                user.deletePicture(fileStore);
                user.updatePicture(null);
                UserInfoDto.createUserInfo(user);

            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        } else {
            throw new FileNotFoundException("file.error.delete");
        }
    }

    @Transactional
    public void updateLoginBlocked(UserLoginBlockUpdateDto userLoginBlockUpdateDto) {
        User user = userQueryService.findOne();
        // block 여부 업데이트
        log.info("userLoginBlockUpdateDto = {}", userLoginBlockUpdateDto.getIsLoginBlocked());
        User.changeIsLoginBlocked(user,userLoginBlockUpdateDto);
        // 세션 업데이트
        UserInfoDto.createUserInfo(user);
    }


    public UrlResource getPictureImage(String imageName) {
        try {
            return fileStore.getPictureUrlResource(imageName);
        } catch (MalformedURLException e) {
            throw new RuntimeException("file.error.notFind");
        }
    }


}
