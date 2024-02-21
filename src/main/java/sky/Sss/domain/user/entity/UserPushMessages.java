package sky.Sss.domain.user.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class UserPushMessages {


    @Id
    @GeneratedValue
    private Long Id;

    @JoinColumn(name = "uid", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
}
