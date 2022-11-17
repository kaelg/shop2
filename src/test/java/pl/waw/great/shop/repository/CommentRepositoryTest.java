package pl.waw.great.shop.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Comment;
import pl.waw.great.shop.model.Product;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    private final static String TEST_NAME = "USER";

    private final static String TEST_EMAIL = "example@gmail.com";

    private final static String TEST_TEXT = "TLDR";
    @Autowired
    private CommentRepository commentRepository;

    private Comment comment;


    @BeforeEach
    void setUp() {
        this.comment = this.commentRepository.create(Comment.builder()
                .name(TEST_NAME)
                .email(TEST_EMAIL)
                .text(TEST_TEXT)
                .build());
    }

    @AfterEach
    void tearDown() {
        this.commentRepository.deleteAll();
    }

    @Test
    void create() {
        assertNotNull(comment);
        assertEquals(TEST_EMAIL, this.comment.getEmail());

    }

    @Test
    void delete() {
        boolean isDeleted = this.commentRepository.delete(this.comment.getId());
        assertTrue(isDeleted);
        Comment deleted = this.commentRepository.get(this.comment.getId());
        assertNull(deleted);
    }

    @Test
    void get() {
        Comment saved = this.commentRepository.get(this.comment.getId());
        assertEquals(this.comment, comment);
    }
}