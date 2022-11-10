package pl.waw.great.shop.controller;

import org.springframework.web.bind.annotation.*;
import pl.waw.great.shop.model.dto.CartDto;
import pl.waw.great.shop.service.CartService;


@RestController
@RequestMapping("/cart")
@CrossOrigin
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userName}")
    public CartDto getUserCart(@PathVariable String userName) {

        return this.cartService.getUserCart(userName);
    }

    @DeleteMapping("/{index}")
    public CartDto removeItem(@PathVariable int index) {
        return this.cartService.removeItem(index);
    }
}
