package pl.waw.great.shop.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.model.Order;
import pl.waw.great.shop.model.OrderLineItem;
import pl.waw.great.shop.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OrderRepositoryTest {

    private final BigDecimal TOTAL_PRICE = BigDecimal.TEN;

    private User user;

    private final List<OrderLineItem> orderLineItemList = new ArrayList<>();

    private Order order;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.user = new User("John");
        this.userRepository.create(user);

        this.order = Order.builder()
                .totalPrice(TOTAL_PRICE)
                .user(user)
                .orderLineItemList(orderLineItemList)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void create() {
        Order saved = this.orderRepository.create(order);
        assertEquals(saved, this.order);
    }
}