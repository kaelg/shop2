package pl.waw.great.shop.model;

import pl.waw.great.shop.config.AuctionType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "AUCTION")
public class Auction extends Product {

    private LocalDateTime starts;
    private LocalDateTime ends;
    private LocalDateTime created;
    private LocalDateTime updated;

    private AuctionType auctionType;


    @OneToMany(mappedBy = "auction", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    private List<Bid> bids = new ArrayList<>();

    public void addBid(Bid bid) {
        bids.add(bid);
    }

    public LocalDateTime getStarts() {
        return starts;
    }

    public void setStart(LocalDateTime starts) {
        this.starts = starts;
    }

    public LocalDateTime getEnds() {
        return ends;
    }

    public void setEnds(LocalDateTime ends) {
        this.ends = ends;
    }

    @Override
    public LocalDateTime getCreated() {
        return created;
    }

    @Override
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public LocalDateTime getUpdated() {
        return updated;
    }

    @Override
    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    @Override
    public AuctionType getAuctionType() {
        return auctionType;
    }

    @Override
    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
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
        return Objects.equals(starts, auction.starts) && Objects.equals(ends, auction.ends) && auctionType == auction.auctionType && Objects.equals(bids, auction.bids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), starts, ends, auctionType, bids);
    }
}
