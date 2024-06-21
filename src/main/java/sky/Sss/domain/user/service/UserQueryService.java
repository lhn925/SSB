package sky.Sss.domain.user.service;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
import sky.Sss.domain.user.dto.redis.RedisUserDto;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.model.Enabled;
import sky.Sss.domain.user.model.UserGrade;
import sky.Sss.domain.user.repository.UserQueryRepository;
import sky.Sss.global.redis.dto.RedisDataListDto;
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

        if (findUser == null || !findUser.getIsEnabled().equals(enabled.getValue())) {
            throw new UsernameNotFoundException("userId.notfound");
        }
        return findUser.getToken();
    }

    public Set<User> findUsersByUserNames(Set<String> userNames, Enabled isEnabled)
        throws UsernameNotFoundException {
        Set<User> users = new HashSet<>(getUserListFromCacheOrDB(userNames, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY));
        return users.stream()
            .filter(user -> user.getIsEnabled().equals(isEnabled.getValue())).
            collect(Collectors.collectingAndThen(Collectors.toSet(), list -> {
                if (!list.isEmpty()) {
                    throw new UserInfoNotFoundException("userId.notFind");
                }
                return list;
            }));
    }
    public List<User> findUsersByIds(Set<Long> ids, Enabled isEnabled)
        throws UsernameNotFoundException {
        Set<String> setIds = ids.stream().map(String::valueOf).collect(Collectors.toSet());
        List<User> users = getUserListFromCacheOrDB(setIds, RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY);
        return users.stream()
            .filter(user -> user.getIsEnabled().equals(isEnabled.getValue())).
            collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                if (list.isEmpty()) {
                    throw new UserInfoNotFoundException("userId.notFind");
                }
                return list;
            }));
    }

    public User findOne() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return getUserInfoFromCacheOrDB(userDetails.getUsername(), RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    public User getUserInfoFromCacheOrDB(String subKey, String redisUidMapKey) {
        String token = redisCacheService.getCacheMapValueBySubKey(String.class, subKey, redisUidMapKey);

        // map 널인 경우
        // 혹은 Map 에 없는 경우
        if (token == null) {
            return fetchAndSetSubKeyRedisBySubKey(subKey, redisUidMapKey);
        }
        return getUserByTokenRedisOrDB(token, subKey, redisUidMapKey);
    }

    public List<User> getUserListFromCacheOrDB(Set<String> subKeyList, String redisUidMapKey) {
        RedisDataListDto<String> redisDataListDto = redisCacheService.getCacheMapValuesBySubKey(String.class,
            subKeyList, redisUidMapKey);
        if (subKeyList.isEmpty()) {
            return new ArrayList<>();
        }
        // map 널인 경우
        // 혹은 Map 에 없는 경우

        if (redisDataListDto.getResult().isEmpty()) {
            return fetchAllAndSetSubKeyRedisBySubKey(subKeyList, redisUidMapKey);
        }
        Map<String, String> result = redisDataListDto.getResult();

        List<User> users = new ArrayList<>();

        if (!redisDataListDto.getMissingKeys().isEmpty()) {
            users.addAll(fetchAllAndSetSubKeyRedisBySubKey(redisDataListDto.getMissingKeys(), redisUidMapKey));
        }

        if (!result.isEmpty()) {
            Set<String> tokens = new HashSet<>(result.values());
            users.addAll(getUserListByTokenRedisOrDB(tokens, subKeyList, redisUidMapKey));
        }
        return users;
    }

    public User getUserByTokenRedisOrDB(String token, String subKey, String redisUidMapKey) {
        RedisUserDto redisUserDto = redisCacheService.getCacheMapValueBySubKey(RedisUserDto.class,token,
            RedisKeyDto.REDIS_USERS_INFO_MAP_KEY);
        if (redisUserDto == null) {
            return fetchAndSetSubKeyRedisBySubKey(subKey, redisUidMapKey);
        }
        return User.redisUserDtoToUser(redisUserDto);
    }

    public List<User> getUserListByTokenRedisOrDB(Set<String> tokens, Set<String> subKeyList, String redisUidMapKey) {
        if (subKeyList.isEmpty()) {
            return new ArrayList<>();
        }
        RedisDataListDto<RedisUserDto> redisDataListDto = redisCacheService.getCacheMapValuesBySubKey(
            RedisUserDto.class, tokens,
            RedisKeyDto.REDIS_USERS_INFO_MAP_KEY);


        //
        if (redisDataListDto.getResult().isEmpty()) {
            return fetchAllAndSetSubKeyRedisBySubKey(subKeyList, redisUidMapKey);
        }
        Map<String, RedisUserDto> result = redisDataListDto.getResult();
        List<User> users = new ArrayList<>();

        if (!redisDataListDto.getMissingKeys().isEmpty()) {
            users.addAll(fetchAllAndSetSubKeyRedisBySubKey(redisDataListDto.getMissingKeys(), redisUidMapKey));
        }

        if (!result.isEmpty()) {
            users.addAll(result.values().stream().map(User::redisUserDtoToUser).toList());
        }
        return users;
    }

    public List<User> getUserListByTokenRedisOrDB(Set<String> tokens, String redisUidMapKey) {
        return getUserListByTokenRedisOrDB(tokens, tokens, redisUidMapKey);
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
            case RedisKeyDto.REDIS_USERS_INFO_MAP_KEY ->
                userQueryRepository.findByTokenAndIsEnabled(subKey, Enabled.ENABLED()).orElse(null);
            default -> null;
        };
        if (entityUser == null) {
            return null;
        }
        RedisUserDto redisUserDTO = RedisUserDto.create(entityUser);
        setUserInfoDtoRedis(redisUserDTO);
        return entityUser;
    }
    private List<User> fetchAllAndSetSubKeyRedisBySubKey(Set<String> subKeyList, String redisUidMapKey) {
        List<User> entityUsers = switch (redisUidMapKey) {
            case RedisKeyDto.REDIS_USER_IDS_MAP_KEY ->
                userQueryRepository.findByUserIds(subKeyList, Enabled.ENABLED());
            case RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY ->
                userQueryRepository.findByEmails(subKeyList, Enabled.ENABLED());
            case RedisKeyDto.REDIS_USER_NAMES_MAP_KEY ->
                userQueryRepository.findByUserNames(subKeyList, Enabled.ENABLED());
            case RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY ->
                userQueryRepository.findByIds(subKeyList.stream().map(Long::valueOf).collect(Collectors.toSet()),
                    Enabled.ENABLED());
            case RedisKeyDto.REDIS_USERS_INFO_MAP_KEY ->
                userQueryRepository.findByTokens(subKeyList, Enabled.ENABLED());
            default -> new ArrayList<>();
        };
        entityUsers.stream()
            .map(RedisUserDto::create)
            .forEach(this::setUserInfoDtoRedis);
        return entityUsers;
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

    public User getUserEntity(String userId) {
        return fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    public User getEntityUser(String userId) {
        Optional<User> optionalUser = Optional.ofNullable(
            fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY));
        return User.getOptionalUser(optionalUser);
    }


    public User findOne(String userId) {
        User user = getUserInfoFromCacheOrDB(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY);

        if (user == null || user.getIsEnabled().equals(Enabled.UNABlED())) {
            throw new UserInfoNotFoundException("sky.user.notFind");
        }
        return user;
    }

    public User findOne(String userId, String token) {
        Optional<User> optionalUser = Optional.ofNullable(
            fetchAndSetSubKeyRedisBySubKey(userId, RedisKeyDto.REDIS_USER_IDS_MAP_KEY));
        User findUser = User.getOptionalUser(optionalUser);

        if (!findUser.getToken().equals(token)) {
            throw new UserInfoNotFoundException("sky.user.notFind");
        }
        return findUser;
    }

    public User findOne(Long uid, Enabled enabled) {
        String uidAndSUb = String.valueOf(uid);

        User findUser = getUserInfoFromCacheOrDB(uidAndSUb,
            RedisKeyDto.REDIS_USER_PK_ID_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled().equals(enabled.getValue())) {
            throw new UserInfoNotFoundException("sky.user.notFind");
        }
        return findUser;
    }
    public List<User> getUserListByTokens(Set<String> tokens, Enabled enabled) {

        log.info("tokens = {}", tokens);

        List<User> userListForm = getUserListByTokenRedisOrDB(tokens,
            RedisKeyDto.REDIS_USERS_INFO_MAP_KEY);
        // enabled 필터링
        return userListForm.stream()
            .filter(user -> user.getIsEnabled().equals(enabled.getValue())).
            collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                if (list.isEmpty()) {
                throw new UserInfoNotFoundException("sky.user.notFind");
            }
            return list;
        }));
    }
    public User findByUserName(String userName, Enabled enabled) {
        User findUser = getUserInfoFromCacheOrDB(userName,
            RedisKeyDto.REDIS_USER_NAMES_MAP_KEY);
        if (findUser == null || !findUser.getIsEnabled().equals(enabled.getValue())) {
            throw new UserInfoNotFoundException("sky.user.notFind");
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
            throw new UserInfoNotFoundException("sky.user.notFind");
        }
        return authentication;
    }

}
