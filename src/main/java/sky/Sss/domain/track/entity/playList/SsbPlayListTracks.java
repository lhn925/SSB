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
    @JoinColumn(name = "setting_id",nullable = false)
    private SsbPlayListSettings ssbPlayListSettings;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "track_id",nullable = false)
    private SsbTrack ssbTrack;

    @Column(nullable = false)
    private Integer orders;

    public static List<SsbPlayListTracks> createSsbPlayListTrackList(Map<Integer, SsbTrack> playListMap,
        SsbPlayListSettings ssbPlayListSettings) {
        List<SsbPlayListTracks> ssbPlayListTracksList = new ArrayList<>();
        for (Integer key : playListMap.keySet()) {
            SsbPlayListTracks ssbPlayListTracks = new SsbPlayListTracks();
            SsbTrack ssbTrack = playListMap.get(key);
            ssbPlayListTracks.setSsbTrack(ssbTrack);
            ssbPlayListTracks.setSsbPlayListSettings(ssbPlayListSettings);
            ssbPlayListTracks.setOrders(key);
            ssbPlayListTracksList.add(ssbPlayListTracks);
        }
        return ssbPlayListTracksList;
    }

    public static void changeOrders(SsbPlayListTracks ssbPlayListTracks, Integer orders) {
        ssbPlayListTracks.setOrders(orders);
    }

}
