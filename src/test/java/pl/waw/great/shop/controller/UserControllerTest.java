package pl.waw.great.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
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
import pl.waw.great.shop.model.User;
import pl.waw.great.shop.model.dto.UserDto;
import pl.waw.great.shop.repository.UserRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String NAME = "Jacek";

    private static final String NAME_2 = "Bogdan";

    private static final String NOT_EXISTING_NAME = "Robert";

    private UserDto userDto;

    private User user_2;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.userDto = new UserDto(NAME);
        this.user_2 = new User(NAME_2);
        this.userRepository.create(this.user_2);
    }

    @AfterEach
    void tearDown() {
        this.userRepository.delete(userDto.getName());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createUser() throws Exception {
        String userDtoAsJson = objectMapper.writeValueAsString(this.userDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/user/create")
                .content(userDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.OK);

        UserDto savedUserDto = objectMapper.readValue(result.getResponse().getContentAsString(), UserDto.class);

        assertNotNull(savedUserDto);
        assertEquals(savedUserDto.getName(), this.userDto.getName());
    }

    private MvcResult sendRequest(RequestBuilder request, HttpStatus expectedStatus) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}