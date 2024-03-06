package sky.Sss.domain.track.service.track.play;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.track.TrackService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;

/**
 * track 조회수 증가 및 재생 파일 가져오기
 */

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackPlayService {


    private final TrackService trackService;
    private final UserQueryService userQueryService;
    /**
     * 비공개 확인
     * 본인 여부
     * 비회원 여부
     * 한시간 여부
     */
    /**
     * @param id
     *     trackId
     * @return
     */
    public UrlResource getTrackPlayFile(Long id, String token) {
        SsbTrack ssbTrack = trackService.findOne(id, token, Status.ON);
        // getTrackPlayFile 권한이 없을 경우
        User playUser = userQueryService.findOne(); // 요청한 유저

        // 요청한 사용자가 비회원인지 확인 비회원이 아닐 경우
        // 해당 요청한 track 에 소유자인지 확인
        boolean isOwnerPost = ssbTrack.getUser().equals(playUser);

        // 요청한 사용자가 해당 track(비공개) 에 권한이 없는경우 예외 발생
        if (ssbTrack.getIsPrivacy() && !isOwnerPost) {// 비공개 일경우
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }

        return trackService.getSsbTrackFile(
            ssbTrack.getToken() + "/" + ssbTrack.getStoreFileName());
    }
}

