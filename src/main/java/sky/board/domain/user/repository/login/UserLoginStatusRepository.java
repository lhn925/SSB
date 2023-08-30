package sky.board.domain.user.repository.login;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import sky.board.domain.user.entity.User;
import sky.board.domain.user.entity.login.UserLoginStatus;
import sky.board.domain.user.model.Status;

public interface UserLoginStatusRepository extends JpaRepository<UserLoginStatus, Long> {

    List<UserLoginStatus> findAllByUidAndLoginStatus(User user, Boolean loginStatus);

}
