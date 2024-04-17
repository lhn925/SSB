package sky.Sss.domain.user.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import sky.Sss.global.base.BaseTimeEntity;
import sky.Sss.global.base.login.DefaultLocationLog;

/**
 *
 * 사용자의 활동과 관련된 테이블
 * 사용자 마다 임의의 session 부여
 *
 */
public class UserSessionTimes extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //사용자 고유번호
    private long uId;

    // 멤버 여부
    @Column(nullable = false)
    private Boolean isMember;

    // 웹 세션 스토리지에 부여된 아이디 값
    private String webSessionId;

    @Embedded
    private DefaultLocationLog defaultLog;




}
