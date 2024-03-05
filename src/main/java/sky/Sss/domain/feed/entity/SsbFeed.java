package sky.Sss.domain.feed.entity;


import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import sky.Sss.domain.feed.model.FeedType;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.global.base.BaseTimeEntity;


@NoArgsConstructor(access = PROTECTED)
@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
public class SsbFeed extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "uid", nullable = false)
    private User user;

    @Column(nullable = false)
    private Long contentsId;

    @Column(nullable = false)
    @Enumerated(value = STRING)
    private ContentsType contentsType;

    // 최초 공개 시간
    @DateTimeFormat(pattern = "yyyy:MM:dd HH:mm:ss")
    private LocalDateTime releaseDateTime;

    public static SsbFeed create(long contentsId, User user, ContentsType contentsType) {
        SsbFeed ssbFeed = new SsbFeed();
        ssbFeed.setUser(user);
        ssbFeed.setContentsType(contentsType);
        ssbFeed.setContentsId(contentsId);

        return ssbFeed;
    }

    public static void updateReleaseDateTime(SsbFeed ssbFeed, LocalDateTime releaseDateTime) {
        ssbFeed.setReleaseDateTime(releaseDateTime);
    }
}
