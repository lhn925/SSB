package sky.Sss.domain.track.service.common;


import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.feed.entity.SsbFeed;
import sky.Sss.domain.feed.service.FeedService;
import sky.Sss.domain.track.dto.common.reply.BaseRedisReplyDto;
import sky.Sss.domain.track.dto.common.repost.RepostModifyReqDto;
import sky.Sss.domain.track.dto.common.repost.RepostInfoDto;
import sky.Sss.domain.track.dto.common.repost.RepostRedisDto;
import sky.Sss.domain.track.dto.common.repost.RepostRmReqDto;
import sky.Sss.domain.track.dto.common.repost.RepostSaveReqDto;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.dto.common.repost.RepostSimpleInfoDto;
import sky.Sss.domain.track.dto.track.rep.TotalCountRepDto;
import sky.Sss.domain.track.entity.SsbRepost;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
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
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
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
    public TotalCountRepDto addRepost(RepostSaveReqDto repostSaveReqDto) {
        User fromUser = userQueryService.findOne();

        ContentsType contentsType = repostSaveReqDto.getContentsType();

        long targetId = repostSaveReqDto.getTargetId();
        String targetToken = repostSaveReqDto.getTargetToken();

        boolean isRepost = existsRepost(targetToken, targetId, fromUser, contentsType);

        // 이미 repost 한적 있는지 확인
        if (isRepost) {
            throw new IllegalArgumentException();
        }
        TargetInfoDto targetInfoDto = getTargetInfoDto(repostSaveReqDto.getTargetId(), contentsType);
        User ownerUser = targetInfoDto.getToUser();
        boolean isOwner = ownerUser.getToken().equals(fromUser.getToken());
        // 자신의 게시물일 경우 repost 불가
        if (isOwner) {
            throw new IllegalArgumentException();
        }
        // 비공개 게시물 일 경우 repost 불가
        if (targetInfoDto.getIsPrivacy()) {
            throw new SsbTrackAccessDeniedException("repost.privacy.error", HttpStatus.FORBIDDEN);
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

        SsbFeed ssbFeed = SsbFeed.create(ssbRepost.getId(), fromUser, ContentsType.REPOST);

        SsbFeed.updateReleaseDateTime(ssbFeed, ssbRepost.getCreatedDateTime());

        feedService.addFeed(ssbFeed);
        /**
         *
         *  REDIS_REPOST_IDS_INFO_MAP_KEY : k:id , v:RepostRedisDto
         *  ssbRepost.getContentsType().getRepostKey() + ssbRepost.getToken() : userToken : simpleInfoDto
         *
         */
        setRepostRedisDtoRedis(fromUser, ssbRepost, targetToken);

        return new TotalCountRepDto(getTotalCount(targetToken, contentsType));
    }

    @Transactional
    public void updateComment(RepostModifyReqDto repostModifyReqDto) {
        User user = userQueryService.findOne();
        long repostId = repostModifyReqDto.getRepostId();
        String repostToken = repostModifyReqDto.getRepostToken();
        SsbRepost ssbRepost = findEntityOne(repostId, repostToken, user);

        String linkUrl = ssbRepost.getContentsType().getUrl() + "/" + ssbRepost.getContentsId();

        TargetInfoDto targetInfoDto = getTargetInfoDto(ssbRepost.getContentsId(), ssbRepost.getContentsType());

        // 비공개 일시 repost 에러 처리
        if (targetInfoDto != null && targetInfoDto.getIsPrivacy()) {
            throw new SsbTrackAccessDeniedException("repost.privacy.error", HttpStatus.FORBIDDEN);
        }
        SsbRepost.updateComment(ssbRepost, repostModifyReqDto.getComment());

        if (targetInfoDto != null) {
            setRepostRedisDtoRedis(user, ssbRepost, targetInfoDto.getTargetToken());
        }

        userPushMsgService.sendPushToUserSet(repostModifyReqDto.getUserTagSet(), repostModifyReqDto.getComment(),
            PushMsgType.REPOST, ContentsType.HASHTAG, user, linkUrl, null, ssbRepost.getContentsId(), true);

    }


    @Transactional
    public int deleteRepost(RepostRmReqDto repostRmReqDto) {
        User user = userQueryService.findOne();
        SsbRepost ssbRepost = findEntityOne(repostRmReqDto.getRepostId(), repostRmReqDto.getRepostToken(), user);

        TargetInfoDto targetInfoDto = getTargetInfoDto(ssbRepost.getContentsId(), ssbRepost.getContentsType());
        // 삭제
        this.removeRepostRedisDtoRedis(user, ssbRepost, targetInfoDto.getTargetToken());

        feedService.deleteFeed(user, ssbRepost.getId(), ContentsType.REPOST);

        repostRepository.delete(ssbRepost);
        return getTotalCount(targetInfoDto.getTargetToken(),
            repostRmReqDto.getContentsType());
    }


    private void setRepostRedisDtoRedis(User fromUser, SsbRepost ssbRepost, String targetToken) {
        redisCacheService.upsertCacheMapValueByKey(new RepostRedisDto(ssbRepost, targetToken),
            RedisKeyDto.REDIS_REPOST_IDS_INFO_MAP_KEY, String.valueOf(ssbRepost.getId()));
        String key = ssbRepost.getContentsType().getRepostKey() + targetToken;
        redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(fromUser), key, fromUser.getToken());
    }

    private void removeRepostRedisDtoRedis(User fromUser, SsbRepost ssbRepost, String targetToken) {
        redisCacheService.removeCacheMapValueByKey(new RepostRedisDto(),
            RedisKeyDto.REDIS_REPOST_IDS_INFO_MAP_KEY, String.valueOf(ssbRepost.getId()));
        String key = ssbRepost.getContentsType().getRepostKey() + targetToken;
        redisCacheService.removeCacheMapValueByKey(new UserSimpleInfoDto(), key, fromUser.getToken());
    }


    public SsbRepost findEntityOne(long repostId, String repostToken, User user) {
        return repostRepository.findOne(repostId, repostToken, user).orElseThrow(IllegalArgumentException::new);
    }


    public Map<String, Integer> getTotalCountList(List<String> tokens, ContentsType contentsType) {
        Map<String, Integer> countMap = new HashMap<>();
        List<RepostSimpleInfoDto> simpleInfoDtos = new ArrayList<>();

        RedisDataListDto<Map<String, RepostRedisDto>> dataList = redisCacheService.fetchAndCountFromRedis(tokens,
            contentsType.getRepostKey(), countMap);

        if (dataList.getMissingKeys().isEmpty()) {
            return countMap;
        }

        if (contentsType.equals(ContentsType.TRACK)) {
            simpleInfoDtos.addAll(repostSimpleInfoByTokens(dataList.getMissingKeys(), contentsType));
        }

        //
        if (!simpleInfoDtos.isEmpty()) {
            Map<String, RepostRedisDto> repostRedisMap = new HashMap<>();
            Map<String, Map<String, UserSimpleInfoDto>> userSimpleMap = new HashMap<>();

            // 스트림을 한 번만 사용하여 repostRedisMap과 userSimpleMap을 생성
            simpleInfoDtos.forEach(dto -> {
                String repostId = String.valueOf(dto.getRepostSimpleInfo().getId());
                String targetToken = dto.getRepostSimpleInfo().getTargetToken();
                String userToken = dto.getUserSimpleInfoDto().getToken();

                repostRedisMap.put(repostId, dto.getRepostSimpleInfo());

                /**
                 * userSimpleMap에서 targetToken 키를 기준으로 값을 검색
                 *
                 * 만약 targetToken에 해당하는 값이 존재하지 않으면, computeIfAbsent 메서드는 새로운 HashMap<>을 생성하여 userSimpleMap에 추가
                 */
                userSimpleMap.computeIfAbsent(targetToken, k -> new HashMap<>())
                    .put(userToken, dto.getUserSimpleInfoDto());
            });

            // userSimpleMap의 각 항목에 대해 작업 수행
            userSimpleMap.forEach((findKey, dtoMap) -> {
                log.info("targetToken = {}", findKey);
                countMap.put(findKey, dtoMap.size());
                redisCacheService.upsertAllCacheMapValuesByKey(dtoMap, contentsType.getRepostKey() + findKey);
            });
            redisCacheService.upsertAllCacheMapValuesByKey(repostRedisMap, RedisKeyDto.REDIS_REPOST_IDS_INFO_MAP_KEY);
        }
        return countMap;
    }


    public int getTotalCount(String targetToken, ContentsType contentsType) {
        String key = contentsType.getRepostKey() + targetToken;
        // redis 에 total 캐시가 있으면
        int totalCount = redisCacheService.getTotalCountByKey(new HashMap<>(), key);
        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (totalCount == 0) {
            List<RepostSimpleInfoDto> repostSimpleInfoDtoList = this.repostSimpleInfoDtoListByType(targetToken,
                contentsType);
            updateCaches(repostSimpleInfoDtoList, key);
            return repostSimpleInfoDtoList.size();

        }
        return totalCount;
    }

    private void updateCaches(List<RepostSimpleInfoDto> repostSimpleInfoDtoList, String key) {
        Map<String, UserSimpleInfoDto> simpleMapDto = new HashMap<>();
        Map<String, RepostRedisDto> repostInfoMap = new HashMap<>();
        for (RepostSimpleInfoDto dto : repostSimpleInfoDtoList) {
            UserSimpleInfoDto userSimpleInfoDto = dto.getUserSimpleInfoDto();
            RepostRedisDto repostRedisDto = dto.getRepostSimpleInfo();
            simpleMapDto.put(userSimpleInfoDto.getToken(), userSimpleInfoDto);
            repostInfoMap.put(String.valueOf(repostRedisDto.getId()), repostRedisDto);
        }

        redisCacheService.upsertAllCacheMapValuesByKey(simpleMapDto, key);
        redisCacheService.upsertAllCacheMapValuesByKey(repostInfoMap, RedisKeyDto.REDIS_REPOST_IDS_INFO_MAP_KEY);
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

    public List<RepostSimpleInfoDto> repostSimpleInfoDtoListByType(String targetToken, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return repostRepository.getRepostSimpleDtoJoinTrack(targetToken, contentsType);
        } else {
            return repostRepository.getRepostSimpleDtoJoinPly(targetToken, contentsType);
        }
    }

    public List<RepostSimpleInfoDto> repostSimpleInfoByTokens(Set<String> tokens, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return repostRepository.findRepostInfoJoinTrack(tokens, contentsType);
        } else {
            return repostRepository.findRepostInfoJoinPly(tokens, contentsType);
        }
    }


    /*

     */
    public boolean existsRepost(String targetToken, long targetId, User user, ContentsType contentsType) {
        String key = contentsType.getRepostKey() + targetToken;
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
                redisCacheService.upsertCacheMapValueByKey(new RepostRedisDto(ssbRepost.orElse(null), targetToken), key,
                    user.getToken());
            }
            isExists = ssbRepost.isPresent();
        }
        return isExists;
    }

    public Optional<SsbRepost> findOne(long targetId, long uid, ContentsType contentsType) {
        return repostRepository.findOne(uid, targetId, contentsType);
    }

    public List<RepostRedisDto> getRepostRedisDtoList(long targetId, ContentsType contentsType) {
        return repostRepository.getRepostRedisDtoList(targetId, contentsType);
    }

    @Transactional
    public void privacyAllUpdate(long contentsId, boolean isPrivacy, ContentsType contentsType) {
        repostRepository.privacyBatchUpdate(contentsId, isPrivacy, contentsType);
    }


    private TargetInfoDto getTargetInfoDto(Long targetId, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackQueryService.getTargetInfoDto(targetId,
                Status.ON);
        } else {
            return plyQueryService.getTargetInfoDto(targetId,
                Status.ON);
        }
    }

}
