package sky.Sss.domain.track.controller.track;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sky.Sss.domain.track.dto.track.TotalLikesCountDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.service.track.TrackActionService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.annotation.UserAuthorize;
import sky.Sss.domain.user.dto.PushMsgCacheDto;
import sky.Sss.domain.user.dto.PushMsgDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.MsgTemplateService;
import sky.Sss.domain.user.service.PushMsgService;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;


/**
 * 트랙과 관련 사용자 활동을 모아놓은 Controller
 */
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/tracks/action")
@UserAuthorize
@RestController
public class TrackActionController {

    private final TrackActionService trackActionService;
    private final TrackQueryService trackQueryService;
    private final UserQueryService userQueryService;
    private final PushMsgService pushMsgService;
    private final MsgTemplateService msgTemplateService;
    private final RedisCacheService redisCacheService;
    /**
     * track 좋아요 등록
     */
    /**
     * @param id
     *     trackId
     * @return
     */
    @PostMapping("/likes/{id}")
    public ResponseEntity<TotalLikesCountDto> saveLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findOneJoinUser(id, Status.ON);
        // 사용자 검색
        User fromUser = userQueryService.findOne();
        // push 를 받을 사용자
        User toUser = ssbTrack.getUser();

        // like 추가
        trackActionService.addLikes(ssbTrack, fromUser);

        // 총 likes count
        int totalLikesCount = trackActionService.getTotalLikesCount(ssbTrack.getToken());

        // userPushMessages 객체 생성
        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            ContentsType.TRACK, ssbTrack.getId());

        // userPushMessages Table insert
        pushMsgService.addUserPushMsg(userPushMessages);

        // push messages
        pushMsgService.sendOrCacheMessages(ContentsType.TRACK.getUrl() + ssbTrack.getId(), ssbTrack.getTitle(), toUser,
            userPushMessages);
        // Redis 알림 리스트에 추가
        return ResponseEntity.ok(new TotalLikesCountDto(totalLikesCount));
    }

    /**
     * track 좋아요 취소 후 총 좋아요수 반환
     *
     * @param id
     *     trackId
     * @return
     */
    @DeleteMapping("/likes/{id}")
    public ResponseEntity<TotalLikesCountDto> removeLikes(@PathVariable Long id) {
        if (id == null || id == 0) {
            throw new IllegalArgumentException();
        }

        // 사용자 검색
        User user = userQueryService.findOne();
        // track 검색
        SsbTrack ssbTrack = trackQueryService.findById(id, Status.ON);
        trackActionService.cancelLikes(ssbTrack, user);

        int totalCount = trackActionService.getTotalLikesCount(ssbTrack.getToken());

        return ResponseEntity.ok(new TotalLikesCountDto(totalCount));
    }

    /**
     *
     * 좋아요 시 알림
     *
     *
     */

}
