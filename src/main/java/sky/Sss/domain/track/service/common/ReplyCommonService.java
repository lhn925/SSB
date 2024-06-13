package sky.Sss.domain.track.service.common;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.LikeSimpleInfoDto;
import sky.Sss.domain.track.dto.common.ReplyRmInfoDto;
import sky.Sss.domain.track.dto.common.ReplySaveReqDto;
import sky.Sss.domain.track.dto.playlist.reply.PlyReplySaveReqDto;
import sky.Sss.domain.track.dto.track.redis.BaseRedisReplyDto;
import sky.Sss.domain.track.dto.track.reply.ReplyRmReqDto;
import sky.Sss.domain.track.dto.track.reply.TrackReplySaveReqDto;
import sky.Sss.domain.track.entity.playList.SsbPlayListSettings;
import sky.Sss.domain.track.entity.playList.reply.SsbPlyReply;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.reply.SsbTrackReply;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.service.playList.PlyQueryService;
import sky.Sss.domain.track.service.playList.reply.PlyReplyService;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.track.service.track.reply.TrackReplyService;
import sky.Sss.domain.user.dto.UserSimpleInfoDto;
import sky.Sss.domain.user.entity.User;
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
public class ReplyCommonService {


    private final UserQueryService userQueryService;
    private final TrackQueryService trackQueryService;
    private final PlyQueryService plyQueryService;
    private final PlyReplyService plyReplyService;
    private final TrackReplyService trackReplyService;
    // reply
    private final UserPushMsgService userPushMsgService;
    private final RedisCacheService redisCacheService;
    /**
     *
     * Reply 추가
     * @param replySaveReqDto
     * @param contentsType
     */
    @Transactional
    public void addReply(ReplySaveReqDto replySaveReqDto, ContentsType contentsType) {
        User user = userQueryService.findOne();
        // 댓글 달 track or playList id
        Long id = replySaveReqDto.getId();
        // 댓글 달 track or playList token
        String token = replySaveReqDto.getToken();

        User ownerUser = null;
        long replyId = 0;

        boolean isOwner = false;
        // 비공개인데 댓글을 달경우 isOwner 와 같이 체크 권한 있는지 확인 후 없으면 SsbTrackAccessDeniedException

        // track reply
        if (ContentsType.TRACK.equals(contentsType)) {
            SsbTrack ssbTrack = trackQueryService.findOneJoinUser(id, token, Status.ON);
            ownerUser = ssbTrack.getUser();
            // 작성자 인지 확인
            isOwner = ownerUser.getToken().equals(user.getToken());
            // 비공개 여부 체크
            checkPrivacy(ssbTrack.getIsPrivacy(), isOwner);
            // ssbTrackReply 생성
            SsbTrackReply ssbTrackReply = SsbTrackReply.create((TrackReplySaveReqDto) replySaveReqDto, user, ssbTrack);
            trackReplyService.addReply(ssbTrackReply, ssbTrack);
            replyId = ssbTrackReply.getId();
        } else { // playList reply
            SsbPlayListSettings ssbPlayListSettings = plyQueryService.findOneJoinUser(id, token, Status.ON);
            ownerUser = ssbPlayListSettings.getUser();
            // 작성자 여부 값
            isOwner = ownerUser.getToken().equals(user.getToken());
            // 비공개 여부 체크
            checkPrivacy(ssbPlayListSettings.getIsPrivacy(), isOwner);
            // ssbTrackReply 생성
            SsbPlyReply ssbPlyReply = SsbPlyReply.create((PlyReplySaveReqDto) replySaveReqDto, user,
                ssbPlayListSettings);
            plyReplyService.addReply(ssbPlyReply, ssbPlayListSettings);
            replyId = ssbPlyReply.getId();
        }

        String linkUrl = contentsType.getUrl() + id + "/" + replyId;
        userPushMsgService.sendPushToUserSet(replySaveReqDto.getUserTagSet(), replySaveReqDto.getContents(), PushMsgType.REPLY,
            ContentsType.HASHTAG, user, linkUrl, ownerUser, replyId, isOwner);
    }

