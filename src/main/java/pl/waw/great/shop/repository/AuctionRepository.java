package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.exception.ProductWithGivenIdNotExistsException;
import pl.waw.great.shop.model.Auction;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class AuctionRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Optional<Auction> findAuctionByTitle(String title) {
        TypedQuery<Auction> query = this.entityManager.createQuery("SELECT a FROM Auction a left join fetch a.bids WHERE a.title=:title", Auction.class);
        query.setParameter("title", title);
        return query.getResultStream()
                .findFirst();
    }

    public Auction findAuctionById(Long id) {
        Auction product = this.entityManager.find(Auction.class, id);
        if (product == null) {
            throw new ProductWithGivenIdNotExistsException(id);
        }
        return product;
    }

    @Transactional
    public Auction update(Auction auction) {
        auction.setUpdated(LocalDateTime.now());
        this.entityManager.merge(auction);
        return this.findAuctionByTitle(auction.getTitle()).get();
    }


}
