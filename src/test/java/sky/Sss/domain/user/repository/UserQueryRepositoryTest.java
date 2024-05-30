package sky.Sss.domain.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sky.Sss.domain.user.entity.User;
import sky.Sss.domain.user.model.Enabled;

@SpringBootTest
class UserQueryRepositoryTest {


    @Autowired
    UserQueryRepository userQueryRepository;


    @Test
    public void queryTest() {

        Set<Long> ids = new HashSet<>();
//        ids.add("1");
//        ids.add("2");

        List<User> byUserIds = userQueryRepository.findByIds(ids, Enabled.ENABLED());

        System.out.println("byUserIds = " + byUserIds);
    }

}