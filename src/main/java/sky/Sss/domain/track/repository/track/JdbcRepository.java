package sky.Sss.domain.track.repository.track;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface JdbcRepository<T> {

    void saveAll(List<T> entityList, LocalDateTime createdDateTime);

    void save(T entity);
}
