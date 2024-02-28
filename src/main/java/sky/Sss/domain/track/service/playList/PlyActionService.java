package sky.Sss.domain.track.service.playList;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.SsbPlyLikes;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.service.UserQueryService;


/**
 * 트랙과 관련된 사용자 활동을 모아 놓은 Service
 *
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class PlyActionService {
    private final UserQueryService userQueryService;
    private final PlyLikesService plyLikesService;

    /**
     * playList 좋아요 추가 후 총 좋아요 수 반환
     */
    @Transactional
    public void addLikes (SsbPlayListSettings ssbPlayListSettings,User fromUser) {
        // 사용자 검색
        User user = userQueryService.findOne();

        // 좋아요가 있는지 확인
        // 좋아요가 이미 있는 경우 예외 처리
        boolean isLikes = plyLikesService.existsLikes(ssbPlayListSettings, user);
        if (isLikes) {
            throw new IllegalArgumentException();
        }
        // 저장
        plyLikesService.addLike(SsbPlyLikes.create(user,ssbPlayListSettings));

        // 좋아요 수 업로드
//        updateLikesCount(ssbPlayListSettings);
    }

    /**
     * playList 좋아요 취소 후 총 좋아요 수 반환
     */
    @Transactional
    public void cancelLikes (SsbPlayListSettings ssbPlayListSettings) {
        // 사용자 검색
        User user = userQueryService.findOne();
        // 좋아요가 있는지 확인
        // 좋아요가 없는데 취소하는 경우 예외 처리
        boolean isLikes = plyLikesService.existsLikes(ssbPlayListSettings, user);
        if (!isLikes) {
            throw new IllegalArgumentException();
        }
        plyLikesService.cancelLike(ssbPlayListSettings, user);
        // 좋아요 수 업로드
//        updateLikesCount(ssbPlayListSettings);
    }
/*
    public void updateLikesCount(SsbPlayListSettings ssbPlayListSettings) {
//        plyLikesService.updateTotalCount(ssbPlayListSettings.getToken());
    }*/
    public int getTotalLikesCount(String token) {
        return plyLikesService.getTotalCount(token);
    }
}
