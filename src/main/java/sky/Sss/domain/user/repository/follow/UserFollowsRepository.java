package sky.Sss.domain.user.repository.follow;


import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;

public interface UserFollowsRepository extends JpaRepository<UserFollows,Long> {
    Optional<UserFollows> findByFollowingUserAndFollowerUser(User followingUser, User followerUser);




    // 유저를 팔로워 하고 있는 총 유저 리스트
    @Query("select f.followerUser from UserFollows f join fetch User u "
        + " on f.followerUser = u "
        + "where f.followingUser = :user ")
    List<User> getMyFollowerUsers(@Param("user") User user);


    // 유저가 팔로워 하고 있는 총 유저 리스트
    @Query("select f.followingUser from UserFollows f join fetch User u "
        + " on f.followingUser = u "
        + "where f.followerUser = :user ")
    List<User> myFollowingUsers(@Param("user") User user);



    // 유저가 팔로워 하고 있는 총 유저 수
    Integer countByFollowerUser(User followerUser);

    // 유저를 팔로워 하고 있는 총 유저 수
    Integer countByFollowingUser(User followingUser);
}
