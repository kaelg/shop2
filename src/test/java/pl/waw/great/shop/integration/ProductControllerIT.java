package pl.waw.great.shop.integration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.dto.ProductDTO;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductControllerIT {

    private static final String PRODUCT_TITLE = "iPhone 14";

    private static final String PRODUCT_TITLE_2 = "Samsung Galaxy S22";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final String DESCRIPTION_2 = "The Samsung Galaxy is a line of smartphones by Samsung";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(1200);

    private static final CategoryType CATEGORY = CategoryType.ELEKTRONIKA;

    private Long QUANTITY = 5L;

    private Long productToUpdateId;
    private ProductDTO productToUpdate;

    private RequestSpecification requestSpecification;
    private ResponseSpecification responseSpecification;

    @BeforeEach
    public void setUp() {

        this.productToUpdate = new ProductDTO(PRODUCT_TITLE_2, DESCRIPTION_2, PRICE_2, CATEGORY, QUANTITY, AuctionType.KUP_TERAZ);

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://localhost:8080/")
                .setBasePath("product")
                .setContentType(ContentType.JSON)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();

        productToUpdateId = given().spec(requestSpecification).body(this.productToUpdate)
                .when().post()
                .then().spec(responseSpecification).extract().body().as(ProductDTO.class).getId();

        RequestLoggingFilter requestLoggingFilter = new RequestLoggingFilter();
        ResponseLoggingFilter responseLoggingFilter = new ResponseLoggingFilter();
        RestAssured.filters(requestLoggingFilter, responseLoggingFilter);
    }

    @AfterEach
    public void tearDown() {
        given()
                .spec(requestSpecification).
                when()
                .delete()
                .then()
                .spec(responseSpecification);
    }

    @Test
    void createProduct() {
        ProductDTO newProduct = new ProductDTO(PRODUCT_TITLE, DESCRIPTION, PRICE, CATEGORY, QUANTITY, AuctionType.KUP_TERAZ);
        ProductDTO createdProductDto = given().spec(requestSpecification).body(newProduct)
                .when().post()
                .then().spec(responseSpecification).extract().body().as(ProductDTO.class);

        assertNotNull(createdProductDto);
        assertEquals(createdProductDto, newProduct);
    }

    @Test
    void updateProduct() {
        ProductDTO newProductData = new ProductDTO("NEW_TITLE", "NEW_DESCRIPTION", BigDecimal.valueOf(1500), CATEGORY, QUANTITY, AuctionType.KUP_TERAZ);

        ProductDTO updatedProductDto = given().pathParam("id", productToUpdateId)
                .spec(requestSpecification).body(newProductData)
                .when().put("{id}")
                .then().spec(responseSpecification).extract().body().as(ProductDTO.class);

        assertNotNull(updatedProductDto);
        assertEquals(newProductData, updatedProductDto);
    }

    @Test
    void getProductById() {
        ProductDTO savedDto = given().pathParam("id", productToUpdateId)
                .spec(requestSpecification)
                .when().get("{id}")
                .then().spec(responseSpecification).extract().body().as(ProductDTO.class);

        assertNotNull(savedDto);
        assertEquals(savedDto.getTitle(), this.productToUpdate.getTitle());
    }

    @Test
    void findAllProducts() {
        List<ProductDTO> allProducts = given()
                .spec(requestSpecification)
                .when().get()
                .then().spec(responseSpecification).
                extract().body().jsonPath().getList(".", ProductDTO.class);

        assertEquals(1, allProducts.size());
        assertEquals(productToUpdate.getTitle(), allProducts.get(0).getTitle());
    }

    @Test
    void deleteProductById() {
        boolean isDeleted = given().pathParam("id", this.productToUpdateId)
                .spec(requestSpecification).
                when()
                .delete("{id}")
                .then()
                .spec(responseSpecification).extract().as(Boolean.class);

        assertTrue(isDeleted);
    }

    @Test
    void deleteAllProducts() {
        boolean isDeleted = given()
                .spec(requestSpecification).
                when()
                .delete()
                .then()
                .spec(responseSpecification).extract().as(Boolean.class);

        assertTrue(isDeleted);
    }
}