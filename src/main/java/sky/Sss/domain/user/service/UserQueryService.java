package sky.Sss.domain.user.service;


import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
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
import sky.Sss.domain.user.dto.UserInfoDto;
import sky.Sss.domain.user.dto.login.CustomUserDetails;
import sky.Sss.domain.user.dto.myInfo.UserMyInfoDto;
import sky.Sss.domain.user.dto.push.PushMsgCacheDto;
import sky.Sss.domain.user.dto.redis.RedisUserDTO;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.exception.UserInfoNotFoundException;
import sky.Sss.domain.user.exception.UserNotFoundException;
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


    public CustomUserDetails findByEmailOne(String email, Enabled enabled)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByEmailAndIsEnabled(email, enabled.getValue());
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("email.notfound"));

        return (CustomUserDetails) User.UserBuilder(user);
    }

    public CustomUserDetails findStatusUserId(String userId, Enabled enabled)
        throws UsernameNotFoundException {
        Optional<User> findOne = userQueryRepository.findByUserIdAndIsEnabled(userId, enabled.getValue());
        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("userId.notfound"));

        return (CustomUserDetails) User.UserBuilder(user);
    }


    //    @Cacheable(value = RedisKeyDto.REDIS_USER_CACHE_TOKEN_KEY,key = "#userId",cacheManager = "contentCacheManager")
    public String findTokenByUserId(String userId, Enabled enabled)
        throws UsernameNotFoundException {
//        Optional<User> findOne = userQueryRepository.findByUserIdAndIsEnabled(userId, enabled.getValue());
//        User user = findOne.orElseThrow(() -> new UsernameNotFoundException("userId.notfound"));
        return findOne().getToken();
    }

    public UserInfoDto findByUser(UserInfoDto userInfoDto)
        throws UsernameNotFoundException {
        User user = User.getOptionalUser(
            userQueryRepository.findByUserId(userInfoDto.getUserId()));

        UserDetails userDetails = User.UserBuilder(user);

        return UserInfoDto.createUserInfo(userDetails);
    }

    public Set<User> findUsersByUserNames(Set<String> userNames, Enabled isEnabled)
        throws UsernameNotFoundException {
        return userQueryRepository.findAllByUserNames(userNames, isEnabled.getValue());
    }


    public User findOne() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return getUserInfoFromCacheOrDB(userDetails.getUsername(), RedisKeyDto.REDIS_USER_IDS_MAP_KEY);
    }

    private User getUserInfoFromCacheOrDB(String subKey, String redisUidMapKey) {
        TypeReference<HashMap<String, String>> type = new TypeReference<>() {
        };
        HashMap<String, String> userIdMap = redisCacheService.getData(redisUidMapKey, type);
        // map 널인 경우
        // 혹은 Map 에 없는 경우
        if (userIdMap == null || !userIdMap.containsKey(subKey)) {
            return fetchAndSetUserInReds();
        }
        String uid = String.valueOf(userIdMap.get(subKey));
        TypeReference<HashMap<String, RedisUserDTO>> redisDtoType = new TypeReference<>() {
        };
        String redisUsersInfoMapKey = RedisKeyDto.REDIS_USERS_INFO_MAP_KEY;
        Map<String, RedisUserDTO> userInfoMap = redisCacheService.getData(redisUsersInfoMapKey, redisDtoType);
        if (userInfoMap == null || !userInfoMap.containsKey(uid)) {
            return fetchAndSetUserInReds();
        }
        RedisUserDTO redisUserDTO = userInfoMap.get(uid);
        return User.redisUserDtoToUser(redisUserDTO);
    }

    private User fetchAndSetUserInReds() {
        User entityUser = getEntityUser();
        RedisUserDTO redisUserDTO = RedisUserDTO.create(entityUser);
        setUserInfoDtoRedis(redisUserDTO);
        return entityUser;
    }

    private void setUserInfoDtoRedis(RedisUserDTO redisUserDTO) {
        setUserIdInRedis(redisUserDTO.getId(), redisUserDTO.getUserId());
        setUserEmailInRedis(redisUserDTO.getId(), redisUserDTO.getEmail());
        setUserNameInRedis(redisUserDTO.getId(), redisUserDTO.getUserName());
        setUserInfoInRedis(redisUserDTO);
    }

    private void removeUserInfoDtoRedis(RedisUserDTO redisUserDTO) {
        removeUserIdFromRedis(redisUserDTO.getId(), redisUserDTO.getUserId());
        removeUserEmailFromRedis(redisUserDTO.getId(), redisUserDTO.getEmail());
        removeUserNameFromRedis(redisUserDTO.getId(), redisUserDTO.getUserName());
        removeUserInfoFromRedis(redisUserDTO);
    }

    public User getEntityUser() {
        Authentication authentication = getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        // getUsername 은 여기선 UserId를 뜻함
        // userName
        // userId
        // userToken
        Optional<User> optionalUser = userQueryRepository.findByUserId(userDetails.getUsername());
        return User.getOptionalUser(optionalUser);
    }


    public UserMyInfoDto getUserMyInfoDto() {
        User user = findOne();
        return new UserMyInfoDto(user.getUserId(), user.getEmail(), user.getUserName(), user.getPictureUrl(),
            user.getIsLoginBlocked(), user.getGrade());
    }

    public User findOne(String userId) {
        Optional<User> optionalUser = userQueryRepository.findByUserId(userId);
        return User.getOptionalUser(optionalUser);
    }

    public Optional<User> getOptUserEntity(String userId) {
        return userQueryRepository.findByUserId(userId);
    }


    public User findOne(String userId, String token) {
        Optional<User> optionalUser = userQueryRepository.findOne(userId, token);
        return User.getOptionalUser(optionalUser);
    }

    public User findOne(Long uid, Enabled enabled) {
        Optional<User> optionalUser = userQueryRepository.findByIdAndIsEnabled(uid, enabled.getValue());
        return User.getOptionalUser(optionalUser);
    }


    public Optional<User> findOptionalUser(String userId) {
        return userQueryRepository.findByUserId(userId);
    }


    private void setUserIdInRedis(Long uid, String userId) {
        redisCacheService.upsertCacheMapValueByKey(uid, RedisKeyDto.REDIS_USER_IDS_MAP_KEY, userId);
    }

    private void setUserNameInRedis(Long uid, String userName) {
        redisCacheService.upsertCacheMapValueByKey(uid, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY,
            userName);
    }

    private void setUserEmailInRedis(Long uid, String email) {
        redisCacheService.upsertCacheMapValueByKey(uid, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY,
            email);
    }

    private void setUserInfoInRedis(RedisUserDTO redisUserDTO) {
        redisCacheService.upsertCacheMapValueByKey(redisUserDTO, RedisKeyDto.REDIS_USERS_INFO_MAP_KEY,
            String.valueOf(redisUserDTO.getId()));
    }


    private void removeUserIdFromRedis(Long uid, String userId) {
        redisCacheService.removeCacheMapValueByKey(uid,
            RedisKeyDto.REDIS_USER_IDS_MAP_KEY,
            userId);
    }

    private void removeUserNameFromRedis(Long uid, String userName) {
        redisCacheService.removeCacheMapValueByKey(uid, RedisKeyDto.REDIS_USER_NAMES_MAP_KEY,
            userName);
    }

    private void removeUserEmailFromRedis(Long uid, String email) {
        redisCacheService.removeCacheMapValueByKey(uid, RedisKeyDto.REDIS_USER_EMAILS_MAP_KEY,
            email);
    }

    private void removeUserInfoFromRedis(RedisUserDTO redisUserDTO) {
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
