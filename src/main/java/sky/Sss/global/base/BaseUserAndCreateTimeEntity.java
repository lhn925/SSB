package sky.Sss.global.base;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public class BaseUserAndCreateTimeEntity {
    @CreatedDate
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime createdDateTime;

    //변경 유저
    @CreatedBy
    private String userId;
}
