package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.exception.CreatingUserFailedException;
import pl.waw.great.shop.exception.UserWithGivenNameNotExistsException;
import pl.waw.great.shop.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public User create(User user) {
        user.setCreated(LocalDateTime.now());
        user.setUpdated(LocalDateTime.now());
        this.entityManager.persist(user);

        return this.findUserByTitle(user.getName())
                .orElseThrow(() -> new CreatingUserFailedException(user.getName()));
    }

    @Transactional
    public Optional<User> findUserByTitle(String name) {
        TypedQuery<User> query = this.entityManager.createQuery("SELECT u FROM User u WHERE u.name=:name", User.class);
        query.setParameter("name", name);
        return query.getResultStream()
                .findFirst();
    }

    @Transactional
    public boolean delete(String name) {
        User userToDelete = this.findUserByTitle(name)
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(name));

        this.entityManager.remove(userToDelete);

        return true;
    }

    @Transactional
    public User updateUser(User user) {
        user.setUpdated(LocalDateTime.now());
        this.entityManager.merge(user);
        return user;
    }

}
