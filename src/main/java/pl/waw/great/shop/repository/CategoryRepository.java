package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.CategoryWithGivenNameNotExistsException;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class CategoryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Category addProductToCategory(Product product, Category category) {
        category.addProduct(product);
        category.setCreated(LocalDateTime.now());
        category.setUpdated(LocalDateTime.now());
        this.entityManager.persist(category);

        return category;
    }

    public List<Category> findAll() {
        return this.entityManager.createQuery("SELECT c FROM  Category c", Category.class).getResultList();

    }

    @Transactional
    public Category findCategoryByName(CategoryType categoryType) {
        TypedQuery<Category> query = this.entityManager.createQuery("SELECT c FROM Category c left join fetch c.productList p WHERE c.name=:name", Category.class);
        query.setParameter("name", categoryType.name());
        return query.getResultStream()
                .findFirst()
                .orElseThrow(() -> new CategoryWithGivenNameNotExistsException(categoryType.name()));
    }

    @Transactional
    public int countProductsFromCategory(CategoryType categoryType) {
        Category category = this.findCategoryByName(categoryType);

        return category.getProductList().size();
    }

    @Transactional
    public List<Product> getProductsFromCategory(CategoryType categoryType) {
        Category category = this.findCategoryByName(categoryType);

        return category.getProductList();
    }

    @Transactional
    public BigDecimal getSumByCategory(CategoryType categoryType) {
        Category category = this.findCategoryByName(categoryType);
        return category.getProductList().stream().map(Product::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
