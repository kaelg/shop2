package pl.waw.great.shop.configuration;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;

@Component
@Profile("dev")
public class TestDataLoader {

    private final ProductRepository productRepository;
    private final RandomDataGenerator randomDataGenerator;

    public TestDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
        this.randomDataGenerator = new RandomDataGenerator();
        generateTestProducts();
    }

    public void generateTestProducts() {
        List<Product> productList = IntStream.range(0, 1000)
                .mapToObj(product ->
                        Product.builder()
                                .title(randomAlphanumeric(randomDataGenerator.nextInt(2, 10)))
                                .description(randomAlphanumeric(randomDataGenerator.nextInt(2, 10)))
                                .price(BigDecimal.valueOf(Long.parseLong(randomNumeric(1, 5))))
                                .category(Category.builder().name(CategoryType.EDUKACJA.toString()).build())
                                .quantity(Long.parseLong(randomNumeric(1, 3))).build()).collect(Collectors.toList());

        productList.forEach(this.productRepository::createProduct);
    }
}