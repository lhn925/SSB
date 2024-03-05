package sky.Sss.domain.feed.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;

public interface FeedRepository extends JpaRepository<SsbFeed, Long> {


    @Query("select f from SsbFeed f where f.user =:user and f.contentsId = :contentsId and f.contentsType =:contentsType ")
    Optional<SsbFeed> findOne(@Param("user") User user, @Param("contentsId") long contentsId,
        @Param("contentsType") ContentsType contentsType);


    /**
     * 여기서
     * :contentsId 는 trackId or playListId
     *
     * @return
     */
    @Query("select f from SsbFeed f join SsbRepost r on f.contentsId = r.id where r.contentsId = :contentsId and "
        + " r.contentsType =:repostType and f.contentsType =:feedType")
    List<SsbFeed> findAllJoinRepost(@Param("contentsId") long contentsId, @Param("repostType") ContentsType repostType,
        @Param("feedType") ContentsType feedType);


}
