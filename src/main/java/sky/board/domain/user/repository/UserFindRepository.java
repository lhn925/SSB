package sky.board.domain.user.repository;


import jakarta.persistence.EntityManager;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import sky.board.domain.user.entity.User;

//@Repository
@RequiredArgsConstructor
public class UserFindRepository{


    private final EntityManager em;
    public Optional<User> findById (Long findId) {
        return Optional.ofNullable(em.find(User.class, findId));
    }


    public Optional<User> findByUserId (String userid) {
        return em.createQuery("select u from User u where u.userId =: userid", User.class).setParameter("userid", userid).getResultList().stream().findFirst();
    }
    public Optional<User> findByUserName (String username) {
        return em.createQuery("select u from User u where u.userName =: username", User.class)
            .setParameter("username", username).getResultList().stream().findFirst();
    }

    public Optional<User> findByEmail (String email) {
        return em.createQuery("select u from User u where u.email =: email", User.class).setParameter("email", email).getResultList().stream().findFirst();
    }
    public Optional<User> findBySalt (String salt) {
        return em.createQuery("select u from User u where u.salt =: salt", User.class).setParameter("salt", salt).getResultList().stream().findFirst();
    }
}