    /**
     * 리플 삭제
     *
     * @param replyRmReqDto
     */
    @Transactional
    public void deleteReply(ReplyRmReqDto replyRmReqDto, ContentsType contentsType) {
        // 삭제를 요청한 사용자
        User user = userQueryService.findOne();

        // 댓글을 가져올때 대 댓글도 모두 검색 후 반환
        List<ReplyRmInfoDto> replyDtoList = getReplyRmInfoDtoList(replyRmReqDto.getReplyId(),
            replyRmReqDto.getReplyToken(), contentsType);

        ReplyRmInfoDto replyDto = replyDtoList.stream()
            .filter(reply -> reply.getReplyToken().equals(replyRmReqDto.getReplyToken()))
            .findFirst().orElseThrow(
                IllegalArgumentException::new);

        String token = replyDto.getTargetToken();

        // 요청자 아이디
        long uid = user.getId();
        // 트랙 작성 유저
        long trackOwnerUid = replyDto.getTargetOwner();

        // 댓글 작성자 고유 아이디 추출
        long ownerUid = replyDto.getReplyOwner();

        // 댓글 작성자도 아니고 트랙 작성자도 아닐시에
        // 권한 에러
        if (ownerUid != uid && trackOwnerUid != uid) {
            throw new SsbTrackAccessDeniedException("access.error.forbidden");
        }
        removeReplies(replyDtoList, token, contentsType);
    }

    private void removeReplies(List<ReplyRmInfoDto> replyDtoList, String token, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            trackReplyService.removeReplies(replyDtoList, token);
        } else {
            plyReplyService.removeReplies(replyDtoList, token);
        }

    }
    private List<ReplyRmInfoDto> getReplyRmInfoDtoList(Long replyId, String replyToken, ContentsType contentsType) {
        if (contentsType.equals(ContentsType.TRACK)) {
            return trackReplyService.getReplyRmInfoDtoList(replyId, replyToken);
        }
        return plyReplyService.getReplyRmInfoDtoList(replyId, replyToken);
    }
    public static void checkPrivacy(boolean isPrivacy, boolean isOwner) {
        if (isPrivacy && !isOwner) {
            throw new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN);
        }
    }
    public Map<String, Integer>  getTotalCountList(List<String> tokens, ContentsType contentsType) {
        int count;
        TypeReference<HashMap<String, BaseRedisReplyDto>> typeReference = new TypeReference<>() {
        };
        RedisDataListDto<HashMap<String, BaseRedisReplyDto>> dataList =
            redisCacheService.getDataList(tokens,
            typeReference, contentsType.getReplyKey());


        Map<String, HashMap<String, BaseRedisReplyDto>> replyMap = dataList.getResult();
        // 총 리플 수를 모을 맵
        Map<String, Integer> countMap = new HashMap<>();

        // 레디스에 있는 좋아요 수 countMap 에 put
        for (String targetToken : tokens) {
            count = 0;
            Map<String, BaseRedisReplyDto> simpleInfoDtoHashMap = replyMap.get(targetToken);
            if (simpleInfoDtoHashMap != null) {
                count = simpleInfoDtoHashMap.size();
            }
            countMap.put(targetToken, count);
        }

        // redis에 트랙 리플 수가 다 있을경우 그대로 반환
        if (dataList.getMissingKeys().isEmpty()) {
            return countMap;
        }
        List<BaseRedisReplyDto> baseRedisReplyList = new ArrayList<>();
        // contentsType 에 따라 sql 쿼리 구분
        // 토큰 으로 리플 수 검색 후
        // map 으로 변경후 캐쉬에 저장하고 좋아요 map 담은 후 반환
        if (contentsType.equals(ContentsType.TRACK)) {
            baseRedisReplyList.addAll(trackReplyService.getTrackRedisDtoListByTokens(
                dataList.getMissingKeys()));
        }
        // DB에서 탐색한 리플 수 저장
        if (!baseRedisReplyList.isEmpty()) {
            /**
             * targetToken: {replyToken:BaseRedisReplyDto}
             */
            Map<String, Map<String, BaseRedisReplyDto>> findMap = baseRedisReplyList.stream()
                .collect(Collectors.groupingBy(BaseRedisReplyDto::getTargetToken,
                    Collectors.mapping(dto -> dto, Collectors.toMap(BaseRedisReplyDto::getToken, value -> value))));
            // 레디스 저장
            // 리플 총 갯수 저장
            for (String findKey : findMap.keySet()) {
                Map<String, BaseRedisReplyDto> dtoMap = findMap.get(findKey);
                redisCacheService.upsertAllCacheMapValuesByKey(dtoMap, contentsType.getReplyKey() + findKey);
                countMap.put(findKey, dtoMap.size());
            }
        }
        return countMap;
    }
}
