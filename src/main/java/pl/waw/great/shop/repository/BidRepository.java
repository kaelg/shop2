package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import pl.waw.great.shop.model.Bid;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class BidRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Bid> getProductBids(String productTitle) {
        TypedQuery<Bid> query = this.entityManager.createQuery(
                "SELECT b FROM Bid b join fetch b.auction a WHERE a.title=:title", Bid.class);
        query.setParameter("title", productTitle);
        return query.getResultList();
    }

}
