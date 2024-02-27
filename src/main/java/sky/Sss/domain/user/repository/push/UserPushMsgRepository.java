package sky.Sss.domain.user.repository.push;


import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.user.entity.UserPushMessages;
public interface UserPushMsgRepository extends JpaRepository<UserPushMessages,Long> {


}
