package sky.Sss.domain.user.service.myInfo;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.Sss.domain.user.dto.myInfo.UserNameUpdateDto;
import sky.Sss.domain.user.dto.redis.RedisUserDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.ChangeUserNameIsNotAfterException;
import sky.Sss.domain.user.exception.DuplicateCheckException;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.join.UserJoinService;
import sky.Sss.global.file.utili.FileStore;
import sky.Sss.global.file.dto.UploadFileDto;

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
        throws DuplicateCheckException {
        // 변경 가능 여부
        boolean isChange = false;
        // 여기 업데이트
        User user = userQueryService.getEntityUser();

        userQueryService.removeUserInfoDtoRedis(RedisUserDto.create(user));

        LocalDateTime userNameModifiedDate = user.getUserNameModifiedDate();

        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        // 1개월 후
        LocalDateTime plusMonthsDate = now.plusMonths(MONTHS);

        // 중복 확인
        userJoinService.checkUserName(userNameUpdateDto.getUserName(), bindingResult);

        // 유저 네임을 가입하고나서 처음 변경 할경우 바로 변경 가능
        if (userNameModifiedDate != null && !now.isAfter(userNameModifiedDate)) {
            // 1개월이 넘지못했다면
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd E HH:mm");
            throw new ChangeUserNameIsNotAfterException(userNameModifiedDate.format(dateTimeFormatter));
        }
        if (!isChange) {
            // 여기 업데이트
            User.updateUserName(user, userNameUpdateDto.getUserName(), plusMonthsDate);
            UserInfoDto.createUserInfo(user);
        }
        userNameUpdateDto.setUserNameModifiedDate(plusMonthsDate);
        userQueryService.setUserInfoDtoRedis(RedisUserDto.create(user));

    }

    @Transactional
    public UploadFileDto updatePicture(MultipartFile file) {
        // 여기 업데이트
        User user = userQueryService.getEntityUser();
        UploadFileDto uploadFileDto = null;
        if (!file.isEmpty()) {
            try {
                uploadFileDto = fileStore.storeFileSave(file, FileStore.PICTURE_TYPE, 500);
                uploadFileDto.setUserId(user.getUserId());
                //기존에 있던 이미지 삭제 없으면 삭제 x

                User.deletePicture(user, fileStore);
                // 서버에 저장되는 파일이름 저장
                User.updatePicture(user, uploadFileDto.getStoreFileName());
                UserInfoDto.createUserInfo(user);

                userQueryService.setUserInfoDtoRedis(RedisUserDto.create(user));
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

        // 여기 업데이트
        User user = userQueryService.getEntityUser();

        String pictureUrl = user.getPictureUrl();

        // 프로필 사진이 있는 경우
        if (StringUtils.hasText(pictureUrl)) {
            try {
                //유저 이미지 삭제
                User.deletePicture(user, fileStore);
                User.updatePicture(user, null);
                UserInfoDto.createUserInfo(user);

                // Redis 초기화
                userQueryService.setUserInfoDtoRedis(RedisUserDto.create(user));
            } catch (IOException e) {
                throw new RuntimeException("error");
            }
        } else {
            throw new FileNotFoundException("file.error.delete");
        }
    }

    @Transactional
    public void updateLoginBlocked(UserLoginBlockUpdateDto userLoginBlockUpdateDto) {
        // 여기 업데이트
        User user = userQueryService.getEntityUser();
        // block 여부 업데이트
        User.changeIsLoginBlocked(user, userLoginBlockUpdateDto);
        // 세션 업데이트
        UserInfoDto.createUserInfo(user);

        userQueryService.setUserInfoDtoRedis(RedisUserDto.create(user));
    }


    public UrlResource getPictureImage(String imageName) {
        return fileStore.getUrlResource(imageName);
    }


}
