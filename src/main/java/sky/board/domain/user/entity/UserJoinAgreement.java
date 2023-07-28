package sky.board.domain.user.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import sky.board.global.base.BaseTimeEntity;
import sky.board.domain.user.dto.UserJoinAgreeDto;

@Entity
@Setter(value = AccessLevel.PRIVATE)
@Getter
public class UserJoinAgreement extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;


    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    // [필수] sdd사이트 이용약관
    @Nullable
    private boolean sddAgreement;

    // [필수] 개인정보 수집 및 이용
    @Nullable
    private boolean infoAgreement;

    private void setUser(User user) {
        this.user = user;
    }

    public static UserJoinAgreement createUserJoinAgreement(User user, UserJoinAgreeDto userJoinAgreeDto) {
        UserJoinAgreement userJoinAgreement = new UserJoinAgreement();
        userJoinAgreement.setUser(user);
        userJoinAgreement.setSddAgreement(userJoinAgreeDto.isSbbAgreement());
        userJoinAgreement.setInfoAgreement(userJoinAgreeDto.isInfoAgreement());
        return userJoinAgreement;
    }


}
