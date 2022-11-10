package pl.waw.great.shop.repository;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.model.User;

import javax.naming.Name;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    private static final String NAME = "Zbigniew";

    private static final String NAME_2 = "Pawel";
    private User user;

    private User user_2;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        this.user = new User(NAME);
        this.user_2 = new User(NAME_2);
        this.userRepository.create(user_2);
    }

    @Test
    void create() {
        User createdUser = this.userRepository.create(user);
        assertNotNull(createdUser);
        assertEquals(this.user, createdUser);
    }

    @Test
    void findUserByTitle() {
        Optional<User> userByTitle = this.userRepository.findUserByTitle(NAME_2);

        assertTrue(userByTitle.isPresent());
        assertEquals(userByTitle.get(), user_2);
    }

    @Test
    void delete() {
        boolean isDeleted = this.userRepository.delete(NAME_2);
        assertTrue(isDeleted);
    }
}