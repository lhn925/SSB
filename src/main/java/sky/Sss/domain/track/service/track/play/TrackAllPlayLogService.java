package sky.Sss.domain.track.service.track.play;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.track.redis.RedisPlayLogDto;
import sky.Sss.domain.track.entity.track.log.SsbTrackAllPlayLogs;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbTrackAccessDeniedException;
import sky.Sss.domain.track.model.ChartStatus;
import sky.Sss.domain.track.model.PlayStatus;
import sky.Sss.domain.track.repository.track.play.TrackAllPlayLogRepository;
import sky.Sss.domain.track.service.track.TrackQueryService;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Status;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

/**
 * 플레이 조회수 관련 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TrackAllPlayLogService {

    private final TrackAllPlayLogRepository trackAllPlayLogRepository;
    private final RedisCacheService redisCacheService;
    private final TrackQueryService trackQueryService;

    /**
     * "
     * 1분 이상 들으면 플레이 횟수 증가 1분미만은 전부다,
     * 들어야함 정지를 해도 넘겨들어도 1분 이상 채우면 횟수 증가-> 이건 차트에 반영 안됨 단순 조회수
     * 조건을 충족한뒤 일정시간(10초) 이 지난후 집계
     * <p>
     * 한시간에 한번 플레이는 차트에 반영
     * 플레이 도중 정지 및 넘김 시 해당 플레이 집계x -> 차트에 반영 되는 조회수
     * 차트 순위는 08~24 TOP 100은 24시간 조회수 50% + 1시간 이용량 50%
     * 01~07시 : 24시간 이용량 100%
     * 일간: 매일 낮 12시 기준 최근 24시간 이용량을 집계하며, 매일 13시 이후 업데이트된다.
     * 주간: 매주 월요일 낮 12시 기준 최근 7일 간 이용량을 집계하며, 매주 월요일 14시 이후 업데이트된다.
     * 월간: 매월 1일 낮 12시 기준 최근 1개월 간 이용량을 집계하며, 매월 1일 15시 이후 업데이트된다.
     *
     * @return
     */
    @Transactional
    public void add(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        trackAllPlayLogRepository.save(ssbTrackAllPlayLogs);
        redisCacheService.upsertCacheMapValueByKey(
            RedisPlayLogDto.create(ssbTrackAllPlayLogs)
            , RedisKeyDto.REDIS_PLAY_LOG_DTO_MAP_KEY,
            ssbTrackAllPlayLogs.getToken());
    }

    public SsbTrackAllPlayLogs findOne(User user, SsbTrack ssbTrack, String token, ChartStatus chartStatus) {
        return trackAllPlayLogRepository.findOne(token, user, ssbTrack, chartStatus)
            .orElseThrow(IllegalArgumentException::new);
    }

    public SsbTrackAllPlayLogs findOne(User user, SsbTrack ssbTrack, String token, PlayStatus playStatus) {
        return trackAllPlayLogRepository.findOne(token, user, ssbTrack, playStatus)
            .orElseThrow(IllegalArgumentException::new);
    }

    public SsbTrackAllPlayLogs findOne(long trackId, String playToken) {
        return trackAllPlayLogRepository.findOne(trackId, playToken)
            .orElseThrow(() -> new SsbTrackAccessDeniedException("track.error.forbidden", HttpStatus.FORBIDDEN));
    }

    public RedisPlayLogDto getPlayDto(long trackId, String playToken) {
        // 삭제처리
        trackQueryService.findById(trackId, Status.ON);
        RedisPlayLogDto redisPlayLogDto = redisCacheService.getCacheMapValueBySubKey(RedisPlayLogDto.class, playToken,
            RedisKeyDto.REDIS_PLAY_LOG_DTO_MAP_KEY);
        if (redisPlayLogDto == null || redisPlayLogDto.getTrackId() != trackId) {
            return RedisPlayLogDto.create(findOne(trackId, playToken));
        }
        return redisPlayLogDto;
    }


    public List<RedisPlayLogDto> getRedisPlayLogByTrackTokens(Set<String> trackTokens, PlayStatus playStatus) {
        return trackAllPlayLogRepository.getRedisPlayLogDtoList(trackTokens, playStatus);
    }


    // 조회수 에 반영되는 로그들을 모아 놓은 캐쉬
    public void completeLogSaveRedisCache(SsbTrackAllPlayLogs ssbTrackAllPlayLogs) {
        if (ssbTrackAllPlayLogs.getPlayStatus().equals(PlayStatus.INCOMPLETE)) {
            return;
        }
        String token = ssbTrackAllPlayLogs.getSsbTrack().getToken();
        String key = RedisKeyDto.REDIS_TRACK_PLAY_LOG_MAP_KEY + token;
        redisCacheService.upsertCacheMapValueByKey(RedisPlayLogDto.create(ssbTrackAllPlayLogs), key,
            ssbTrackAllPlayLogs.getToken());
    }

    public Map<String, Integer> getTotalCountMap(List<String> tokens) {
        int count;
        TypeReference<HashMap<String, RedisPlayLogDto>> typeReference = new TypeReference<>() {
        };
        RedisDataListDto<HashMap<String, RedisPlayLogDto>> dataList =
            redisCacheService.getDataList(tokens,
                typeReference, RedisKeyDto.REDIS_TRACK_PLAY_LOG_MAP_KEY);

        Map<String, HashMap<String, RedisPlayLogDto>> replyMap = dataList.getResult();
        // 총 리플 수를 모을 맵
        Map<String, Integer> countMap = new HashMap<>();

        // 레디스에 있는 좋아요 수 countMap 에 put
        for (String logToken : tokens) {
            count = 0;
            Map<String, RedisPlayLogDto> redisPlayLogDtoHashMap = replyMap.get(logToken);
            if (redisPlayLogDtoHashMap != null) {
                count = redisPlayLogDtoHashMap.size();
            }
            countMap.put(logToken, count);
        }

        // redis에 트랙 리플 수가 다 있을경우 그대로 반환
        if (dataList.getMissingKeys().isEmpty()) {
            return countMap;
        }
        // contentsType 에 따라 sql 쿼리 구분
        // 토큰 으로 리플 수 검색 후
        // map 으로 변경후 캐쉬에 저장하고 좋아요 map 담은 후 반환
        List<RedisPlayLogDto> trackPlayLogList = new ArrayList<>(getRedisPlayLogByTrackTokens(
            dataList.getMissingKeys(), PlayStatus.COMPLETED));
        // DB에서 탐색한 리플 수 저장
        if (!trackPlayLogList.isEmpty()) {
            /**
             * targetToken: {replyToken:BaseRedisReplyDto}
             */
            Map<String, Map<String, RedisPlayLogDto>> findMap = trackPlayLogList.stream()
                .collect(Collectors.groupingBy(RedisPlayLogDto::getTrackToken,
                    Collectors.mapping(dto -> dto, Collectors.toMap(RedisPlayLogDto::getToken, value -> value))));
            // 레디스 저장
            // 리플 총 갯수 저장
            for (String findKey : findMap.keySet()) {
                Map<String, RedisPlayLogDto> dtoMap = findMap.get(findKey);
                redisCacheService.upsertAllCacheMapValuesByKey(dtoMap, RedisKeyDto.REDIS_TRACK_PLAY_LOG_MAP_KEY + findKey);
                countMap.put(findKey, dtoMap.size());
            }
        }
        return countMap;
    }


}

