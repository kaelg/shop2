package pl.waw.great.shop.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.InsufficientProductQuantityException;
import pl.waw.great.shop.exception.ProductWithGivenTitleNotExistsException;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.CartDto;
import pl.waw.great.shop.model.mapper.CartLineItemMapper;
import pl.waw.great.shop.model.mapper.CartMapper;
import pl.waw.great.shop.repository.CartRepository;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class CartServiceTest {

    private static final String PRODUCT_NAME = "iPhone 14";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static final Long QUANTITY = 5L;

    private List<CartLineItem> cartItems;

    private CartLineItem cartLineItem;

    private Cart cart;
    private Category category;

    private Product product;

    private CartRepository cartRepository = mock(CartRepository.class);

    private ProductRepository productRepository = mock(ProductRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private User user;

    @InjectMocks
    private CartService cartService;

    @Spy
    private CartMapper cartMapper = Mappers.getMapper(CartMapper.class);

    @Spy
    private CartLineItemMapper cartLineItemMapper = Mappers.getMapper(CartLineItemMapper.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = Product.builder()
                .title(PRODUCT_NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();
        this.user = new User("Mikolaj");
        this.cart = new Cart();
        this.cart.setUser(this.user);
        this.cartLineItem = CartLineItem.builder()
                .product(product)
                .cart(cart)
                .cartIndex(1)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .quantity(2L)
                .build();        this.cartItems = new ArrayList<>();
        this.cartItems.add(this.cartLineItem);
        this.cart.setCartLineItemList(this.cartItems);
        this.cart.setTotalAmount(BigDecimal.valueOf(100));

        ReflectionTestUtils.setField(
                cartMapper,
                "cartLineItemMapper",
                cartLineItemMapper
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void create() {
        when(this.productRepository.findProductByTitle(anyString())).thenReturn(Optional.of(this.product));
        when(this.cartRepository.findCartByUserId(any())).thenReturn(this.cart);
        when(this.userRepository.findUserByTitle(anyString())).thenReturn(Optional.ofNullable(this.user));
        when(this.cartRepository.create(any())).thenReturn(this.cart);

        CartDto cartDto = this.cartService.create(PRODUCT_NAME, 1L);
        assertNotNull(cartDto);
        assertEquals(cartDto.getTotalAmount(), this.cart.getTotalAmount());

    }

    @Test
    void createWithNotExistingProductShouldThrowException() {
        when(this.productRepository.findProductByTitle(anyString())).thenReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProductWithGivenTitleNotExistsException.class, () -> {
            this.cartService.create("Samsung", 1L);
        });

    }

    @Test
    @WithMockUser(roles = "USER")
    void createWithToHigherAmountShouldThrowException() {
        when(this.productRepository.findProductByTitle(anyString())).thenReturn(Optional.of(this.product));
        when(this.cartRepository.findCartByUserId(any())).thenReturn(this.cart);
        when(this.userRepository.findUserByTitle(anyString())).thenReturn(Optional.ofNullable(this.user));

        Assertions.assertThrows(InsufficientProductQuantityException.class, () -> {
            this.cartService.create(PRODUCT_NAME, 10L);
        });

    }

    @Test
    @WithMockUser(roles = "USER")
    void get() {
        when(this.cartRepository.findCartByUserId(any())).thenReturn(this.cart);
        when(this.userRepository.findUserByTitle(anyString())).thenReturn(Optional.ofNullable(this.user));

        CartDto userCart = this.cartService.getUserCart(this.user.getName());

        assertEquals(userCart.getUserName(), this.user.getName());
    }
}