package pl.waw.great.shop.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.waw.great.shop.exception.InsufficientProductQuantityException;
import pl.waw.great.shop.exception.ProductWithGivenTitleNotExistsException;
import pl.waw.great.shop.exception.UserWithGivenNameNotExistsException;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.CartDto;
import pl.waw.great.shop.model.mapper.CartMapper;
import pl.waw.great.shop.repository.CartRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.repository.UserRepository;

import java.time.LocalDateTime;


@Service
public class CartService {

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;


    public CartService(ProductRepository productRepository, CartRepository cartRepository, UserRepository userRepository, CartMapper cartMapper) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.cartMapper = cartMapper;
    }

    public CartDto create(String productTitle, Long amount) {
        Product product = this.productRepository.findProductByTitle(productTitle)
                .orElseThrow(() -> new ProductWithGivenTitleNotExistsException(productTitle));

        User user = this.getAuthenticatedUser();
        Cart cart = this.cartRepository.findCartByUserId(user.getId());

        cart.setUser(user);
        CartLineItem cartLineItem = new CartLineItem(product,
                cart,
                cart.getSize() + 1,
                LocalDateTime.now(),
                LocalDateTime.now(),
                amount);

        if (amount + cart.getProductInCartAmount(cartLineItem) > product.getQuantity()) {
            throw new InsufficientProductQuantityException(productTitle, product.getQuantity());
        }
        cart.addCartLineItem(cartLineItem);
        return cartMapper.cartToDto(this.cartRepository.create(cart));
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return this.userRepository.findUserByTitle(authentication.getName())
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(authentication.getName()));
    }

    public CartDto removeItem(int index) {
        User user = this.getAuthenticatedUser();
        Cart cart = this.cartRepository.findCartByUserId(user.getId());
        cart.removeItem(index);
        return cartMapper.cartToDto(this.cartRepository.update(cart));
    }

    public CartDto getUserCart(String userName) {
        User user = this.userRepository.findUserByTitle(userName)
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(userName));

        Cart cartByUserId = this.cartRepository.findCartByUserId(user.getId());
        if (cartByUserId.getUser() == null) {
            cartByUserId.setUser(this.getAuthenticatedUser());
        }
        return cartMapper.cartToDto(cartByUserId);
    }

}
