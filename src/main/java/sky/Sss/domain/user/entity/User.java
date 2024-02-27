package sky.Sss.domain.user.entity;


import static jakarta.persistence.EnumType.STRING;

import jakarta.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.myInfo.UserLoginBlockUpdateDto;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.Blocked;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.domain.user.dto.join.UserJoinPostDto;
import sky.Sss.domain.user.model.PwSecLevel;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.utili.TokenUtil;
import sky.Sss.global.file.utili.FileStore;


@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
//@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SequenceGenerator(name="userTable_id_sequence",sequenceName = "userTable_id_sequence",initialValue = 1,allocationSize = 50)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueTokenAndNotification", columnNames = {"token"}),
    @UniqueConstraint(name = "UniqueEmailAndSalt", columnNames = {"email", "salt"}),
    @UniqueConstraint(name = "UniqueUserIdAndUserName", columnNames = {"userid", "username"})})
public class User extends BaseTimeEntity {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "userTable_id_sequence")
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String password;

    // 사용자 이름 or 사용자 profileLink 주소
    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String email;

    // 프로필 사진
    private String pictureUrl;

    // 유저네임 마지막 수정일 한번 바꾼후 3개월 동안 바꾸질 못함
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime userNameModifiedDate;
    // 회원 등급
    @Enumerated(STRING)
    private UserGrade grade;

    // 비밀번호 보안 등급
//    @Column(nullable = false)
    @Enumerated(STRING)
    private PwSecLevel pwSecLevel;

//    @Column(nullable = false)
    private String salt;

    // 차단: true , 차단 x : false
//    @Column(nullable = false)
    private Boolean isLoginBlocked;

    // 비활성화(회원 탈퇴) 여부 true:탈퇴x ,false:탈퇴
//    @Column(nullable = false)
    private Boolean isEnabled;


    @Builder
    private User(String token, String userId, String password, String userName, String email, String pictureUrl,
        LocalDateTime userNameModifiedDate, UserGrade grade, PwSecLevel pwSecLevel, String salt, Boolean isEnabled,Boolean isLoginBlocked) {
        this.token = token;
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.pictureUrl = pictureUrl;
        this.userNameModifiedDate = userNameModifiedDate;
        this.grade = grade;
        this.pwSecLevel = pwSecLevel;
        this.salt = salt;
        this.isEnabled = isEnabled;
        this.isLoginBlocked = isLoginBlocked;
    }

    // 가입할 유저 entity 생성
    public static User createJoinUser(UserJoinPostDto userJoinDto, String salt, PasswordEncoder passwordEncoder) {
        return User.builder().userId(userJoinDto.getUserId())
            .email(userJoinDto.getEmail())
            .token(TokenUtil.hashing(userJoinDto.getEmail().getBytes(), salt))
            .password(passwordEncoder.encode(userJoinDto.getPassword()))
            .userName(userJoinDto.getUserName())
            .salt(salt)
            .grade(UserGrade.USER)
            .isLoginBlocked(Blocked.UNBLOCKED())
            .pwSecLevel(userJoinDto.getPwSecLevel())
            .isEnabled(Enabled.ENABLED()).build();
    }

    //  User 클래스 (org.springframework.security.core.UserDetails.User)의 빌더를
    //  사용해서 username 에 아이디, password 에 비밀번호,
    //  roles 에 권한(역할)을 넣어주고 리턴
    public static UserDetails UserBuilder(User user) {
        CustomUserDetails build = CustomUserDetails.builder().
            userId(user.getUserId()).
            username(user.getUserId()).
            token(user.getToken()).
            email(user.getEmail()).
            pictureUrl(user.getPictureUrl()).
            nickname(user.getUserName()).
            password(user.getPassword()).
            enabled(user.getIsEnabled()).
            userNameModifiedDate(user.getUserNameModifiedDate()).
            isLoginBlocked(user.getIsLoginBlocked()).createdDateTime(user.getCreatedDateTime()).
            build();
        build.setAuthorities(user.getGrade().getDescription());
        return build;
    }

    public static User getOptionalUser(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(() -> new UserInfoNotFoundException("sky.userId.notFind"));
    }

    /**
     * 비밀번호 업데이트 및 보안등급 업데이트
     *
     * @param user
     * @param password
     * @param passwordEncoder
     * @return
     */
    public static void updatePw(User user, String password, PwSecLevel pwSecLevel, PasswordEncoder passwordEncoder) {
        user.setPassword(passwordEncoder.encode(password));
        user.setPwSecLevel(pwSecLevel);
    }


    public static void updateUserName(User user,String updateName, LocalDateTime plusMonthsDate) {
        user.setUserName(updateName);
        // 3개월 동안 변경 불가능
        user.setUserNameModifiedDate(plusMonthsDate);
    }

    public static void updatePicture(User user,String uploadFile) {
        user.setPictureUrl(uploadFile);
    }

    /**
     * 기존에 있던 프로필 이미지 삭제
     *
     * @param fileStore
     * @throws IOException
     */
    public static void deletePicture(User user,FileStore fileStore) throws IOException {
        if (StringUtils.hasText(user.getPictureUrl())) {
            fileStore.deleteFile(FileStore.IMAGE_DIR, user.getPictureUrl());
        }
    }

    public static void changeIsLoginBlocked(User user, UserLoginBlockUpdateDto userLoginBlockUpdateDto) {
        user.setIsLoginBlocked(userLoginBlockUpdateDto.getIsLoginBlocked());
    }
    @Override
    public String toString() {
        return "User{" +
            "id=" + id +
            ", token='" + token + '\'' +
            ", userId='" + userId + '\'' +
            ", password='" + password + '\'' +
            ", userName='" + userName + '\'' +
            ", email='" + email + '\'' +
            ", pictureUrl='" + pictureUrl + '\'' +
            ", userNameModifiedDate=" + userNameModifiedDate +
            ", grade=" + grade +
            ", pwSecLevel=" + pwSecLevel +
            ", salt='" + salt + '\'' +
            ", isLoginBlocked=" + isLoginBlocked +
            ", isEnabled=" + isEnabled +
            '}';
    }


}
