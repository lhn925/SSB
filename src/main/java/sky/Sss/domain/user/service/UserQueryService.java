package sky.Sss.domain.user.service;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.redis.RedisUserDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.global.redis.dto.RedisKeyDto;
import sky.Sss.global.redis.service.RedisCacheService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;
    private final RedisCacheService redisCacheService;


    public CustomUserDetails findByEmailOne(String email)
        throws UsernameNotFoundException {
        User findUser = getUserInfoFromCacheOrDB(email, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled()) {
            throw new UsernameNotFoundException("email.notfound");
        }
        return (CustomUserDetails) User.UserBuilder(findUser);
    }

    public CustomUserDetails findStatusUserId(String userId)
        throws UsernameNotFoundException {

        User findUser = getUserInfoFromCacheOrDB(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled()) {
            throw new UsernameNotFoundException("userId.notfound");
        }
        return (CustomUserDetails) User.UserBuilder(findUser);
    }

    public String findTokenByUserId(String userId, Enabled enabled)
        throws UsernameNotFoundException {
        User findUser = getUserInfoFromCacheOrDB(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);

        if (findUser == null || !findUser.getIsEnabled()) {
            throw new UsernameNotFoundException("userId.notfound");
        }
        return findUser.getToken();
    }

    public Set<User> findUsersByUserNames(Set<String> userNames, Enabled isEnabled)
        throws UsernameNotFoundException {
        Set<User> users = new HashSet<>();
        for (String userName : userNames) {
            User user = getUserInfoFromCacheOrDB(userName, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY);
            users.add(user);
        }
        return users;
    }


    public User findOne() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return getUserInfoFromCacheOrDB(userDetails.getUsername(), RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    public User getUserInfoFromCacheOrDB(String subKey, String redisUidMapKey) {
        TypeReference<HashMap<String, String>> type = new TypeReference<>() {
        };

        HashMap<String, String> subKeyMap = redisCacheService.getData(redisUidMapKey, type);
        // map 널인 경우
        // 혹은 Map 에 없는 경우
        if (subKeyMap == null || !subKeyMap.containsKey(subKey)) {
            return fetchAndSetSubKeyRedisBySubKey(subKey, redisUidMapKey);
        }
        String token = String.valueOf(subKeyMap.get(subKey));
        return getUserInfoByTokenRedisOrDB(token, subKey, redisUidMapKey);
    }

    public User getUserInfoByTokenRedisOrDB(String token, String subKey, String redisUidMapKey) {

        TypeReference<HashMap<String, RedisUserDto>> redisDtoType = new TypeReference<>() {
        };
        String redisUsersInfoMapKey = RedisKeyDto.REDIS_USERS_INFO_MAP_KEY;
        Map<String, RedisUserDto> userInfoMap = redisCacheService.getData(redisUsersInfoMapKey, redisDtoType);

        if (userInfoMap == null || !userInfoMap.containsKey(token)) {
            return fetchAndSetSubKeyRedisBySubKey(subKey, redisUidMapKey);
        }
        RedisUserDto redisUserDTO = userInfoMap.get(token);
        return User.redisUserDtoToUser(redisUserDTO);
    }

    private User fetchAndSetSubKeyRedisBySubKey(String subKey, String redisUidMapKey) {
        User entityUser = switch (redisUidMapKey) {
            case RedisKeyDto.REDIS_USER_IDS_MAP_KEY ->
                userQueryRepository.findByUserIdAndIsEnabled(subKey, Enabled.ENABLED()).orElse(null);
            case RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY ->
                userQueryRepository.findByEmailAndIsEnabled(subKey, Enabled.ENABLED()).orElse(null);
            case RedisKeyDto.REDIS_USER_NAMES_MAP_KEY ->
                userQueryRepository.findByUserName(subKey, Enabled.ENABLED()).orElse(null);
            case RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY ->
                userQueryRepository.findByIdAndIsEnabled(Long.valueOf(subKey), Enabled.ENABLED()).orElse(null);
            default -> null;
        };
        if (entityUser == null) {
            return null;
        }
        RedisUserDto redisUserDTO = RedisUserDto.create(entityUser);
        setUserInfoDtoRedis(redisUserDTO);
        return entityUser;
    }

    public void setUserInfoDtoRedis(RedisUserDto redisUserDTO) {
        setUserIdInRedis(redisUserDTO.getToken(), redisUserDTO.getUserId());
        setUserEmailInRedis(redisUserDTO.getToken(), redisUserDTO.getEmail());
        setUserNameInRedis(redisUserDTO.getToken(), redisUserDTO.getUserName());
        setUidInRedis(redisUserDTO.getToken(), redisUserDTO.getId());
        setUserInfoInRedis(redisUserDTO);
    }

    public void removeUserInfoDtoRedis(RedisUserDto redisUserDTO) {
        removeUserIdFromRedis(redisUserDTO.getToken(), redisUserDTO.getUserId());
        removeUserEmailFromRedis(redisUserDTO.getToken(), redisUserDTO.getEmail());
        removeUserNameFromRedis(redisUserDTO.getToken(), redisUserDTO.getUserName());
        removeUidFromRedis(redisUserDTO.getToken(), redisUserDTO.getId());
        removeUserInfoFromRedis(redisUserDTO);
    }

    public User getEntityUser() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Optional<User> optionalUser = Optional.ofNullable(
            fetchAndSetSubKeyRedisBySubKey(userDetails.getUsername(), RedisKeyDto.REDIS_USER_IDS_MAP_KEY));
        return User.getOptionalUser(optionalUser);
    }


    public UserMyInfoDto getUserMyInfoDto() {
        User user = findOne();
        return new UserMyInfoDto(user.getUserId(), user.getEmail(), user.getUserName(), user.getPictureUrl(),
            user.getIsLoginBlocked(), user.getGrade());
    }


    public User getUserEntity(String userId) {
        return fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    public User getEntityUser(String userId) {
        Optional<User> optionalUser = Optional.ofNullable(
            fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY));
        return User.getOptionalUser(optionalUser);
    }


    public User findOne(String userId) {
        return getUserInfoFromCacheOrDB(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    public User findOne(String userId, String token) {
        Optional<User> optionalUser = Optional.ofNullable(
            fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY));
        User findUser = User.getOptionalUser(optionalUser);

        if (!findUser.getToken().equals(token)) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        return findUser;
    }

    public User findOne(Long uid, Enabled enabled) {
        String uidAndSUb = String.valueOf(uid);

        User findUser = getUserInfoFromCacheOrDB(uidAndSUb,
            RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled().equals(enabled.getValue())) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        return findUser;
    }


    public User findByUserName(String userName, Enabled enabled) {
        User findUser = getUserInfoFromCacheOrDB(userName,
            RedisKeyDto.REDIS_USER_NAMES_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled().equals(enabled.getValue())) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        return findUser;
    }

    public Optional<User> findOptionalUser(String userId) {
        return userQueryRepository.findByUserId(userId);
    }


    private void setUserIdInRedis(String token, String userId) {
        redisCacheService.upsertCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_IDS_MAP_KEY, userId);
    }

    private void setUserNameInRedis(String token, String userName) {
        redisCacheService.upsertCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY,
            userName);
    }

    private void setUidInRedis(String token, Long uid) {
        redisCacheService.upsertCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY,
            String.valueOf(uid));
    }

    private void setUserEmailInRedis(String token, String email) {
        redisCacheService.upsertCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY,
            email);
    }

    private void setUserInfoInRedis(RedisUserDto redisUserDTO) {
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO, RedisKeyDto.REDIS_USERS_INFO_MAP_KEY,
            redisUserDTO.getToken());
    }


    private void removeUidFromRedis(String token, Long uid) {
        redisCacheService.removeCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY,
            String.valueOf(uid));
    }

    private void removeUserIdFromRedis(String token, String userId) {
        redisCacheService.removeCacheMapValueByKey(token,
            RedisKeyDto.REDIS_USER_IDS_MAP_KEY,
            userId);
    }

    private void removeUserNameFromRedis(String token, String userName) {
        redisCacheService.removeCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY,
            userName);
    }

    private void removeUserEmailFromRedis(String token, String email) {
        redisCacheService.removeCacheMapValueByKey(token, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY,
            email);
    }

    private void removeUserInfoFromRedis(RedisUserDto redisUserDTO) {
        redisCacheService.removeCacheMapValueByKey(redisUserDTO, RedisKeyDto.REDIS_USERS_INFO_MAP_KEY,
            String.valueOf(redisUserDTO.getId()));
    }

    private static Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority
        ).findFirst().orElse(null);
        if (authorities == null || authorities.equals(UserGrade.ANONYMOUS.getRole())) {
            throw new UserInfoNotFoundException("sky.userId.notFind");
        }
        return authentication;
    }

}
