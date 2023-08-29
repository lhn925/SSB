package sky.board.domain.user.repository.login;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sky.board.domain.user.entity.login.UserLoginStatus;

public interface UserLoginStatusRepository extends JpaRepository<UserLoginStatus,Long> {

}
