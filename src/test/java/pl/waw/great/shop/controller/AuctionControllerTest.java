package pl.waw.great.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import pl.waw.great.shop.model.Auction;
import pl.waw.great.shop.model.User;
import pl.waw.great.shop.model.dto.AuctionDto;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.repository.UserRepository;
import pl.waw.great.shop.service.ProductService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuctionControllerTest {

    private static final String PRODUCT_TITLE = "iPhone 14S";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private Auction auction;

    @BeforeEach
    void setUp() {
        this.auction = new Auction();
        this.auction.setTitle(PRODUCT_TITLE);
        this.auction.setStart(LocalDateTime.now());
        this.auction.setEnds(LocalDateTime.now());
        this.productRepository.createProduct(this.auction);
        this.userRepository.create(new User("user"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @Transactional
    void bid() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/auction/" + PRODUCT_TITLE + "/1501")
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        AuctionDto saved = objectMapper.readValue(result.getResponse().getContentAsString(), AuctionDto.class);

        assertEquals(1, saved.getBids().size());

    }

    @Test
    @WithMockUser(roles = "USER")
    void getAuction() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.get("/auction/" + PRODUCT_TITLE)
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        AuctionDto saved = objectMapper.readValue(result.getResponse().getContentAsString(), AuctionDto.class);

        assertEquals(saved.getTitle(), this.auction.getTitle());
    }

    private MvcResult sendRequest(RequestBuilder request, HttpStatus expectedStatus) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}