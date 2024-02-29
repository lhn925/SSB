package sky.Sss.domain.user.entity;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배송지명
    @Nullable
    private String titleAddress;

    // 수령인
    @Nullable
    private String recipient;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid")
    private User uid;

    // 도시
    @Nullable
    private String city;

    // 우편 번호
    @Nullable
    private String zipCode;

    // 상세주소
    private String street;

    // 연락처
    @Nullable
    private String contactInfo;
    // 기본 배송지 여부
    @Nullable
    private Boolean isMain;

}
