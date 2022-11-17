package pl.waw.great.shop;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.mapper.ProductMapper;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.repository.ProductRepository;
import pl.waw.great.shop.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Profile("local")
@RestController
@RequestMapping("/test")
public class TestController {
    private final ProductService productService;

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private int counter = 0;

    public TestController(ProductService productService, CategoryRepository categoryRepository, ProductRepository productRepository, ProductMapper productMapper) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @PostMapping
    public void createProduct(@RequestParam int count) {
        Random random = new Random();
        for (int i = counter; i < counter + count; i++) {

            int index = random.nextInt(CategoryType.values().length - 1);

            ProductDTO productDTO = ProductDTO.builder()
                    .title("product" + i)
                    .description("test description")
                    .price(BigDecimal.valueOf(Long.parseLong(randomNumeric(1,5))))
                    .quantity(2L)
                    .auctionType(AuctionType.KUP_TERAZ)
                    .build();
            this.productService.createProduct(productDTO);
        }
        counter += count;

    }
    @DeleteMapping
    public boolean deleteAllProducts() {
        return this.productService.deleteAllProducts();
    }

    @GetMapping("/count")
    public int countProductsFromCategory(@RequestParam CategoryType categoryType) {
        return this.categoryRepository.countProductsFromCategory(categoryType);
    }

    @GetMapping("/list")
    public List<ProductDTO> getProductsFromCategory(@RequestParam CategoryType categoryType) {
        List<Product> productList = this.categoryRepository.getProductsFromCategory(categoryType);

        return productList.stream().map(productMapper::productToDto).collect(Collectors.toList());
    }
    @GetMapping("/categorySum")
    public BigDecimal getSumByCategory(@RequestParam CategoryType categoryType) {

        return this.categoryRepository.getSumByCategory(categoryType);
    }
    @GetMapping("/allSum")
    public BigDecimal getAllSum() {
        return this.productRepository.getAllSum();
    }

    @GetMapping("/{id}")
    public ProductDTO getProductById(@PathVariable Long id) {
        return this.productService.getProduct(id);
    }

}
