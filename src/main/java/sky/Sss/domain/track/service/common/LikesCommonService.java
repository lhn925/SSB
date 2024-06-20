package sky.Sss.domain.track.service.common;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.like.LikeSimpleInfoDto;
import sky.Sss.domain.track.dto.common.like.LikedRedisDto;
import sky.Sss.domain.track.dto.common.rep.TargetInfoDto;
import sky.Sss.domain.track.dto.track.rep.TotalCountRepDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.playList.PlyLikesService;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyLikesService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyService;
import sky.Sss.domain.track.service.track.TrackLikesService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.track.service.track.reply.TrackReplyLikesService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.domain.user.service.push.UserPushMsgService;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.service.RedisCacheService;


/**
 * 트랙과 플레이리스트에 공통적인 기능을 담당할 서비스
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikesCommonService {

    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final PlyQueryService plyQueryService;
    private final PlyReplyService plyReplyService;
    private final TrackReplyService trackReplyService;
    private final TrackLikesService trackLikesService;
    private final PlyLikesService plyLikesService;
    private final TrackReplyLikesService trackReplyLikesService;
    private final PlyReplyLikesService plyReplyLikesService;
    // reply
    private final UserPushMsgService userPushMsgService;
    private final RedisCacheService redisCacheService;

    /**
     * 좋아요 추가
     *
     * @param id
     * @param token
     * @param contentsType
     * @return
     */
    @Transactional
    public TotalCountRepDto addLikes(Long id, String token, ContentsType contentsType) {
        // track 검색
        TargetInfoDto likeTargetInfoDto = getLikeTargetInfoDto(id, token, contentsType);
        // 사용자 검색
        User fromUser = userQueryService.findOne();
        // push 를 받을 사용자
        User toUser = likeTargetInfoDto.getToUser();

        // 같은 사용자인지 확인
        boolean isOwner = fromUser.getToken().equals(toUser.getToken());

        boolean isLikeType = contentsType.equals(ContentsType.TRACK) || contentsType.equals(ContentsType.PLAYLIST);

        // 트랙 혹은 플레이리스트인데 비공개일 경우 Like 불가
        if (isLikeType && likeTargetInfoDto.getIsPrivacy()) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }

        long targetId = likeTargetInfoDto.getTargetId();
        String targetToken = likeTargetInfoDto.getTargetToken();

        // like 추가
        addLikeAndType(contentsType, fromUser, targetId, targetToken);

        // 총 likes count
        int totalLikesCount = getTotalCount(targetToken, contentsType);

        // userPushMessages 객체 생성
        UserPushMessages userPushMessages = UserPushMessages.create(toUser, fromUser, PushMsgType.LIKES,
            contentsType, targetId);

        StringBuilder linkUrl = new StringBuilder(contentsType.getUrl());

        // 리플 like 인 경우 url 에 parentId(trackId,playList) 추가
        if (likeTargetInfoDto.getParentId() != 0) {
            linkUrl.append(likeTargetInfoDto.getParentId()).append("/");
        }
        linkUrl.append(targetId);

        try {
            // 같은 사용자 인지 확인
            // userPushMessages Table insert
            userPushMsgService.addUserPushMsg(userPushMessages);
            // push messages
            userPushMsgService.sendOrCacheMessages(linkUrl.toString(),
                likeTargetInfoDto.getTargetContents(),
                toUser,
                userPushMessages);
        } catch (Exception ex) {
            log.info("ex.getMessage() = {}", ex.getMessage());

        }
        // Redis 알림 리스트에 추가
        return new TotalCountRepDto(totalLikesCount);
    }

    @Transactional
    public TotalCountRepDto cancelLikes(Long targetId, String targetToken, ContentsType contentsType) {
        // 사용자 검색
        User user = userQueryService.findOne();
        // track 검색
        TargetInfoDto targetInfoDto = getLikeTargetInfoDto(targetId, targetToken, contentsType);

        cancelLikeAndType(targetInfoDto.getTargetId(), targetInfoDto.getTargetToken(), user, contentsType);

        int totalCount = getTotalCount(targetInfoDto.getTargetToken(), contentsType);

        return new TotalCountRepDto(totalCount);
    }

    private void cancelLikeAndType(long targetId, String targetToken, User user, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            trackLikesService.cancelLikes(targetId, targetToken, user);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            plyLikesService.cancelLikes(targetId, targetToken, user);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            trackReplyLikesService.cancelLikes(targetId, targetToken, user);
        } else {
            plyReplyLikesService.cancelLikes(targetId, targetToken, user);
        }
        // 유저 like 리스트 삭제
        redisCacheService.removeCacheMapValueByKey(new LikedRedisDto(),
            contentsType.getUserLikedKey() + user.getToken(),
            String.valueOf(targetId));
    }

    private void addLikeAndType(ContentsType contentsType, User fromUser, long targetId, String targetToken) {
        boolean isLikes = existsLikes(targetToken, targetId, fromUser, contentsType);

        LikedRedisDto likedRedisDto = null;
        if (isLikes) {
            // 좋아요가 있는지 확인
            // 좋아요가 이미 있는 경우 예외 처리
            throw new IllegalArgumentException();
        }
        if (contentsType.equals(ContentsType.TRACK)) {
            likedRedisDto = trackLikesService.addLikes(targetId, targetToken, fromUser);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            likedRedisDto = plyLikesService.addLikes(targetId, targetToken, fromUser);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            likedRedisDto = trackReplyLikesService.addLikes(targetId, targetToken, fromUser);
        } else {
            likedRedisDto = plyReplyLikesService.addLikes(targetId, targetToken, fromUser);
        }
        if (likedRedisDto != null) {
            cacheUserLikedAdd(contentsType, fromUser, likedRedisDto);
        }
    }

    // 유저가 좋아요한 목록 추가
    private void cacheUserLikedAdd(ContentsType contentsType, User fromUser, LikedRedisDto likedRedisDto) {
        redisCacheService.upsertCacheMapValueByKey(
            likedRedisDto, contentsType.getUserLikedKey() + fromUser.getToken(),
            String.valueOf(likedRedisDto.getTargetId()));
    }

    /**
     * 좋아요 눌렀는지 확인
     */
    public boolean existsLikes(String targetToken, long targetId, User user, ContentsType contentsType) {

        boolean isExists;
        String key = contentsType.getLikeKey() + targetToken;
        // redis 에 있는지 확인
        isExists = redisCacheService.existsBySubKey(user.getToken(), key);

        LikedRedisDto likedRedisDto;
        if (!isExists) {
            if (contentsType.equals(ContentsType.TRACK)) {
                likedRedisDto = trackLikesService.getLikedRedisDto(targetId, user);
            } else if (contentsType.equals(ContentsType.PLAYLIST)) {
                likedRedisDto = plyLikesService.getLikedRedisDto(targetId, user);
            } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
                likedRedisDto = trackReplyLikesService.getLikedRedisDto(targetId, user);
            } else {
                likedRedisDto = plyReplyLikesService.getLikedRedisDto(targetId, user);
            }
            // 만약 레디스에는 없고 디비에는 있으면
            isExists = likedRedisDto != null;
            if (isExists) {
                redisCacheService.upsertCacheMapValueByKey(new UserSimpleInfoDto(user), key, user.getToken());
                cacheUserLikedAdd(contentsType, user, likedRedisDto);
            }
        }
        return isExists;
    }


    private TargetInfoDto getLikeTargetInfoDto(Long targetId, String targetToken, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackQueryService.getTargetInfoDto(targetId, Status.ON);
        } else if (contentsType.equals(ContentsType.PLAYLIST)) {
            return plyQueryService.getTargetInfoDto(targetId, targetToken, Status.ON);
        } else if (contentsType.equals(ContentsType.REPLY_TRACK)) {
            return trackReplyService.getTargetInfoDto(targetId, targetToken);
        } else {
            return plyReplyService.getTargetInfoDto(targetId, targetToken);
        }
    }

    private List<User> getUserList(String targetToken, ContentsType contentsType) {
        List<User> users = null;
        switch (contentsType) {
            case TRACK -> users = trackLikesService.getUserList(targetToken);
            case PLAYLIST -> users = plyLikesService.getUserList(targetToken);
            case REPLY_TRACK -> users = trackReplyLikesService.getUserList(targetToken);
            case REPLY_PLAYLIST -> users = plyReplyLikesService.getUserList(targetToken);
        }
        return users;
    }


    /**
     * profileUser 가 좋아요한 트랙을 가져오는 method
     * 다만, currentUser 소유가 아닌 비공개 트랙 좋아요는 제외한다
     * @param currentUser
     * @param profileUser
     * @param contentsType
     * @return
     */
    public List<LikedRedisDto> getVisibleLikeTracksForUser(User currentUser, User profileUser,
        ContentsType contentsType) {
        TypeReference<Map<String, LikedRedisDto>> typeReference = new TypeReference<>() {
        };
        Map<String, LikedRedisDto> likedRedisMap = redisCacheService.getData(
            contentsType.getUserLikedKey() + profileUser.getToken(),
            typeReference);

        if (likedRedisMap != null && !likedRedisMap.isEmpty()) {
            // 비공개 및 status off 제외
            return new ArrayList<>(filterAndCacheLikedItems(currentUser, profileUser, contentsType, likedRedisMap).values());
        }

        List<LikedRedisDto> likedRedisDtoList = new ArrayList<>();
        switch (contentsType) {
            case TRACK -> {
                likedRedisDtoList.addAll(
                    trackLikesService.getLikedRedisDtoList(profileUser, Sort.by(Order.desc("id"))));
            }
        }
        if (likedRedisDtoList.isEmpty()) {
            return likedRedisDtoList;
        }
        //
        Map<String, LikedRedisDto> filterMap = likedRedisDtoList.stream()
            .collect(Collectors.toMap(key -> String.valueOf(key.getTargetId()), value -> value));
        // 비공개 및 status off 제외
        Map<String, LikedRedisDto> redisDtoMap = filterAndCacheLikedItems(currentUser, profileUser, contentsType,
            filterMap);

        return new ArrayList<>(redisDtoMap.values());
    }


    // 비공개 및 status off 제외 후
    // redis에 저장
    private Map<String, LikedRedisDto> filterAndCacheLikedItems(User currentUser,
        User profileUser,
        ContentsType contentsType,
        Map<String, LikedRedisDto> likedRedisDtoMap) {

        Set<Long> likedItemIds = likedRedisDtoMap.keySet().stream().map(Long::valueOf).collect(Collectors.toSet());
        // redis 에 저장할 Map statusOff 제외
        Map<String, LikedRedisDto> cachingMap = new HashMap<>();
        Map<String, LikedRedisDto> filteredLikedMap = new HashMap<>();

        // status 제외
        if (contentsType.equals(ContentsType.TRACK)) {
            List<SsbTrack> ssbTrackList = trackQueryService.getTrackListFromOrDbByIds(likedItemIds);
            for (SsbTrack ssbTrack : ssbTrackList) {
                String targetId = String.valueOf(ssbTrack.getId());
                boolean isOwner = ssbTrack.getUser().getToken().equals(currentUser.getToken());

                // status가 false인 항목은 제외
                if (!ssbTrack.getIsStatus()) {
                    continue;
                }
                LikedRedisDto likedRedisDto = likedRedisDtoMap.get(targetId);

                // 필터링 조건: 오너이면서 자신의 트랙, 또는 오너가 아닌 공개 트랙
                if (isOwner || !ssbTrack.getIsPrivacy()) {
                    filteredLikedMap.put(targetId, likedRedisDto);
                }
                // Redis에 저장할 Map 구성
                cachingMap.put(targetId, likedRedisDto);
            }
            // 비공개 및 status off 제외
        }
        redisCacheService.upsertAllCacheMapValuesByKey(cachingMap,
            contentsType.getUserLikedKey() + profileUser.getToken());
        return filteredLikedMap;
    }


    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 반환 검색
    public int getTotalCount(String targetToken, ContentsType contentsType) {
        String key = contentsType.getLikeKey() + targetToken;
        // redis 에 total 캐시가 있으면
        int count = redisCacheService.getTotalCountByKey(new HashMap<>(), key);

        // redis 에 저장이 안되어 있을경우 count 후 저장
        if (count == 0) {
            List<User> users = getUserList(targetToken, contentsType);
            if (!users.isEmpty()) {
                count = users.size();
                redisCacheService.updateCacheMapValueByKey(key, users);
            }
        }
        return count;
    }


    // likes Total 레디스에서 검색 후 존재하지 않으면 DB 검색 후 map 형태로 반환
    public Map<String, Integer> getTotalCountMap(List<String> tokens, ContentsType contentsType) {
        // 총 like수를 모을 맵
        Map<String, Integer> countMap = new HashMap<>();

        RedisDataListDto<Map<String, UserSimpleInfoDto>> dataList = redisCacheService.fetchAndCountFromRedis(tokens,
            contentsType.getLikeKey(), countMap);

        // redis에 트랙 좋아요 수가 다 있을경우 그대로 반환
        if (dataList.getMissingKeys().isEmpty()) {
            return countMap;
        }
        List<LikeSimpleInfoDto> likeSimpleList = new ArrayList<>();

        // contentsType 에 따라 sql 쿼리 구분
        // 토큰 으로 좋아요 수 검색 후
        // map 으로 변경후 캐쉬에 저장하고 좋아요 map 담은 후 반환
        if (contentsType.equals(ContentsType.TRACK)) {
            likeSimpleList.addAll(trackLikesService.getLikeSimpleListByTokens(
                dataList.getMissingKeys()));
        }
        // DB에서 탐색한 좋아요 수 저장
        if (!likeSimpleList.isEmpty()) {
            Map<String, Map<String, UserSimpleInfoDto>> findMap = likeSimpleList.stream()
                .collect(Collectors.groupingBy(LikeSimpleInfoDto::getToken,
                    Collectors.mapping(LikeSimpleInfoDto::getUserSimpleInfoDto, Collectors.toMap(
                        UserSimpleInfoDto::getToken, value -> value))));

            // 레디스 저장
            // 하트 총 갯수 저장
            for (String findKey : findMap.keySet()) {
                Map<String, UserSimpleInfoDto> dtoMap = findMap.get(findKey);
                redisCacheService.upsertAllCacheMapValuesByKey(dtoMap, contentsType.getLikeKey() + findKey);
                countMap.put(findKey, dtoMap.size());
            }
        }
        return countMap;
    }


}
