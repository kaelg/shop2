package pl.waw.great.shop.service;

import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.method.P;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.ProductWithGivenTitleExists;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.dto.ProductListElementDto;
import pl.waw.great.shop.model.mapper.ProductListElementMapper;
import pl.waw.great.shop.model.mapper.ProductMapper;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ProductServiceImplTest {
    private static final String PRODUCT_TITLE = "iPhone 14";
    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";
    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    public static final String UPDATED_TITLE = "Samsung S22";

    private static final String UPDATED_DESCRIPTION = "The S is a line of smartphones by Samsung";

    public static final BigDecimal UPDATED_PRICE = BigDecimal.valueOf(1200);

    private static final CategoryType CATEGORY_NAME = CategoryType.ELEKTRONIKA;

    private static final Category CATEGORY = Category.builder().name(CATEGORY_NAME.toString()).build();


    private static final Long QUANTITY = 5L;


    private Category category;
    ProductRepository productRepository = mock(ProductRepository.class);

    CategoryRepository categoryRepository = mock(CategoryRepository.class);

    @Spy
    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Spy
    ProductListElementMapper productListElementMapper = Mappers.getMapper(ProductListElementMapper.class);
    @InjectMocks
    ProductService productService;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private Product product;

    @BeforeEach
    void setUp() {
        this.product = Product.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();
        this.category = Category.builder().name(CATEGORY_NAME.toString()).build();
        this.product.setCategory(category);
    }

    @AfterEach()
    void tearDown() throws Exception {
    }


    @Test
    void createProduct() {
        when(this.productRepository.createProduct(any())).thenReturn(this.product);
        when(this.categoryRepository.findCategoryByName(any())).thenReturn(this.category);
        ProductDTO createdProduct = this.productService.createProduct(ProductDTO.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .categoryName(CATEGORY_NAME)
                .quantity(QUANTITY)
                .auctionType(AuctionType.KUP_TERAZ)
                .build());
        assertEquals(PRODUCT_TITLE, createdProduct.getTitle());
        assertEquals(DESCRIPTION, createdProduct.getDescription());
        assertEquals(PRICE, createdProduct.getPrice());
    }

    @Test
    void createProductWithDuplicateTitleShouldThrowException() {
        ProductDTO dto = ProductDTO.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .categoryName(CATEGORY_NAME)
                .quantity(QUANTITY)
                .auctionType(AuctionType.KUP_TERAZ)
                .build();
        when(this.productRepository.findProductByTitle(anyString())).thenReturn(Optional.of(this.product));

        Assertions.assertThrows(ProductWithGivenTitleExists.class, () -> {
            this.productService.createProduct(dto);
        });
    }


    @Test
    void updateProduct() {
        ProductDTO dtoToUpdate = ProductDTO.builder()
                .title(UPDATED_TITLE)
                .description(UPDATED_DESCRIPTION)
                .price(UPDATED_PRICE)
                .categoryName(CATEGORY_NAME)
                .quantity(QUANTITY)
                .auctionType(AuctionType.KUP_TERAZ)
                .build();

        Product updated = Product.builder()
                .title(UPDATED_TITLE)
                .description(UPDATED_DESCRIPTION)
                .price(UPDATED_PRICE)
                .category(this.category)
                .quantity(QUANTITY).build();

        when(this.productRepository.getProduct(anyLong())).thenReturn(this.product);
        when(this.productRepository.updateProduct(any())).thenReturn(updated);
        ProductDTO saved = this.productService.updateProduct(1L, dtoToUpdate);
        assertNotNull(updated);
        assertEquals(dtoToUpdate.getTitle(), updated.getTitle());
    }

    @Test
    void updateToDuplicateTitleShouldThrowException() {
        ProductDTO dto = ProductDTO.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .categoryName(CATEGORY_NAME)
                .quantity(QUANTITY)
                .auctionType(AuctionType.KUP_TERAZ)
                .build();

        when(this.productRepository.findProductByTitle(anyString())).thenReturn(Optional.of(this.product));
        Assertions.assertThrows(ProductWithGivenTitleExists.class, () -> {
            this.productService.updateProduct(1L, dto);
        });
    }

    @Test
    void getProduct() {
        when(this.productRepository.getProduct(anyLong())).thenReturn(this.product);
        ProductDTO productDTO = this.productService.getProduct(1L);

        assertEquals(productDTO.getTitle(), this.product.getTitle());
    }

    @Test
    void findAllProducts() {
        when(this.productRepository.findAllProducts()).thenReturn(Collections.singletonList(this.product));
        List<ProductListElementDto> allProducts = this.productService.findAllProducts(PageRequest.of(0, 50));
        assertEquals(1, allProducts.size());
    }

    @Test
    void deleteProduct() {
        when(this.productRepository.deleteProduct(anyLong())).thenReturn(true);
        boolean isDeleted = this.productService.deleteProduct(1L);

        assertTrue(isDeleted);
    }

    @Test
    void deleteAllProducts() {
        when(this.productRepository.deleteAll()).thenReturn(true);
        boolean isDeleted = this.productService.deleteAllProducts();

        assertTrue(isDeleted);
    }
}