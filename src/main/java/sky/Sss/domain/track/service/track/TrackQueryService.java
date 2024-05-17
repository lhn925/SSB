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
import sky.Sss.domain.track.dto.track.redis.RedisTrackDto;
import sky.Sss.domain.track.dto.track.rep.TrackInfoRepDto;
import sky.Sss.domain.track.dto.track.common.TrackInfoSimpleDto;
import sky.Sss.domain.track.entity.track.SsbTrack;
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
//    private final TrackLikesService trackLikesService;

    public SsbTrack getEntityTrack(Long id, String token, User user, Status isStatus) {
        SsbTrack ssbTrack = fetchAndSetSubKeyRedisBySubKey(id, RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (ssbTrack == null || !ssbTrack.getToken().equals(token) || !user.getToken().equals(ssbTrack.getToken()) ||
            !isStatus.getValue().equals(ssbTrack.getIsStatus()) ) {
            throw new SsbFileNotFoundException();
        }
        return ssbTrack;
    }

    public List<TrackInfoRepDto> getTrackInfoRepDto(List<String> tokenList, User user, Status isStatus) {
        List<SsbTrack> ssbTrackList = trackQueryRepository.findAllByToken(tokenList, user, isStatus.getValue());

        if (ssbTrackList.isEmpty()) {
            throw new SsbFileNotFoundException();
        }
        // redisCache 저장을 위해
//        Set<Long> trackIds = ssbTrackList.stream().map(SsbTrack::getId).collect(Collectors.toSet());

        List<TrackInfoRepDto> trackInfoRepDtos = ssbTrackList.stream().map(
            ssbTrack -> new TrackInfoRepDto(ssbTrack.getId(), ssbTrack.getToken(), ssbTrack.getTitle(),
                ssbTrack.getCoverUrl(),
                ssbTrack.getUser().getUserName(), ssbTrack.getTrackLength(), ssbTrack.getCreatedDateTime())).toList();

        // 레디스 저장
        for (SsbTrack ssbTrack : ssbTrackList) {
            setTrackIdInRedis(RedisTrackDto.create(ssbTrack));
        }

//        addKeyUidValTrackIdInRedis(user.getId(), trackIds);
        return trackInfoRepDtos;
    }

    private void setTrackIdInRedis(RedisTrackDto redisTrackDto) {
        redisCacheService.upsertCacheMapValueByKey(redisTrackDto, RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY,
            String.valueOf(redisTrackDto.getId()));
    }

//    private void addKeyUidValTrackIdInRedis(Long uid, Set<Long> trackSet) {
//
//        String subKey = String.valueOf(uid);
//        TypeReference<HashMap<String, Set<Long>>> type = new TypeReference<>() {
//        };
//        // 있는지 확인
//        HashMap<String, Set<Long>> uidMap = redisCacheService.getData(RedisKeyDto.REDIS_TRACKS_UID_SET_KEY, type);
//
//        // 없으면 저장
//        if (uidMap == null || !uidMap.containsKey(subKey)) {
//            redisCacheService.upsertCacheMapValueByKey(trackSet, RedisKeyDto.REDIS_TRACKS_UID_SET_KEY, subKey);
//            return;
//        }
//        // 있으면 TrackId 추가
//        Set<Long> trackIds = uidMap.get(subKey);
//        trackIds.addAll(trackSet);
//        redisCacheService.upsertCacheMapValueByKey(trackIds, RedisKeyDto.REDIS_TRACKS_UID_SET_KEY, subKey);
//    }


    // 토큰으로 찾는거와
    // 트랙아이디로 찾는거
    // 유저아이디로 찾는거
    private SsbTrack getTrackCacheFromOrDbByTrackId(Long trackId, String redisTrackMapKey) {
        TypeReference<HashMap<String, RedisTrackDto>> redisTrackType = new TypeReference<>() {
        };

        String trackSubKey = String.valueOf(trackId);
        HashMap<String, RedisTrackDto> redisTrackMap = redisCacheService.getData(trackSubKey, redisTrackType);

        if (redisTrackMap == null || !redisTrackMap.containsKey(trackSubKey)) {
            return fetchAndSetSubKeyRedisBySubKey(trackId, redisTrackMapKey);
        }
        RedisTrackDto redisTrackDto = redisTrackMap.get(trackSubKey);

        User user = userQueryService.findOne(redisTrackDto.getId(), Enabled.ENABLED);
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
//        Long id = redisTrackDto.getId();
//        Set<Long> trackIdSet = new HashSet<>();
//        trackIdSet.add(id);
//        addKeyUidValTrackIdInRedis(redisTrackDto.getUid(), trackIdSet);
        setTrackIdInRedis(redisTrackDto);
    }

    public SsbTrack findById(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (findTrack == null || isStatus.getValue().equals(findTrack.getIsStatus())) {
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


    public List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(Set<Long> ids, long likeUid, Status isStatus) {
        User user = userQueryService.findOne(likeUid, Enabled.ENABLED);
        HashMap<String, RedisTrackDto> redisTrackMap = getStringRedisTrackDtoHashMap();
        Set<SsbTrack> trackList = new HashSet<>();
        Set<Long> searchIdList = new HashSet<>();
        // 트랙 검색
        if (redisTrackMap == null) {
            trackList.addAll(trackQueryRepository.findAllByIdInAndIsStatus(ids,
                isStatus.getValue()));
        } else {
            for (Long id : ids) {
                if (!redisTrackMap.containsKey(String.valueOf(id))) {
                    searchIdList.add(id);
                    continue;
                }
                RedisTrackDto redisTrackDto = redisTrackMap.get(String.valueOf(id));
                trackList.add(SsbTrack.redisTrackDtoToSsbTrack(redisTrackDto,user));
            }
            if (!searchIdList.isEmpty()) {
                trackList.addAll(trackQueryRepository.findAllByIdInAndIsStatus(ids,
                    isStatus.getValue()));
            }
        }

        // Like 리스트
        for (SsbTrack ssbTrack : trackList) {
            setRedisTrackDtoRedis(RedisTrackDto.create(ssbTrack));


        }






        // 팔로우 리스트



        return null;
    }


    private HashMap<String, RedisTrackDto> getStringRedisTrackDtoHashMap() {
        TypeReference<HashMap<String,RedisTrackDto>> typeReference = new TypeReference<>() {};
        return redisCacheService.getData(RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY,
            typeReference);
    }

    public List<TrackInfoSimpleDto> getTrackInfoSimpleDtoList(Set<Long> ids, Status isStatus, boolean isPrivacy) {
        return trackQueryRepository.getTrackInfoSimpleDtoList(ids, isStatus.getValue(), isPrivacy);
    }

    public SsbTrack findOneJoinUser(Long id, String token, Status isStatus) {
//        return trackQueryRepository.findByIdJoinUser(id, token, isStatus.getValue())
//            .orElseThrow(SsbFileNotFoundException::new);
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (findTrack == null || findTrack.getToken().equals(token) || findTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return findTrack;
    }

    public SsbTrack findOneJoinUser(Long id, Status isStatus) {
        SsbTrack findTrack = getTrackCacheFromOrDbByTrackId(id,
            RedisKeyDto.REDIS_TRACKS_INFO_MAP_KEY);
        if (findTrack == null ||  findTrack.getIsStatus().equals(isStatus.getValue())) {
            throw new SsbFileNotFoundException();
        }
        return findTrack;
    }
    @Cacheable(value = RedisKeyDto.REDIS_USER_TOTAL_LENGTH_MAP_KEY, key = "#user.userId", cacheManager = "contentCacheManager")
    public Integer getTotalLength(User user) {
        return trackQueryRepository.getTotalTrackLength(user, Status.ON.getValue());
    }

    public TargetInfoDto getTargetInfoDto(long id, String token, Status isStatus) {
        return trackQueryRepository.getTargetInfoDto(id, token, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }

    public TargetInfoDto getTargetInfoDto(long id, Status isStatus) {
        return trackQueryRepository.getTargetInfoDto(id, isStatus.getValue())
            .orElseThrow(SsbFileNotFoundException::new);
    }
}
