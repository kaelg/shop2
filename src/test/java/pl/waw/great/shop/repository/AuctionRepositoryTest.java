package pl.waw.great.shop.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.waw.great.shop.model.Auction;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuctionRepositoryTest {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private ProductRepository productRepository;

    private static final String PRODUCT_NAME = "iPhone 14";

    private Auction auction;

    @BeforeEach
    void setUp() {
        this.auction = new Auction();
        auction.setTitle(PRODUCT_NAME);
        this.productRepository.createProduct(auction);
    }


    @Test
    void findAuctionByTitle() {
        Optional<Auction> auctionByTitle = this.auctionRepository.findAuctionByTitle(PRODUCT_NAME);
        assertTrue(auctionByTitle.isPresent());
    }

    @Test
    void findAuctionById() {
        Auction auctionById = this.auctionRepository.findAuctionById(this.auction.getId());
        assertEquals(PRODUCT_NAME, auctionById.getTitle());

    }
}