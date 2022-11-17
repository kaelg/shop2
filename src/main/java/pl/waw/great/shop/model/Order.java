package pl.waw.great.shop.model;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "order_")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue
    private UUID id;

    private BigDecimal totalPrice;
    @ManyToOne
    private User user;

    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    @JoinColumn(name = "order_id")
    private List<OrderLineItem> orderLineItemList;

    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(totalPrice, order.totalPrice) && Objects.equals(user, order.user) && Objects.equals(orderLineItemList, order.orderLineItemList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPrice, user, orderLineItemList);
    }
}
