package sky.Sss.domain.track.service.track;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacpp.presets.opencv_core.Str;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.track.redis.RedisTrackDto;
import sky.Sss.domain.track.dto.track.rep.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
import sky.Sss.global.redis.dto.RedisDataListDto;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TrackQueryService {

    private final TrackQueryRepository trackQueryRepository;
    private final RedisCacheService redisCacheService;
    private final UserQueryService userQueryService;

    public SsbTrack getEntityTrack(Long id, String token, User user, Status isStatus) {
        SsbTrack ssbTrack = fetchAndSetSubKeyRedisBySubKey(id);
        if (ssbTrack == null || !ssbTrack.getToken().equals(token) || !user.getToken().equals(ssbTrack.getToken()) ||
            !isStatus.getValue().equals(ssbTrack.getIsStatus())) {
            throw new SsbFileNotFoundException();
        }
        return ssbTrack;
    }

    public List<TrackInfoRepDto> getTrackInfoRepDto(List<String> tokenList, User user, Status isStatus) {
        List<SsbTrack> ssbTrackList = trackQueryRepository.findAllByToken(tokenList, user, isStatus.getValue());

        if (ssbTrackList.isEmpty()) {
            throw new SsbFileNotFoundException();
        }
        List<TrackInfoRepDto> trackInfoRepDtos = ssbTrackList.stream().map(
            ssbTrack -> new TrackInfoRepDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(),
                ssbTrack.getCoverUrl(),
                ssbTrack.getUser().getUserName(), ssbTrack.getTrackLength(), ssbTrack.getCreatedDateTime())).toList();

        // 레디스 저장
        for (SsbTrack ssbTrack : ssbTrackList) {
            setTrackIdInRedis(RedisTrackDto.create(ssbTrack));
        }

        return trackInfoRepDtos;
    }

    public void setTrackIdInRedis(RedisTrackDto redisTrackDto) {
        redisCacheService.upsertCacheMapValueByKey(redisTrackDto, RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY,
            String.valueOf(redisTrackDto.getId()));
    }

    public void setTracksIdInRedis(Map<String, RedisTrackDto> dtoMap) {
        redisCacheService.upsertAllCacheMapValuesByKey(dtoMap, RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
    }

    // 토큰으로 찾는거와
    // 트랙아이디로 찾는거
    // 유저아이디로 찾는거
    private SsbTrack getTrackCacheFromOrDbByTrackId(Long trackId) {
        String trackSubKey = String.valueOf(trackId);
        RedisTrackDto redisTrackDto = redisCacheService.getCacheMapValueBySubKey(RedisTrackDto.class, trackSubKey,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (redisTrackDto == null) {
            return fetchAndSetSubKeyRedisBySubKey(trackId);
        }
        User user = userQueryService.findOne(redisTrackDto.getUid(), Enabled.ENABLED);
        return SsbTrack.redisTrackDtoToSsbTrack(redisTrackDto,
            user);
    }


    // 토큰으로 찾는거와
    // 트랙아이디로 찾는거
    // 유저아이디로 찾는거
    public List<SsbTrack> getTrackListFromOrDbByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> trackSubKeys = ids.stream().map(String::valueOf).collect(Collectors.toSet());

        log.info("trackSubKeys = {}", trackSubKeys);

        RedisDataListDto<RedisTrackDto> redisDataListDto = redisCacheService.getCacheMapValuesBySubKey(
            RedisTrackDto.class, trackSubKeys,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);

        List<SsbTrack> ssbTracks = new ArrayList<>();

        if (redisDataListDto.getResult().isEmpty()) {
            return fetchAllAndSetSubKeyRedisBySubKey(ids);
        }
        if (!redisDataListDto.getMissingKeys().isEmpty()) {
            Set<String> missingKeys = redisDataListDto.getMissingKeys();
            Set<Long> missingKey = missingKeys.stream().map(Long::valueOf).collect(Collectors.toSet());
            ssbTracks.addAll(fetchAllAndSetSubKeyRedisBySubKey(missingKey));
        }
        if (!redisDataListDto.getResult().isEmpty()) {
            // 결과 값을 RedisTrackDto 리스트로 변환
            List<RedisTrackDto> redisTrackDtoList = redisDataListDto.getResult().values().stream().toList();

            // RedisTrackDto 리스트에서 고유한 사용자 ID 추출
            Set<Long> userPkIds = redisTrackDtoList.stream()
                .map(RedisTrackDto::getUid)
                .collect(Collectors.toSet());

            // 추출된 사용자 ID를 기반으로 User 객체를 검색
            Map<Long, User> userMap = userQueryService.findUsersByIds(userPkIds, Enabled.ENABLED).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

            //userMap에서 사용자 정보를 가져와 RedisTrackDto 객체를 SsbTrack 객체로 변환
            List<SsbTrack> ssbTrackList = redisTrackDtoList.stream()
                .map(redisTrackDto -> SsbTrack.redisTrackDtoToSsbTrack(redisTrackDto,
                    userMap.get(redisTrackDto.getUid())))
                .toList();
            //변환된 SsbTrack 객체를 기존 ssbTracks 리스트에 추가
            ssbTracks.addAll(ssbTrackList);
        }
        return ssbTracks;
    }

    private List<SsbTrack> fetchAllAndSetSubKeyRedisBySubKey(Set<Long> ids) {
        List<SsbTrack> ssbTrackList = trackQueryRepository.findAllByIdsJoinUser(ids, Status.ON.getValue());
        if (ssbTrackList.isEmpty()) {
            return ssbTrackList;
        }
        setRedisTracksDtoRedis(ssbTrackList);
        return ssbTrackList;
    }

    private SsbTrack fetchAndSetSubKeyRedisBySubKey(Long trackId) {
        SsbTrack entityTrack = trackQueryRepository.findByIdJoinUser(trackId, Status.ON.getValue()).orElse(null);
        if (entityTrack == null) {
            return null;
        }
        RedisTrackDto redisUserDTO = RedisTrackDto.create(entityTrack);
        setRedisTrackDtoRedis(redisUserDTO);
        return entityTrack;
    }

    public void setRedisTracksDtoRedis(List<SsbTrack> ssbTrackList) {

        Map<String, RedisTrackDto> cacheMap = ssbTrackList.stream()
            .collect(Collectors.toMap(key -> String.valueOf(key.getId()), RedisTrackDto::create));
        setTracksIdInRedis(cacheMap);
    }


    public void setRedisTrackDtoRedis(RedisTrackDto redisTrackDto) {
        setTrackIdInRedis(redisTrackDto);
    }

    public SsbTrack findById(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id
        );
        if (findTrack == null || !isStatus.getValue().equals(findTrack.getIsStatus())) {
            throw new SsbFileNotFoundException();
        }

        return findTrack;
    }

    public TrackInfoSimpleDto getTrackInfoSimpleDto(Long id, Status isStatus) {
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id
        );
        if (ssbTrack == null) {
            throw new SsbFileNotFoundException();
        }
        return new TrackInfoSimpleDto(ssbTrack.getId(), ssbTrack.getToken(),
            ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getTrackLength(), ssbTrack.getCoverUrl(), ssbTrack.getIsPrivacy(), ssbTrack.getCreatedDateTime());
    }


    public List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(Set<Long> ids, long likedUserId, Status isStatus) {
        return trackQueryRepository.getTrackInfoSimpleDtoList(ids, likedUserId, isStatus.getValue());
    }

    private HashMap<String, RedisTrackDto> getStringRedisTrackDtoHashMap() {
        TypeReference<HashMap<String, RedisTrackDto>> typeReference = new TypeReference<>() {
        };
        return redisCacheService.getData(RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY,
            typeReference);
    }

    public List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(Set<Long> ids, Status isStatus, boolean isPrivacy) {
        return trackQueryRepository.getTrackInfoSimpleDtoList(ids, isStatus.getValue(), isPrivacy);
    }

    public SsbTrack findOneJoinUser(Long id, String token, Status isStatus) {

        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id
        );
        if (findTrack == null || !findTrack.getToken().equals(token) || !findTrack.getIsStatus()
            .equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return findTrack;
    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id
        );

        if (findTrack == null || !findTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return findTrack;
    }

    @Cacheable(value = RedisKeyDto.REDIS_USER_TOTAL_LENGTH_MAP_KEY, key = "#user.userId", cacheManager = "contentCacheManager")
    public Integer getTotalLength(User user) {
        return trackQueryRepository.getTotalTrackLength(user, Status.ON.getValue());
    }

    public TargetInfoDto getTargetInfoDto(long id, String token, Status isStatus) {
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id
        );

        if (ssbTrack == null || !ssbTrack.getToken().equals(token) || !ssbTrack.getIsStatus()
            .equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return new TargetInfoDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getIsPrivacy());
    }

    public TargetInfoDto getTargetInfoDto(long id, Status isStatus) {
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id
        );

        if (ssbTrack == null || !ssbTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return new TargetInfoDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getIsPrivacy());
    }

}
