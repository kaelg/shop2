package pl.waw.great.shop.model;


import lombok.*;
import lombok.experimental.SuperBuilder;
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
@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
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
    @Builder.Default
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<CartLineItem> cartLineItemList;

    private AuctionType auctionType;

    public int addComment(Comment comment) {
        this.commentsList.add(comment);
        comment.setProduct(this);

        return this.commentsList.indexOf(comment);
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
