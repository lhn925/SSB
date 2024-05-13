package sky.Sss.domain.user.service.push;


import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.push.PushMsgCacheDto;
import sky.Sss.domain.user.dto.push.PushMsgDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.entity.UserPushMessages;
import sky.Sss.domain.user.model.ContentsType;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.PushMsgType;
import sky.Sss.domain.user.repository.push.UserPushMsgRepository;
import sky.Sss.domain.user.service.MsgTemplateService;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserPushMsgService {

    /**
     * 알림을 DB 에 저장
     * <p>
     * key + userToken 으로 저장
     * 안 읽음 ,읽음 표시 , 삭제
     * <p>
     * redis 에 저장
     * <p>
     * 실시간 알림은
     * 누군가 좋아요를 눌렀을 때
     * key + 사용자토큰 에 있는 webSocket sessionId 를 찾아서
     * 알림
     */
    private final UserPushMsgRepository userPushMsgRepository;
    private final RedisCacheService redisCacheService;
    private final MsgTemplateService msgTemplateService;
    private final UserQueryService userQueryService;


    @Transactional
    public void addUserPushMsg(UserPushMessages userPushMessages) {
        userPushMsgRepository.save(userPushMessages);
    }


    // 실시간 알림(유저가 존재하는경우) and Cache 저장
    public void sendOrCacheMessages(String linkUrl, String insertedText, User toUser,
        UserPushMessages userPushMessages) {
        // 현재 유저가 webSocket 에 접속 되어 있는지 확인
        boolean isEmpty = redisCacheService.hasWsStatusOnUser(toUser.getToken());
        // redis 에 저장할 실시간 알림 여부
        boolean isPush = false;

        // redisCache 알림 목록에 저장
        if (!isEmpty) { // 있을 경우 실시간 알림 전송
            isPush = true;
            String topicUrl = "/topic/push/" + toUser.getUserId();
            PushMsgDto pushMsgDto = PushMsgDto.create(userPushMessages, insertedText, linkUrl);
            msgTemplateService.convertAndSend(topicUrl, pushMsgDto);
        }
        // Redis cache 알림 리스트에 추가
        this.addCachePushMsgList(userPushMessages, isPush, toUser.getToken());
    }

    @Transactional
    public void sendPushToUserSet(Set<String> userTagSet, String contents, PushMsgType pushMsgType,
        ContentsType contentsType,
        User user, String linkUrl, User ownerUser, long contentsId, boolean isOwner) {
        try {
            if (ownerUser != null) {
                // 작성자 태그 Set 에서 삭제
                userTagSet.remove(ownerUser.getUserName());
            }
            // 자기 자신을 태그 한 경우 삭제
            userTagSet.remove(user.getUserName());
            Set<User> users = new HashSet<>();

            // 작성자가 아닌 경우
            if (!isOwner) {
                //  작성자 추가
                users.add(ownerUser);
            }
            users.addAll(userQueryService.findUsersByUserNames(userTagSet, Enabled.ENABLED));

            users.forEach(toUser -> {
                UserPushMessages userPushMessages =
                    UserPushMessages.create(toUser, user, pushMsgType, contentsType, contentsId);
                this.addUserPushMsg(userPushMessages);
                this.sendOrCacheMessages(linkUrl, contents, toUser, userPushMessages);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 사용자가 ws에 없을 경우 실시간 알림을 Redis 에 Cache
    public void addCachePushMsgList(UserPushMessages userPushMessages, boolean isPush, String userToken) {
        PushMsgCacheDto pushMsgCacheDto = PushMsgCacheDto.create(userPushMessages, isPush);
        // mapKey: String
        // mapValue : SET
        TypeReference<HashMap<String, List<PushMsgCacheDto>>> type = new TypeReference<>() {
        };
        HashMap<String, List<PushMsgCacheDto>> userPushMap = null;
        List<PushMsgCacheDto> pushMsgCacheDtoList = null;
        // 현재 레디스에 있는 유저 알림 목록 반환
        String redisPushMsgListKey = RedisKeyDto.REDIS_PUSH_MSG_LIST_KEY;

        if (redisCacheService.hasRedis(redisPushMsgListKey)) { // 있는지 확인
            userPushMap = redisCacheService.getData(redisPushMsgListKey, type);
            pushMsgCacheDtoList = Optional.ofNullable(userPushMap.get(userToken)).orElse(new ArrayList<>());
        } else { //
            pushMsgCacheDtoList = new ArrayList<>();
        }
        pushMsgCacheDtoList.add(pushMsgCacheDto);
        // redis 에 저장
        redisCacheService.upsertCacheMapValueByKey(pushMsgCacheDtoList, redisPushMsgListKey, userToken);
    }
}
