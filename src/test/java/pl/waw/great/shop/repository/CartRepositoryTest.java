package pl.waw.great.shop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartRepositoryTest {

    private static final String PRODUCT_NAME = "iPhone 14";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static  final Long QUANTITY = 5L;
    private Category category;

    private Product product;

    private List<CartLineItem> cartItems;

    private CartLineItem cartLineItem;

    private User user;

    private Cart cart;

    private Cart saved;
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {

        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = new Product(PRODUCT_NAME, DESCRIPTION, PRICE, this.category, QUANTITY);
        this.productRepository.createProduct(product);
        this.user = this.userRepository.create(new User("Rafal"));
        this.cart = new Cart();
        this.cartLineItem = new CartLineItem(this.product, this.cart, 1, LocalDateTime.now(), LocalDateTime.now(), 2L);
        this.cartItems = new ArrayList<>();
        this.cartItems.add(this.cartLineItem);
        this.cart.setCartLineItemList(this.cartItems);
        this.saved = this.cartRepository.create(cart);
    }
    @Test
    void create() {
        assertNotNull(this.saved);
        assertEquals(this.saved, this.cart);
    }

    @Test
    void update() {
        this.cart.removeItem(1);
        Cart updatd = this.cartRepository.update(this.cart);

        assertEquals(0, updatd.getSize());
    }

    @Test
    void findCartByUserId() {
        Cart cartByUserId = this.cartRepository.findCartByUserId(this.user.getId());
        assertEquals(cartByUserId.getUser(), this.saved.getUser());
    }

    @Test
    void findCartById() {
        Cart cartById = this.cartRepository.findCartById(this.saved.getId());
        assertEquals(cartById.getId(), this.saved.getId());
    }

    @Test
    void delete() {
        boolean delete = this.cartRepository.delete(this.saved.getId());
        assertTrue(delete);
    }
}