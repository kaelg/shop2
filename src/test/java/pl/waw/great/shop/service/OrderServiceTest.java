package pl.waw.great.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.OrderDto;
import pl.waw.great.shop.model.mapper.OrderLineMapper;
import pl.waw.great.shop.model.mapper.OrderMapper;
import pl.waw.great.shop.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class OrderServiceTest {

    private static final String PRODUCT_NAME = "iPhone 14";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);
    private static final Long QUANTITY = 5L;

    public static final String NAME = "Mikolaj";

    private Category category;

    private Product product;

    private User user;

    private Order order;

    private OrderLineItem orderLineItem;

    private List<OrderLineItem> orderItems = new ArrayList<>();

    private CartLineItem cartLineItem;

    private Cart cart;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private ProductRepository productRepository = mock(ProductRepository.class);

    @InjectMocks
    private OrderService orderService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Spy
    OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Spy
    OrderLineMapper orderLineMapper = Mappers.getMapper(OrderLineMapper.class);

    @BeforeEach
    void setUp() {
        this.user = new User(NAME);
        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = Product.builder()
                .title(PRODUCT_NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();
        this.orderLineItem = OrderLineItem.builder()
                .product(this.product)
                .quantity(2L)
                .build();
        this.orderItems.add(this.orderLineItem);
        this.cart = new Cart();
        this.cart.setUser(this.user);
        this.cartLineItem = CartLineItem.builder()
                .product(product)
                .cart(cart)
                .cartIndex(1)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .quantity(2L)
                .build();
        this.cart.addCartLineItem(this.cartLineItem);
        this.order = Order.builder()
                .totalPrice(BigDecimal.ONE)
                .user(user)
                .orderLineItemList(this.orderItems)
                .created(LocalDateTime.now())
                .build();
        this.order.setId(UUID.randomUUID());

        ReflectionTestUtils.setField(
                orderMapper,
                "orderLineMapper",
                orderLineMapper
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder() {
        when(this.cartRepository.findCartByUserId(any())).thenReturn(this.cart);
        when(this.orderRepository.create(any())).thenReturn(this.order);
        when(this.userRepository.findUserByTitle(anyString())).thenReturn(Optional.of(this.user));
        when(this.userRepository.create(any())).thenReturn(this.user);
        OrderDto order = this.orderService.createOrder();

        assertNotNull(order);
        assertEquals(BigDecimal.ONE, order.getTotalPrice());
    }
}