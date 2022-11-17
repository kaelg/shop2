package pl.waw.great.shop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import pl.waw.great.shop.exception.CartIsEmptyException;
import pl.waw.great.shop.exception.InsufficientProductQuantityException;
import pl.waw.great.shop.exception.UserWithGivenNameNotExistsException;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.OrderDto;
import pl.waw.great.shop.model.mapper.OrderLineMapper;
import pl.waw.great.shop.model.mapper.OrderMapper;
import pl.waw.great.shop.repository.CartRepository;
import pl.waw.great.shop.repository.OrderRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final OrderLineMapper orderLineMapper;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    private static final double coinsMultiplayer = 0.05;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, OrderMapper orderMapper, OrderLineMapper orderLineMapper, ProductRepository productRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.orderLineMapper = orderLineMapper;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public OrderDto createOrder() {
        User user = this.getAuthenticatedUser();

        Cart cartByUserId = this.cartRepository.findCartByUserId(user.getId());
        List<OrderLineItem> orderItems =
                cartByUserId.getCartLineItemList()
                        .stream()
                        .map(orderLineMapper::cartItemToOrderItem)
                        .collect(Collectors.toList());

        if (orderItems.isEmpty()) {
            throw new CartIsEmptyException();
        }

        Order order = Order.builder()
                .totalPrice(getOrderTotalAmount(orderItems))
                .user(user)
                .orderLineItemList(orderItems)
                .created(LocalDateTime.now())
                .build();

        OrderDto orderDto = orderMapper.orderToDto(this.orderRepository.create(order));
        this.updateProductsQuantity(orderItems);
        this.cartRepository.delete(cartByUserId.getId());
        user.addCoins(countNumberOfCoinsForOrder(order.getTotalPrice()));
        this.userRepository.updateUser(user);
        return orderDto;
    }

    public List<OrderDto> getUserOrders() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = this.userRepository.findUserByTitle(authentication.getName())
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(authentication.getName()));

        List<Order> ordersByUserId = this.orderRepository.getOrdersByUserId(user.getId());

        return ordersByUserId.stream().map(orderMapper::orderToDto).collect(Collectors.toList());
    }

    public OrderDto getOrderById(@PathVariable String orderId) {
        return orderMapper.orderToDto(this.orderRepository.getOrderById(orderId));
    }

    private Long countNumberOfCoinsForOrder(BigDecimal orderAmount) {
        return Math.round(orderAmount.longValue() * coinsMultiplayer);
    }

    private BigDecimal getOrderTotalAmount(List<OrderLineItem> orderItems) {
        return orderItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void updateProductsQuantity(List<OrderLineItem> itemList) {
        itemList.forEach(item -> {
            Product product = item.getProduct();
            long updatedQuantity = product.getQuantity() - item.getQuantity();

            if (updatedQuantity < 0) {
                throw new InsufficientProductQuantityException(product.getTitle(), product.getQuantity());
            }
            product.setQuantity(updatedQuantity);
            this.productRepository.updateProduct(product);
        });
    }

    public OrderDto createAuctionWinnerOrder(Auction auction, User winner) {
        OrderLineItem orderLineItem = OrderLineItem.builder()
                .product(auction)
                .quantity(auction.getQuantity())
                .build();

        List<OrderLineItem> orderLineItems = Collections.singletonList(orderLineItem);

        Order order = Order.builder()
                .totalPrice(auction.getPrice())
                .user(winner)
                .orderLineItemList(orderLineItems)
                .created(LocalDateTime.now())
                .build();

        OrderDto orderDto = orderMapper.orderToDto(this.orderRepository.create(order));
        this.updateProductsQuantity(orderLineItems);

        return orderDto;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return this.userRepository.findUserByTitle(authentication.getName())
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(authentication.getName()));
    }
}
