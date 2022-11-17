package pl.waw.great.shop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    private LocalDateTime created;

    private LocalDateTime updated;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.REMOVE)
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Bid> bids;


    private Long coins;

    public User() {
    }

    public User(String name) {
        this.name = name;
        this.created = LocalDateTime.now();
        this.updated = LocalDateTime.now();
        this.coins = 0L;
    }
    public void addCoins(Long earnedCoins) {
        this.coins += earnedCoins;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
