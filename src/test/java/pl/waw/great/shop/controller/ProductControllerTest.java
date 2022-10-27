package pl.waw.great.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.ErrorInfo;
import pl.waw.great.shop.model.Comment;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.model.dto.CommentDto;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.dto.ProductListElementDto;
import pl.waw.great.shop.repository.CommentRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.service.CommentService;
import pl.waw.great.shop.service.ProductService;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    private static final String PRODUCT_TITLE = "iPhone 14";

    private static final String PRODUCT_TITLE_2 = "Samsung Galaxy S22";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final String DESCRIPTION_2 = "The Samsung Galaxy is a line of smartphones by Samsung";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(899);

    private static final Long NOT_EXISTING_ID = 500L;

    private static final CategoryType CATEGORY_NAME = CategoryType.EDUKACJA;

    private final static String TEST_NAME = "USER";

    private final static String TEST_EMAIL = "example@gmail.com";

    private final static String TEST_TEXT = "TLDR";

    private final static String NOT_EXISTING_TITLE = "EXAMPLE";

    private ProductDTO productDTO;

    private ProductDTO toUpdateDto;

    private CommentDto commentDto;

    private Long createdProductId;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @BeforeEach
    void setup() {
        this.commentDto = new CommentDto(TEST_NAME, TEST_EMAIL, TEST_TEXT);
        this.productDTO = new ProductDTO(PRODUCT_TITLE, DESCRIPTION, PRICE, CATEGORY_NAME);
        this.toUpdateDto = new ProductDTO(PRODUCT_TITLE_2, DESCRIPTION_2, PRICE_2, CATEGORY_NAME);
        this.createdProductId = this.productService.createProduct(this.toUpdateDto).getId();
        this.commentService.createComment(this.toUpdateDto.getTitle(), this.commentDto);
    }

    @AfterEach
    void tearDown() {
        this.commentRepository.deleteAll();
        this.productService.deleteAllProducts();
    }

    @Test
    @Transactional
    void createProduct() throws Exception {
        String productDtoAsJson = objectMapper.writeValueAsString(this.productDTO);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product")
                .content(productDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.OK);

        ProductDTO createdProductDto = objectMapper.readValue(result.getResponse().getContentAsString(), ProductDTO.class);
        assertNotNull(createdProductDto);
        assertEquals(this.productDTO, createdProductDto);
    }

    @Test
    @Transactional
    void createProductWithDuplicateTitleShouldThrowException() throws Exception {
        String productDtoAsJson = objectMapper.writeValueAsString(this.toUpdateDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product")
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);


        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Product with title: " + PRODUCT_TITLE_2 + " already exists", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void createWithBlankTitleShouldThrowException() throws Exception {
        ProductDTO dtoWithBlankTitle = new ProductDTO("", DESCRIPTION, PRICE_2, CATEGORY_NAME);
        String productDtoAsJson = objectMapper.writeValueAsString(dtoWithBlankTitle);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product")
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Title cannot be blank", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void createWithBlankDescriptionShouldThrowException() throws Exception {
        ProductDTO dtoWithBlankDescription = new ProductDTO(PRODUCT_TITLE, "", PRICE_2, CATEGORY_NAME);
        String productDtoAsJson = objectMapper.writeValueAsString(dtoWithBlankDescription);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product")
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Description cannot be blank", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void createWithPriceZeroException() throws Exception {
        ProductDTO dtoWithPriceZero = new ProductDTO(PRODUCT_TITLE, DESCRIPTION, BigDecimal.ZERO, CATEGORY_NAME);
        String productDtoAsJson = objectMapper.writeValueAsString(dtoWithPriceZero);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product")
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.BAD_REQUEST);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Price must be higher than 1", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void updateProduct() throws Exception {
        String productDtoAsJson = objectMapper.writeValueAsString(this.productDTO);

        MvcResult result = sendRequest(MockMvcRequestBuilders.put("/product/" + this.createdProductId)
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.OK);

        ProductDTO updatedDto = objectMapper.readValue(result.getResponse().getContentAsString(), ProductDTO.class);
        assertNotNull(updatedDto);
        assertEquals(updatedDto, this.productDTO);
    }

    @Test
    @Transactional
    void updateToDuplicateTitleShouldThrowException() throws Exception {
        ProductDTO newData = new ProductDTO(PRODUCT_TITLE_2, "TEST DESCRIPTION", BigDecimal.valueOf(1500), CATEGORY_NAME);
        String productDtoAsJson = objectMapper.writeValueAsString(newData);

        MvcResult result = sendRequest(MockMvcRequestBuilders.put("/product/" + this.createdProductId)
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Product with title: " + PRODUCT_TITLE_2 + " already exists", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void updateWithNotExistingIdShouldThrowException() throws Exception {
        ProductDTO newData = new ProductDTO("NEW TITLE", "TEST DESCRIPTION", BigDecimal.valueOf(1500), CATEGORY_NAME);
        String productDtoAsJson = objectMapper.writeValueAsString(newData);

        MvcResult result = sendRequest(MockMvcRequestBuilders.put("/product/" + NOT_EXISTING_ID)
                .content(productDtoAsJson).contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Product with id: " + NOT_EXISTING_ID + " not exists", exceptionDtoResponse.getMessage());
    }
    @Test
    @Transactional
    void findAllProducts() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.get("/product")
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        List<ProductListElementDto> allProducts = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, ProductDTO.class));

        assertNotNull(allProducts);
        assertEquals(1, allProducts.size());
    }

    @Test
    @Transactional
    void deleteProductById() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.delete("/product/" + this.createdProductId)
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        boolean isDeleted = objectMapper.readValue(result.getResponse().getContentAsString(), Boolean.class);
        assertTrue(isDeleted);
    }

    @Test
    @Transactional
    void addComment() throws Exception {
        String commentDtoAsJson = objectMapper.writeValueAsString(this.commentDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product/" + this.toUpdateDto.getTitle())
                .content(commentDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.OK);

        CommentDto createdCommentDto = objectMapper.readValue(result.getResponse().getContentAsString(), CommentDto.class);
        assertNotNull(createdCommentDto);
        assertEquals(createdCommentDto, this.commentDto);
    }

    @Test
    @Transactional
    void deleteComment() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.delete("/product/" + PRODUCT_TITLE_2 + "/" + 0)
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);
        boolean isDeleted = objectMapper.readValue(result.getResponse().getContentAsString(), Boolean.class);
        Optional<Product> productByTitle = this.productRepository.findProductByTitle(this.toUpdateDto.getTitle());
        int commentsNumber = productByTitle.get().getCommentsList().size();
        assertTrue(isDeleted);
        assertEquals(0, commentsNumber);
    }

    @Test
    @Transactional
    void findByTitle() throws Exception {
        MvcResult result = sendRequest(MockMvcRequestBuilders.get("/product/" + PRODUCT_TITLE_2)
                .content(String.valueOf(MediaType.APPLICATION_JSON)), HttpStatus.OK);

        ProductDTO saved = objectMapper.readValue(result.getResponse().getContentAsString(), ProductDTO.class);

        assertNotNull(saved);
        assertEquals(saved, this.toUpdateDto);
    }

    @Test
    @Transactional
    void addCommentToWithNotExistingProductTitleShouldThrowException() throws Exception {
        String commentDtoAsJson = objectMapper.writeValueAsString(this.commentDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.post("/product/" + NOT_EXISTING_TITLE)
                .content(commentDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Product with title: " + NOT_EXISTING_TITLE + " not exists", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void deleteCommentWithNotExistingProductTitleShouldThrowException() throws Exception {
        String commentDtoAsJson = objectMapper.writeValueAsString(this.commentDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.delete("/product/" + NOT_EXISTING_TITLE + "/0")
                .content(commentDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Product with title: " + NOT_EXISTING_TITLE + " not exists", exceptionDtoResponse.getMessage());
    }

    @Test
    @Transactional
    void deleteCommentWithInvalidIndexShouldThrowException() throws Exception {
        String commentDtoAsJson = objectMapper.writeValueAsString(this.commentDto);

        MvcResult result = sendRequest(MockMvcRequestBuilders.delete("/product/" + PRODUCT_TITLE_2 + "/50")
                .content(commentDtoAsJson)
                .contentType(MediaType.APPLICATION_JSON), HttpStatus.CONFLICT);

        ErrorInfo exceptionDtoResponse = objectMapper.readValue(result.getResponse().getContentAsString(), ErrorInfo.class);
        assertEquals("Invalid comment index: " + 50, exceptionDtoResponse.getMessage());
    }

    private MvcResult sendRequest(RequestBuilder request, HttpStatus expectedStatus) throws Exception {
        return mockMvc.perform(request)
                .andExpect(status().is(expectedStatus.value()))
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}