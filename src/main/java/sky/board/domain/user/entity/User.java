package sky.board.domain.user.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import sky.board.domain.user.dto.UserJoinDto;

import java.time.LocalDateTime;


@Getter
@Setter(value = AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SequenceGenerator(name="userTable_id_sequence",sequenceName = "userTable_id_sequence",initialValue = 1,allocationSize = 50)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UniqueTokenAndNotification", columnNames = {"token",
        "notification_enabled"}),
    @UniqueConstraint(name = "UniqueEmailAndSalt", columnNames = {"email", "salt"}),
    @UniqueConstraint(name = "UniqueUserIdAndUserName", columnNames = {"userid", "username"})})
public class User {

    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "userTable_id_sequence")
    @GeneratedValue
    private Long id;

    @Column(name = "token")
    private String token;

    private String userId;
    private String password;
    private String userName;
    private String email;
    private String salt;
    private Boolean notification_enabled;

    @CreationTimestamp
    private LocalDateTime createDateTime;

    @UpdateTimestamp
    private LocalDateTime lastVisitDateTime;

    public static User createUser(UserJoinDto userJoinDto,String salt) {
        User user = new User();
        user.setUserId(userJoinDto.getUserId());
        user.setEmail(userJoinDto.getEmail());
        user.setPassword(userJoinDto.getPassword());
        user.setUserName(userJoinDto.getUserName());
        user.setSalt(salt);
        user.setNotification_enabled(userJoinDto.getNotification_enabled());
        return user;
    }

}
