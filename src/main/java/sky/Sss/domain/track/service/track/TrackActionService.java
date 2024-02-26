package sky.Sss.domain.track.service.track;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.service.PushMsgService;
import sky.Sss.domain.user.service.UserQueryService;


/**
 * 트랙과 관련된 사용자 활동을 모아 놓은 Service
 */
@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TrackActionService {

    private final TrackLikesService trackLikesService;

    /**
     * Track 좋아요 추가 후 총 좋아요 수 반환
     */
    @Transactional
    public void addLikes(SsbTrack ssbTrack, User fromUser) {
        // 좋아요가 있는지 확인
        // 좋아요가 이미 있는 경우 예외 처리
        boolean isLikes = trackLikesService.existsLikes(ssbTrack, fromUser);
        if (isLikes) {
            throw new IllegalArgumentException();
        }
        // 저장
        trackLikesService.addLike(SsbTrackLikes.create(fromUser, ssbTrack));

        // 좋아요 수 업로드
        updateLikesCount(ssbTrack);
    }

    /**
     * Track 좋아요 취소 후 총 좋아요 수 반환
     */
    @Transactional
    public void cancelLikes(SsbTrack ssbTrack,User user) {
        // 사용자 검색
        // 좋아요가 있는지 확인
        // 좋아요가 없는데 취소하는 경우 예외 처리
        boolean isLikes = trackLikesService.existsLikes(ssbTrack, user);
        if (!isLikes) {
            throw new IllegalArgumentException();
        }
        trackLikesService.cancelLike(ssbTrack, user);
        // 좋아요 수 업로드
        updateLikesCount(ssbTrack);
    }

    public void updateLikesCount(SsbTrack ssbTrack) {
        trackLikesService.updateTotalCount(ssbTrack.getToken());
    }

    public int getTotalLikesCount(String token) {
        return trackLikesService.getTotalCount(token);
    }
}
