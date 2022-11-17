package pl.waw.great.shop.model;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartLineItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Cart cart;

    private LocalDateTime created;

    private LocalDateTime updated;

    private Long quantity;

    @Builder.Default
    private BigDecimal productAmount = BigDecimal.ZERO;

    private int cartIndex;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartLineItem)) return false;
        CartLineItem that = (CartLineItem) o;
        return Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product);
    }
}
