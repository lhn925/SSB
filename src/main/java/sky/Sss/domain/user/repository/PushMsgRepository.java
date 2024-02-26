package sky.Sss.domain.user.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import sky.Sss.domain.user.entity.UserPushMessages;
public interface PushMsgRepository extends JpaRepository<UserPushMessages,Long> {


}
