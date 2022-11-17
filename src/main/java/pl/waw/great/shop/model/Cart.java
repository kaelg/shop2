package pl.waw.great.shop.model;

import lombok.*;
import pl.waw.great.shop.exception.InvalidCartItemIndexException;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartLineItem> cartLineItemList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private LocalDateTime created;

    private LocalDateTime updated;
    public void addCartLineItem(CartLineItem cartLineItem) {
        if (cartLineItemList.contains(cartLineItem)) {
            int index = this.cartLineItemList.indexOf(cartLineItem);
            CartLineItem savedCartLineItem = this.cartLineItemList.get(index);
            savedCartLineItem.setQuantity(savedCartLineItem.getQuantity() + cartLineItem.getQuantity());
            savedCartLineItem.setProductAmount(savedCartLineItem.getProductAmount().add(cartLineItem.getProductAmount()));
            this.cartLineItemList.set(index, savedCartLineItem);
            this.addToTotalAmount(cartLineItem.getProductAmount());

        } else {
            this.cartLineItemList.add(cartLineItem);
            this.addToTotalAmount(cartLineItem.getProductAmount());
        }
    }

    public Long getProductInCartAmount(CartLineItem cartLineItem) {
        if (cartLineItemList.contains(cartLineItem)) {
            int index = this.cartLineItemList.indexOf(cartLineItem);
            CartLineItem savedCartLineItem = this.cartLineItemList.get(index);
            return savedCartLineItem.getQuantity();
        }

        return 0L;
    }

    public void addToTotalAmount(BigDecimal productAmount) {
        this.totalAmount = this.totalAmount.add(productAmount);
    }

    public void subtractFromTotalAmount(BigDecimal productAmount) {
        this.totalAmount = this.totalAmount.subtract(productAmount);
    }

    public void removeItem(int index) {
        CartLineItem cartItem = this.cartLineItemList.stream()
                .filter(item -> item.getCartIndex() == index)
                .findFirst()
                .orElseThrow(InvalidCartItemIndexException::new);
        this.subtractFromTotalAmount(cartItem.getProductAmount());
        this.cartLineItemList.remove(cartItem);
    }

    public int getSize() {
        return this.cartLineItemList.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;
        Cart cart = (Cart) o;
        return Objects.equals(cartLineItemList, cart.cartLineItemList) && Objects.equals(user, cart.user) && Objects.equals(totalAmount, cart.totalAmount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartLineItemList, user, totalAmount);
    }
}
