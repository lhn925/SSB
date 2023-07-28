package sky.board.global.base;


import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity extends BaseTimeEntity {

    @CreatedBy
    @Column(updatable = false)
    private String createdByUserName; // 작성자 유저네임
    @CreatedBy
    @Column(updatable = false)
    private String createdById; // 작성자 고유 아이디 값 혹은 토큰 값으로 변경 예정
    @LastModifiedBy
    private String modifiedBy;

}
