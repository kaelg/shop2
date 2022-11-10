package pl.waw.great.shop.controller;

import org.springframework.web.bind.annotation.*;
import pl.waw.great.shop.model.dto.UserDto;
import pl.waw.great.shop.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {

        return this.userService.create(userDto);
    }

    @DeleteMapping("/{name}")
    public boolean deleteUser(@PathVariable String name) {
        return this.userService.delete(name);
    }

    @GetMapping
    public Long getUserCoinsBalance() {
        return this.userService.getUserCoinsBalance();
    }
}
