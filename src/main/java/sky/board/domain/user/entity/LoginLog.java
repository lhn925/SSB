package sky.board.domain.user.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sky.board.global.base.BaseTimeEntity;

/**
 * 같은 아이디 당 시도
 *
 * 5번이상 틀렸을 경우
 *
 *
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
public class LoginLog extends BaseTimeEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String ip;


}
