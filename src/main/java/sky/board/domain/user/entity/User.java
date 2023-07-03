package sky.board.domain.user.entity;


import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.base.BaseTimeEntity;
import sky.board.domain.user.dto.UserJoinAgreeDto;
import sky.board.domain.user.dto.UserJoinPostDto;
import sky.board.domain.user.utill.UserTokenUtil;


@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
@EntityListeners(AuditingEntityListener.class)
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

    private String token;

    private String userId;
    private String password;
    private String userName;
    private String email;

    @OneToOne(mappedBy = "user", fetch = LAZY)
    private UserJoinAgreement userJoinAgreement;

    // 프로필 사진
    private String pictureUrl;

    // 유저네임마지막 수정일
    @LastModifiedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime userNameModifiedDate;

    // 회원 등급
    @Enumerated(STRING)
    private UserGrade grade;

    private String salt;

    // 비활성화(회원 탈퇴) 여부 true:탈퇴,false:탈퇴 x
    private Boolean isStatus;

    // 가입할 유저 entity 생성
    public static User createJoinUser(UserJoinPostDto userJoinDto, String salt) {
        User user = new User();
        user.setUserId(userJoinDto.getUserId());
        user.setEmail(userJoinDto.getEmail());
        user.setToken(UserTokenUtil.hashing(userJoinDto.getEmail().getBytes(), salt));
        user.setPassword(userJoinDto.getPassword());
        user.setUserName(userJoinDto.getUserName());
        user.setSalt(salt);
        user.setGrade(UserGrade.USER);
        user.setIsStatus(false);

        return user;
    }

    public void changeUserJoinAgreement(UserJoinAgreement userJoinAgreement){
        this.userJoinAgreement = userJoinAgreement;
    }

}
