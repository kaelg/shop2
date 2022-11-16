package pl.waw.great.shop.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;

import java.math.BigDecimal;
import java.util.List;
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

    @Autowired
    private OrderRepository orderRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        this.category = categoryRepository.findCategoryByName(CategoryType.ELEKTRONIKA);
        this.product = new Product(PRODUCT_NAME, DESCRIPTION, PRICE, this.category, QUANTITY);
        this.productRepository.createProduct(this.product);
    }

    @AfterEach
    void tearDown() {
        this.orderRepository.deleteAll();
        this.productRepository.deleteAll();
    }

    @Test
    void create() {
        assertNotNull(this.product.getId());
        this.productRepository.createProduct(new Product("title", "ddses", BigDecimal.valueOf(15), this.category, QUANTITY));
    }

    @Test
    void get() {
        Product savedProduct = this.productRepository.getProduct(this.product.getId());
        assertNotNull(savedProduct);
    }

    @Test
    void delete() {
        boolean isDeleted = this.productRepository.deleteProduct(this.product.getId());
        assertTrue(isDeleted);
    }

    @Test
    void update() {
        Product newProduct = new Product(PRODUCT_NAME_2, DESCRIPTION_2, PRICE_2, this.category, QUANTITY);
        newProduct.setId(this.product.getId());
        Product updatedProduct = this.productRepository.updateProduct(newProduct);
        assertEquals(updatedProduct, newProduct);
    }

    @Test
    void findAllProducts() {
        this.productRepository.createProduct(new Product(PRODUCT_NAME_2, DESCRIPTION_2, PRICE_2, this.category, QUANTITY ));
        List<Product> allProducts = this.productRepository.findAllProducts();
        assertTrue( allProducts.size() > 1);
    }

    @Test
    void findProductByTitle() {
        Optional<Product> productByTitle = this.productRepository.findProductByTitle(PRODUCT_NAME);

        assertNotNull(productByTitle);
    }


}