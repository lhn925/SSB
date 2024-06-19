package sky.Sss.domain.user.repository.follow;


import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.user.dto.redis.RedisFollowsDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserFollows;

public interface UserFollowsRepository extends JpaRepository<UserFollows,Long> {
    Optional<UserFollows> findByFollowingUserAndFollowerUser(User followingUser, User followerUser);




    // fetch 수정
    // 유저를 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f  where f.followingUser = :user and f.followingUser.isEnabled =:isEnabled ")
    List<UserFollows> getMyFollowerUsers(@Param("user") User user, @Param("isEnabled") boolean isEnabled);


    // fetch 수정
    // 유저가 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f "
        + "where f.followerUser = :user and f.followerUser.isEnabled =:isEnabled ")
    List<UserFollows> myFollowingUsers(@Param("user") User user, @Param("isEnabled") boolean isEnabled);


    // fetch 수정
    // 유저들이 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f "
        + "where f.followerUser.token in (:tokens) and  f.followerUser.isEnabled =:isEnabled ")
    List<UserFollows> followingUsersByTokens(@Param("tokens") List<String> tokens, @Param("isEnabled") boolean isEnabled);


    // fetch 수정
    // 유저들을 팔로워 하고 있는 총 유저 리스트
    @Query("select f from UserFollows f "
        + "where f.followingUser.token in (:tokens) and f.followingUser.isEnabled =:isEnabled ")
    List<UserFollows> followerUsersByTokens(@Param("tokens") List<String> tokens, @Param("isEnabled") boolean isEnabled);

    // 유저가 팔로워 하고 있는 총 유저 수
    Integer countByFollowerUser(User followerUser);

    // 유저를 팔로워 하고 있는 총 유저 수
    Integer countByFollowingUser(User followingUser);
}
