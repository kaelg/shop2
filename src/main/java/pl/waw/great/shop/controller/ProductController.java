package pl.waw.great.shop.controller;

import org.quartz.SchedulerException;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.dto.*;
import pl.waw.great.shop.repository.CategoryRepository;
import pl.waw.great.shop.service.AuctionService;
import pl.waw.great.shop.service.CartService;
import pl.waw.great.shop.service.CommentService;
import pl.waw.great.shop.service.ProductService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    private final CommentService commentService;

    private final CartService cartService;

    private final AuctionService auctionService;

    public ProductController(ProductService productService, CommentService commentService, CartService cartService, AuctionService auctionService) {
        this.productService = productService;
        this.commentService = commentService;
        this.cartService = cartService;
        this.auctionService = auctionService;
    }

    @PostMapping
    public ProductDTO createProduct(@Valid @RequestBody ProductDTO productDTO) throws SchedulerException {
        if (productDTO.getQuantity() == null) {
            productDTO.setQuantity(1L);
        }

        if (!productDTO.getAuctionType().equals(AuctionType.KUP_TERAZ)) {
            return this.auctionService.create(productDTO);
        }

        return this.productService.createProduct(productDTO);
    }

    @PutMapping("/{id}")
    public ProductDTO updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        return this.productService.updateProduct(id, productDTO);
    }


    @GetMapping("/{title}")
    public ProductDTO getProductByTitle(@PathVariable String title){
        return this.productService.getProductByTitle(title);
    }

    @PostMapping("/{productTitle}")
    public CommentDto addComment(@PathVariable String productTitle, @Valid @RequestBody CommentDto commentDto) {
        return this.commentService.createComment(productTitle, commentDto);
    }

    @DeleteMapping("/{productTitle}/{commentIndex}")
    public boolean deleteComment(@PathVariable String productTitle, @PathVariable int commentIndex) {
        return this.commentService.delete(productTitle, commentIndex);
    }

    @GetMapping
    public List<ProductListElementDto> findAllProducts(@RequestParam int page, @RequestParam int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return this.productService.findAllProducts(pageRequest);
    }

    @DeleteMapping("/{id}")
    public boolean deleteProductById(@PathVariable Long id) {
        return this.productService.deleteProduct(id);
    }

    @PostMapping("/{productTitle}/{amount}/addToCart")
    public CartDto addToCart(@PathVariable String productTitle, @PathVariable Long amount) {
        return this.cartService.create(productTitle, amount);
    }

    @GetMapping("/byCategory/{categoryName}")
    public List<ProductDTO> byCategory(@PathVariable String categoryName) {
        CategoryType categoryType = CategoryType.valueOf(categoryName);
        return this.productService.getProductsByCategory(categoryType);
    }

}
