package sky.Sss.domain.track.service.common;


import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.service.FeedService;
import sky.Sss.domain.track.dto.common.repost.RepostModifyReqDto;
import sky.Sss.domain.track.dto.common.repost.RepostInfoDto;
import sky.Sss.domain.track.dto.common.repost.RepostRmReqDto;
import sky.Sss.domain.track.dto.common.repost.RepostSaveReqDto;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.entity.SsbRepost;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.repository.common.RepostRepository;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.global.redis.service.RedisCacheService;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RepostCommonService {

    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final PlyQueryService plyQueryService;
    private final RepostRepository repostRepository;
    private final UserPushMsgService userPushMsgService;
    private final RedisCacheService redisCacheService;
    private final FeedService feedService;

    @Transactional
    public void addRepost(RepostSaveReqDto repostSaveReqDto) {
        User fromUser = userQueryService.findOne();

        ContentsType contentsType = repostSaveReqDto.getContentsType();

        long targetId = repostSaveReqDto.getTargetId();
        String targetToken = repostSaveReqDto.getTargetToken();

        boolean isRepost = existsRepost(targetToken, targetId, fromUser, contentsType);

        // 이미 repost 한적 있는지 확인
        if (isRepost) {
            throw new IllegalArgumentException();
        }
        TargetInfoDto targetInfoDto = getTargetInfoDto(repostSaveReqDto, contentsType);
        User ownerUser = targetInfoDto.getToUser();
        boolean isOwner = ownerUser.getToken().equals(fromUser.getToken());
        // 자신의 게시물일 경우 repost 불가
        if (isOwner) {
            throw new IllegalArgumentException();
        }
        // 비공개 게시물 일 경우 repost 불가
        if (targetInfoDto.getIsPrivacy()) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
        // 객체 생성
        SsbRepost ssbRepost = SsbRepost.create(targetInfoDto, contentsType, fromUser);

        SsbRepost.updateIsPrivacy(ssbRepost, targetInfoDto.getIsPrivacy());
        repostRepository.save(ssbRepost);

        UserPushMessages userPushMessages = UserPushMessages.create(ownerUser, fromUser, PushMsgType.REPOST,
            contentsType, targetInfoDto.getTargetId());

        userPushMsgService.addUserPushMsg(userPushMessages);

        // push Msg 전달
        String linkUrl = contentsType.getUrl() + targetId;
        userPushMsgService.sendOrCacheMessages(linkUrl, fromUser.getUserName(), ownerUser, userPushMessages);
        String key = getRepostKey(contentsType, targetInfoDto.getTargetToken());

        SsbFeed ssbFeed = SsbFeed.create(ssbRepost.getId(), fromUser, ContentsType.REPOST);

        SsbFeed.updateReleaseDateTime(ssbFeed, ssbRepost.getCreatedDateTime());


        feedService.addFeed(ssbFeed);

        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(fromUser), key, fromUser.getToken());
    }


    @Transactional
    public void updateComment(RepostModifyReqDto repostModifyReqDto) {
        User user = userQueryService.findOne();
        long repostId = repostModifyReqDto.getRepostId();
        String repostToken = repostModifyReqDto.getRepostToken();
        SsbRepost ssbRepost = findOne(repostId, repostToken, user);

        SsbRepost.updateComment(ssbRepost, repostModifyReqDto.getComment());

        String linkUrl = ssbRepost.getContentsType().getUrl() + "/" + ssbRepost.getContentsId();
        userPushMsgService.sendPushToUserSet(repostModifyReqDto.getUserTagSet(), repostModifyReqDto.getComment(),
            PushMsgType.REPOST, ContentsType.HASHTAG, user, linkUrl, null, ssbRepost.getContentsId(), true);

    }


    @Transactional
    public int deleteRepost(RepostRmReqDto repostRmReqDto) {
        User user = userQueryService.findOne();
        RepostInfoDto repostInfoDto = findOneByType(repostRmReqDto.getRepostId(), repostRmReqDto.getRepostToken(), user,
            repostRmReqDto.getContentsType());
        repostRepository.delete(repostInfoDto.getSsbRepost());
        ContentsType contentsType = repostInfoDto.getSsbRepost().getContentsType();
        String key = getRepostKey(contentsType, repostInfoDto.getTargetToken());
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, user.getToken());
        feedService.deleteFeed(user, repostInfoDto.getSsbRepost().getId(), ContentsType.REPOST);
        return getTotalCount(repostInfoDto.getTargetId(), repostInfoDto.getTargetToken(),
            repostRmReqDto.getContentsType());
    }

    public RepostInfoDto findOneByType(long repostId, String repostToken, User user, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return repostRepository.findOneJoinType(repostId, repostToken, user)
                .orElseThrow(IllegalArgumentException::new);
        } else {
            return repostRepository.findOneJoinPlayList(repostId, repostToken, user)
                .orElseThrow(IllegalArgumentException::new);
        }
    }




    public SsbRepost findOne(long repostId, String repostToken, User user) {
        return repostRepository.findOne(repostId, repostToken, user).orElseThrow(IllegalArgumentException::new);
    }

    private String getRepostKey(ContentsType contentsType, String targetToken) {
        return contentsType.getRepostKey() + targetToken;
    }


    public int getTotalCount(long targetId, String targetToken, ContentsType contentsType) {
        String key = getRepostKey(contentsType, targetToken);
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            List<User> users = this.getUserList(targetId, contentsType);
            if (!users.isEmpty()) {
                count = users.size();
                redisCacheService.updateCacheMapValueByKey(key, users);
            }
        }
        return count;
    }

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsRepost(String targetToken, long targetId, User user, ContentsType contentsType) {
        String key = getRepostKey(contentsType, targetToken);
        boolean isExists = false;
        // redis 에 있는지 확인
        if (redisCacheService.hasRedis(key)) {
            isExists = redisCacheService.existsBySubKey(user.getToken(), key);
        }
        // 레디스에 없으면 DB 확인
        if (!isExists) {
            Optional<SsbRepost> ssbRepost = findOne(targetId, user.getId(), contentsType);
            // 만약 레디스에는 없고 디비에는 있으면
            if (ssbRepost.isPresent()) {
                redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
            }
            isExists = ssbRepost.isPresent();
        }
        return isExists;
    }

    public Optional<SsbRepost> findOne(long targetId, long uid, ContentsType contentsType) {
        return repostRepository.findOne(uid, targetId, contentsType);
    }

    public List<User> getUserList(long targetId, ContentsType contentsType) {
        return repostRepository.getUserList(targetId, contentsType);
    }

    @Transactional
    public void privacyAllUpdate(long contentsId,boolean isPrivacy, ContentsType contentsType) {
        repostRepository.privacyBatchUpdate(contentsId, isPrivacy ,contentsType);
    }


    private TargetInfoDto getTargetInfoDto(RepostSaveReqDto repostSaveReqDto, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackQueryService.getTargetInfoDto(repostSaveReqDto.getTargetId(), repostSaveReqDto.getTargetToken(),
                Status.ON);
        } else {
            return plyQueryService.getTargetInfoDto(repostSaveReqDto.getTargetId(), repostSaveReqDto.getTargetToken(),
                Status.ON);
        }
    }


}
