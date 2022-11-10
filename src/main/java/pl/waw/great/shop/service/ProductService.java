package pl.waw.great.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.ProductWithGivenTitleExists;
import pl.waw.great.shop.exception.ProductWithGivenTitleNotExistsException;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.model.dto.CommentDto;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.dto.ProductListElementDto;
import pl.waw.great.shop.model.mapper.CommentMapper;
import pl.waw.great.shop.model.mapper.ProductListElementMapper;
import pl.waw.great.shop.model.mapper.ProductMapper;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductMapper productMapper;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private final CommentMapper commentMapper;

    private final ProductListElementMapper productListElementMapper;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, CommentMapper commentMapper, ProductListElementMapper productListElementMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.commentMapper = commentMapper;
        this.productListElementMapper = productListElementMapper;
    }

    public ProductDTO createProduct(ProductDTO productDTO) {
        this.productRepository.findProductByTitle(productDTO.getTitle())
                .ifPresent(a -> {
                    throw new ProductWithGivenTitleExists(productDTO.getTitle());
                });

        Category category = this.categoryRepository.findCategoryByName(productDTO.getCategoryName());
        Product createdProduct = this.productRepository.createProduct(productMapper.dtoToProduct(productDTO));
        this.categoryRepository.addProductToCategory(createdProduct, category);
        ProductDTO createdDto = productMapper.productToDto(createdProduct);
        createdDto.setCategoryName(CategoryType.valueOf(category.getName()));

        return createdDto;
    }

    public ProductDTO updateProduct(Long id, ProductDTO newProduct) {
        newProduct.setId(id);
        this.getProduct(newProduct.getId());
        this.productRepository.findProductByTitle(newProduct.getTitle())
                .ifPresent(a -> {
                    throw new ProductWithGivenTitleExists(newProduct.getTitle());
                });

        Category category = this.categoryRepository.findCategoryByName(newProduct.getCategoryName());
        Product updatedProduct = this.productRepository.updateProduct(productMapper.dtoToProduct(newProduct));
        this.categoryRepository.addProductToCategory(updatedProduct, category);
        return productMapper.productToDto(updatedProduct);
    }

    public ProductDTO getProduct(Long id) {
        Product product = this.productRepository.getProduct(id);
        return productMapper.productToDto(product);
    }

    public ProductDTO getProductByTitle(String title) {
        Product product = this.productRepository.findProductByTitle(title)
                .orElseThrow(() -> new ProductWithGivenTitleNotExistsException(title));


        List<CommentDto> productCommentsDtos = product.getCommentsList()
                .stream().map(commentMapper::commentToDto).collect(Collectors.toList());

        ProductDTO productDTO = productMapper.productToDto(product);
        productDTO.setCommentsList(productCommentsDtos);

        return productDTO;
    }

    public List<ProductListElementDto> findAllProducts(Pageable pageable) {
        List<ProductListElementDto> productListElementDtos = this.productRepository.findAllProducts()
                .stream()
                .map(productListElementMapper::productToDto)
                .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productListElementDtos.size());

        return new PageImpl<>(productListElementDtos.subList(start, end), pageable, productListElementDtos.size()).toList();
    }

    public List<ProductDTO> getProductsByCategory(CategoryType categoryType) {
        List<Product> productList = this.categoryRepository.getProductsFromCategory(categoryType);

        return productList.stream().map(productMapper::productToDto).collect(Collectors.toList());

    }

    public boolean deleteProduct(Long id) {
        return this.productRepository.deleteProduct(id);
    }

    public boolean deleteAllProducts() {
        return this.productRepository.deleteAll();
    }

}
