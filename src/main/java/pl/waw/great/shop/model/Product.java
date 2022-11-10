package pl.waw.great.shop.model;


import pl.waw.great.shop.config.AuctionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "PRODUCT")
@Inheritance(strategy = InheritanceType.JOINED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private LocalDateTime created;
    private LocalDateTime updated;

    private Long quantity;
    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<OrderLineItem> orderLineItem;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<CartLineItem> cartLineItemList;

    private AuctionType auctionType;
    public Product() {
    }

    public Product(String title, String description, BigDecimal price, Category category, Long quantity) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCreated() {
        this.created = LocalDateTime.now();
    }

    public void setUpdated() {
        this.updated = LocalDateTime.now();
    }
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int addComment(Comment comment) {
        this.commentsList.add(comment);
        comment.setProduct(this);

        return this.commentsList.indexOf(comment);
    }

    public List<Comment> getCommentsList() {
        return commentsList;
    }

    public void setCommentsList(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }


    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
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
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Objects.equals(title, product.title) && Objects.equals(description, product.description) && Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, price);
    }
}
