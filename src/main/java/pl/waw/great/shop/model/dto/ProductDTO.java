package pl.waw.great.shop.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;


public class ProductDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "{titleNotBlank}")
    private String title;
    @NotBlank(message = "{descriptionNotBlank}")
    private String description;
    @NotNull
    @Min(value = 1, message = "{priceMinValue}")
    @Digits(integer = 6, fraction = 2, message = "{invalidPriceFormat}")
    private BigDecimal price;
    @NotNull
    private CategoryType categoryName;

    private Long quantity;

    private AuctionType auctionType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    List<CommentDto> commentsList = new ArrayList<>();

    public ProductDTO() {
    }

    public ProductDTO(String title, String description, BigDecimal price, CategoryType categoryName, Long quantity, AuctionType auctionType) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.categoryName = categoryName;
        this.quantity = quantity;
        this.auctionType = auctionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public CategoryType getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(CategoryType categoryName) {
        this.categoryName = categoryName;
    }

    public List<CommentDto> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<CommentDto> commentsList) {
        this.commentsList = commentsList;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDTO)) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, price);
    }
}
