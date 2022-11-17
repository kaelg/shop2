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
@Table(name = "AUCTION_")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Auction extends Product {

    private LocalDateTime start;
    private LocalDateTime ends;
    private LocalDateTime created;
    private LocalDateTime updated;

    private AuctionType auctionType;

    @OneToMany(mappedBy = "auction", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<Bid> bids = new ArrayList<>();


    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public void updatePrice(BigDecimal price) {
        super.setPrice(price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Auction)) return false;
        if (!super.equals(o)) return false;
        Auction auction = (Auction) o;
        return Objects.equals(start, auction.start) && Objects.equals(ends, auction.ends) && auctionType == auction.auctionType && Objects.equals(bids, auction.bids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), start, ends, auctionType, bids);
    }
}
