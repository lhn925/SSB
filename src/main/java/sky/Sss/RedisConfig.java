package sky.Sss;


import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
//@EnableRedisHttpSession
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
//
//        Jedis는 멀티스레드 환경에서 안전하지 않다.
//            Lettuce는 Netty 기반 환경으로 비동기를 지원한다.
//            Lettuce는 거의 모든 측면에서 성능이 Jedis보다 우수하다.

//        Lettuce 라이브러리를 사용한다면 Keep Alive 기능을 활성화하고 Connection timeout을 설정하는 것을 추천합니다.
//
//        keepAlive 옵션을 활성화(keepAlive(true))하면, 애플리케이션 런타임 중에 실패한 연결을 처리해야 할 상황이 줄어듭니다.
//        이 속성은 TCP Keep Alive 기능을 설정합니다. TCP Keep Alive는 다음과 같은 특성을 가집니다.
//
//            TCP Keep Alive를 켜면 오랫동안 데이터를 전송하지 않아도, TCP Connection이 활성된 상태로 유지됩니다.
//        TCP Keep Alive는 주기적으로 프로브(Probe)나 메시지를 전송하고 Acknowledgment를 수신합니다.
//            만약 Acknowledgment가 주어진 시간에 오지 않는다면, TCP Connection은 끊어진 걸로 간주되어 종료됩니다.
        // 소켓 설정
//        SocketOptions socketOptions = SocketOptions.builder()
//            .connectTimeout(Duration.ofMillis(100L))
//            .keepAlive(true)
//            .build();
        //----------------- (2) Cluster topology refresh 옵션
        ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions
            .builder()
            .dynamicRefreshSources(true) // 모든 Redis 노드로부터 topology 정보 획득. default = true
            .enableAllAdaptiveRefreshTriggers()  // Redis 클러스터에서 발생하는 모든 이벤트(MOVE, ACK)등에 대해서 topology 갱신
            .enablePeriodicRefresh(Duration.ofSeconds(30))// 주기적으로 토폴로지를 갱신하는 시간
            .build();
        //----------------- (3) Cluster client 옵션
        ClusterClientOptions clusterClientOptions = ClusterClientOptions
            .builder()
            .pingBeforeActivateConnection(true)// 커넥션을 사용하기 위하여 PING 명령어를 사용하여 검증합니다.
            .autoReconnect(true)// 자동 재접속 옵션을 사용합니다.
//            .socketOptions(socketOptions)  // 앞서 생성한 socketOptions 객체를 세팅합니다.
            .topologyRefreshOptions(clusterTopologyRefreshOptions) // 앞서 생성한 clusterTopologyRefreshOptions 객체를 생성합니다.
            .maxRedirects(3).build();

        //maxRedirects() 옵션은 Redis 클러스터가 MOVED_REDIRECT를 응답할 때 클라이언트 애플리케이션에서 Redirect하는 최대 횟수

        //----------------- (4) Lettuce Client 옵션
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration
            .builder()
            .commandTimeout(Duration.ofMillis(150L))// ----------- 명령어 타임아웃 설정
            .clientOptions(clusterClientOptions)
            .build();
        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(host);
        redisConfiguration.setPort(port);
        redisConfiguration.setDatabase(0);
        return new LettuceConnectionFactory(redisConfiguration, clientConfig);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory());
        return redisMessageListenerContainer;
    }

//    @Bean
//    public RedisConnectionFactory redisConnectionFactoryToken() {
//        RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
//        redisConfiguration.setHostName(host);
//        redisConfiguration.setPort(port);
//        redisConfiguration.setDatabase(1);
//        return new LettuceConnectionFactory(redisConfiguration);
//    }


    @Bean(name = "redisTemplate")
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());/**/
        return redisTemplate;
    }

//    @Bean(name = "redisTemplateToken")
//    public RedisTemplate<?, ?> redisTemplateToken() {
//        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactoryToken());
//        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
//        return redisTemplate;
//    }

}