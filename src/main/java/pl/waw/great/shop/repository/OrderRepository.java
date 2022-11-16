package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.model.Order;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.UUID;

@Repository
public class OrderRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Order create(Order order) {
        this.entityManager.persist(order);

        return order;
    }

    public List<Order> getOrdersByUserId(Long id) {
        TypedQuery<Order> query = this.entityManager.createQuery("SELECT o FROM Order o inner join fetch o.user u WHERE u.id=:id", Order.class);
        query.setParameter("id", id);

        return query.getResultList();
    }

    public Order getOrderById(String id) {
        UUID uuid = UUID.fromString(id);
        TypedQuery<Order> query = this.entityManager.createQuery("SELECT o FROM Order o WHERE o.id=:id", Order.class);
        query.setParameter("id", uuid);

        return query.getSingleResult();
    }

    @Transactional
    public boolean deleteAll() {
        this.entityManager.createQuery("delete from OrderLineItem cascade").executeUpdate();
        this.entityManager.createQuery("delete from Order cascade").executeUpdate();
        this.entityManager.flush();
        return true;
    }
}
