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




    // fetch 수정
    // 유저를 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f join f.followerUser u where f.followingUser = :user")
    List<UserFollows> getMyFollowerUsers(@Param("user") User user);


    // fetch 수정
    // 유저가 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f join f.followingUser u "
        + "where f.followerUser = :user ")
    List<UserFollows> myFollowingUsers(@Param("user") User user);



    // 유저가 팔로워 하고 있는 총 유저 수
    Integer countByFollowerUser(User followerUser);

    // 유저를 팔로워 하고 있는 총 유저 수
    Integer countByFollowingUser(User followingUser);
}
