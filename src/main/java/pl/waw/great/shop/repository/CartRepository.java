package pl.waw.great.shop.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.model.Cart;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;


@Repository
public class CartRepository {

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public Cart create(Cart cart) {
        cart.setCreated(LocalDateTime.now());
        cart.setUpdated(LocalDateTime.now());
        this.entityManager.persist(cart);
        return cart;
    }

    @Transactional
    public Cart update(Cart cart) {
        cart.setUpdated(LocalDateTime.now());
        this.entityManager.merge(cart);
        return this.findCartById(cart.getId());
    }

    @Transactional
    public Cart findCartByUserId(Long id) {
        TypedQuery<Cart> query = this.entityManager.createQuery("SELECT c FROM Cart c inner join fetch c.user u WHERE u.id=:id", Cart.class);
        query.setParameter("id", id);
        return query.getResultStream()
                .findFirst()
                .orElse(new Cart());

    }

    public Cart findCartById(Long id) {
        return this.entityManager.find(Cart.class, id);
    }

    @Transactional
    public boolean delete(Long id) {
        Cart cartById = this.findCartById(id);
        this.entityManager.remove(cartById);
        return true;
    }

    @Transactional
    public void deleteAll() {
        this.entityManager.createQuery("delete from Cart").executeUpdate();
        this.entityManager.flush();
    }
}
