package pl.waw.great.shop.repository;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductRepositoryTest {

    private static final String PRODUCT_NAME = "iPhone 14";

    private static final String PRODUCT_NAME_2 = "Samsung Galaxy S22";
    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final String DESCRIPTION_2 = "The Samsung Galaxy is a line of smartphones by Samsung";
    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static final BigDecimal PRICE_2 = BigDecimal.valueOf(899);

    private Category category;

    private Long QUANTITY = 5L;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = Product.builder()
                .title(PRODUCT_NAME)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();
        this.productRepository.createProduct(this.product);
    }


    @Test
    @Transactional
    void create() {
        assertNotNull(this.product.getId());
        this.productRepository.createProduct(Product.builder()
                .title("title")
                .description("description")
                .price(BigDecimal.valueOf(15))
                .category(this.category)
                .quantity(QUANTITY).build());
    }

    @Test
    @Transactional
    void get() {
        Product savedProduct = this.productRepository.getProduct(this.product.getId());
        assertNotNull(savedProduct);
    }

    @Test
    @Transactional
    void delete() {
        boolean isDeleted = this.productRepository.deleteProduct(this.product.getId());
        assertTrue(isDeleted);
    }

    @Test
    @Transactional
    void update() {
        Product newProduct = Product.builder()
                .title(PRODUCT_NAME_2)
                .description(DESCRIPTION_2)
                .price(PRICE_2)
                .category(this.category)
                .quantity(QUANTITY)
                .build();
        newProduct.setId(this.product.getId());
        Product updatedProduct = this.productRepository.updateProduct(newProduct);
        assertEquals(updatedProduct, newProduct);
    }


    @Test
    @Transactional
    void findProductByTitle() {
        Optional<Product> productByTitle = this.productRepository.findProductByTitle(PRODUCT_NAME);

        assertNotNull(productByTitle);
    }


}