package pl.waw.great.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.CartDto;
import pl.waw.great.shop.repository.CartRepository;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Disabled
class CartControllerTest {

    private static final String PRODUCT_NAME = "iPhone 14";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static  final Long QUANTITY = 5L;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private CartLineItem cartLineItem;

    private Cart cart;
    private Category category;

    private Product product;


    @BeforeEach
    void setUp() {
        this.user = this.userRepository.create(new User("John1"));
        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = Product.builder()
                .title(PRODUCT_NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();

        this.productRepository.createProduct(this.product);
        this.cart = new Cart();
        this.cartLineItem = CartLineItem.builder()
                .product(product)
                .cart(cart)
                .cartIndex(1)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .quantity(2L)
                .build();
        this.cart.setTotalAmount(BigDecimal.valueOf(100));
        this.cart.addCartLineItem(this.cartLineItem);
        this.cart.setUser(this.user);
        this.cartRepository.create(cart);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCart() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.get("/cart/" + this.user.getName())
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        CartDto cartDto = objectMapper.readValue(
                result.getResponse().getContentAsString(),CartDto.class);

        assertNotNull(cartDto);
        assertEquals(PRODUCT_NAME, cartDto.getCartLineItemList().get(0).getProductTitle());
    }


    private MvcResult sendRequest(RequestBuilder request, HttpStatus expectedStatus) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}