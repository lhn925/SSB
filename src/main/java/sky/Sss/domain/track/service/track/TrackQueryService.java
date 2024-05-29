package sky.Sss.domain.track.service.track;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.CacheProperties.Redis;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.track.dto.common.TargetInfoDto;
import sky.Sss.domain.track.dto.common.TrackInfoDto;
import sky.Sss.domain.track.dto.track.redis.RedisTrackDto;
import sky.Sss.domain.track.dto.track.rep.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
import sky.Sss.domain.track.entity.track.SsbTrackLikes;
import sky.Sss.domain.track.exception.checked.SsbFileNotFoundException;
import sky.Sss.domain.track.repository.track.TrackQueryRepository;
import sky.Sss.domain.user.dto.redis.RedisUserDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.Status;
import sky.Sss.domain.user.service.UserQueryService;
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
        SsbTrack ssbTrack = fetchAndSetSubKeyRedisBySubKey(id, RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
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

    // 토큰으로 찾는거와
    // 트랙아이디로 찾는거
    // 유저아이디로 찾는거
    private SsbTrack getTrackCacheFromOrDbByTrackId(Long trackId, String redisTrackMapKey) {
        String trackSubKey = String.valueOf(trackId);
        RedisTrackDto redisTrackDto = redisCacheService.getCacheMapBySubKey(RedisTrackDto.class, trackSubKey,
            redisTrackMapKey);

        if (redisTrackDto == null) {
            return fetchAndSetSubKeyRedisBySubKey(trackId, redisTrackMapKey);
        }

        User user = userQueryService.findOne(redisTrackDto.getUid(), Enabled.ENABLED);
        return SsbTrack.redisTrackDtoToSsbTrack(redisTrackDto,
            user);
    }

    private SsbTrack fetchAndSetSubKeyRedisBySubKey(Long trackId, String redisUidMapKey) {
        SsbTrack entityTrack = switch (redisUidMapKey) {
            case RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY ->
                trackQueryRepository.findByIdJoinUser(trackId, Status.ON.getValue()).orElse(null);
            default -> null;
        };
        if (entityTrack == null) {
            return null;
        }
        RedisTrackDto redisUserDTO = RedisTrackDto.create(entityTrack);
        setRedisTrackDtoRedis(redisUserDTO);
        return entityTrack;
    }

    public void setRedisTrackDtoRedis(RedisTrackDto redisTrackDto) {
        setTrackIdInRedis(redisTrackDto);
    }

    public SsbTrack findById(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (findTrack == null || !isStatus.getValue().equals(findTrack.getIsStatus())) {
            throw new SsbFileNotFoundException();
        }

        return findTrack;
    }

    public TrackInfoSimpleDto getTrackInfoSimpleDto(Long id, Status isStatus) {
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
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

        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (findTrack == null || !findTrack.getToken().equals(token) || !findTrack.getIsStatus()
            .equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return findTrack;
    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);


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
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);

        if (ssbTrack == null || !ssbTrack.getToken().equals(token) || !ssbTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return new TargetInfoDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getIsPrivacy());
    }
    public TargetInfoDto getTargetInfoDto(long id, Status isStatus) {
        SsbTrack ssbTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);

        if (ssbTrack == null || !ssbTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return new TargetInfoDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(), ssbTrack.getUser(),
            ssbTrack.getIsPrivacy());
    }

}
