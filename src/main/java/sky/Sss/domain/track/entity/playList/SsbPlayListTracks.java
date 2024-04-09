package sky.Sss.domain.track.entity.playList;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.global.base.BaseTimeEntity;



@Getter
@Setter(AccessLevel.PRIVATE)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SsbPlayListTracks extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "settings_id",nullable = false)
    private SsbPlayListSettings ssbPlayListSettings;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id",nullable = false)
    private SsbTrack ssbTrack;


    // 앞에 순서의 Id 를 나타냄 맨 앞일 경우 0
    private Long parentId;

    // 뒤 순서의 Id를 나타냄  맨 뒤 순서 일 경우 0
    private Long childId;

    // 현재 위치
    @Column(nullable = false)
    private Integer position;

    public static List<SsbPlayListTracks> createSsbPlayListTrackList(Map<Integer, SsbTrack> playListMap,
        SsbPlayListSettings ssbPlayListSettings) {
        List<SsbPlayListTracks> ssbPlayListTracksList = new ArrayList<>();
        for (Integer key : playListMap.keySet()) {
            SsbPlayListTracks ssbPlayListTracks = new SsbPlayListTracks();
            SsbTrack ssbTrack = playListMap.get(key);
            ssbPlayListTracks.setSsbTrack(ssbTrack);
            ssbPlayListTracks.setSsbPlayListSettings(ssbPlayListSettings);
            ssbPlayListTracks.setPosition(key);
            ssbPlayListTracksList.add(ssbPlayListTracks);
        }
        return ssbPlayListTracksList;
    }

    public static void changePosition(SsbPlayListTracks ssbPlayListTracks, Integer position) {
        ssbPlayListTracks.setPosition(position);
    }


    public static void changeParentId(SsbPlayListTracks ssbPlayListTracks, Long parentId) {
        ssbPlayListTracks.setParentId(parentId);
    }

    public static void changeChildId(SsbPlayListTracks ssbPlayListTracks, Long childId) {
        ssbPlayListTracks.setChildId(childId);
    }
}
