package pl.waw.great.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.AuctionDto;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.mapper.AuctionMapper;
import pl.waw.great.shop.model.mapper.BidMapper;
import pl.waw.great.shop.model.mapper.ProductMapper;
import pl.waw.great.shop.repository.*;
import pl.waw.great.shop.scheduler.EndAuctionJobService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class AuctionServiceTest {

    private static final String PRODUCT_TITLE = "iPhone 15";

    private static final String DESCRIPTION = "The iPhone is a line of smartphones by Apple";

    private static final BigDecimal PRICE = BigDecimal.valueOf(999);

    private static final Long QUANTITY = 5L;

    private Product product;

    private ProductDTO productDto;

    private Auction auction;

    private Category category;

    private User user;

    private List<Bid> bidList = new ArrayList<>();

    private Bid bid;

    private AuctionRepository auctionRepository = mock(AuctionRepository.class);

    private CategoryRepository categoryRepository = mock(CategoryRepository.class);

    private ProductRepository productRepository = mock(ProductRepository.class);

    private BidRepository bidRepository = mock(BidRepository.class);

    private OrderService orderService = mock(OrderService.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private EndAuctionJobService endAuctionJobService = mock(EndAuctionJobService.class);

    @Spy
    AuctionMapper auctionMapper = Mappers.getMapper(AuctionMapper.class);

    @Spy
    ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    @Spy
    BidMapper bidMapper = Mappers.getMapper(BidMapper.class);

    @InjectMocks
    private AuctionService auctionService;


    @BeforeEach
    void setUp() {
        this.user = new User("Mikolaj");

        this.auction = new Auction();
        this.auction.setTitle(PRODUCT_TITLE);
        this.auction.setStart(LocalDateTime.now());
        this.auction.setEnds(LocalDateTime.now());
        this.bid = Bid.builder()
                .amount(BigDecimal.valueOf(100))
                .user(this.user)
                .auction(this.auction)
                .updated(LocalDateTime.now())
                .created(LocalDateTime.now())
                .build();

        this.bidList.add(bid);
        this.auction.setBids(this.bidList);
        this.category = Category.builder().name("INNE").build();
        this.product = Product.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .category(this.category)
                .quantity(QUANTITY)
                .build();

        this.productDto = ProductDTO.builder()
                .title(PRODUCT_TITLE)
                .description(DESCRIPTION)
                .price(PRICE)
                .categoryName(CategoryType.EDUKACJA)
                .quantity(QUANTITY)
                .auctionType(AuctionType.KUP_TERAZ)
                .build();

        ReflectionTestUtils.setField(
                auctionMapper,
                "bidMapper",
                bidMapper
        );
    }


    @Test
    @WithMockUser(roles = "USER")
    void create() throws SchedulerException {
        when(this.categoryRepository.findCategoryByName(any())).thenReturn(this.category);
        when(this.productRepository.createProduct(any())).thenReturn(this.product);
        when(this.userRepository.findUserByTitle(any())).thenReturn(Optional.of(this.user));


        ProductDTO savedDto = this.auctionService.create(this.productDto);
        assertEquals(savedDto, this.productDto);
    }

    @Test
    void get() {
        when(this.auctionRepository.findAuctionByTitle(any())).thenReturn(Optional.of(this.auction));

        AuctionDto auctionDto = this.auctionService.get(PRODUCT_TITLE);

        assertEquals(auctionDto.getTitle(), PRODUCT_TITLE);
    }


    @Test
    @WithMockUser(roles = "USER")
    void bid() {
        when(this.auctionRepository.findAuctionByTitle(any())).thenReturn(Optional.of(this.auction));
        when(this.userRepository.findUserByTitle(any())).thenReturn(Optional.of(this.user));
        when(this.auctionRepository.update(auction)).thenReturn(this.auction);
        AuctionDto bidedAuction = this.auctionService.bid(PRODUCT_TITLE, BigDecimal.valueOf(1000));

        assertEquals(2, bidedAuction.getBids().size());

    }

    @Test
    void endAuction() throws SchedulerException {
        when(this.auctionRepository.findAuctionById(any())).thenReturn(this.auction);
        when(this.bidRepository.getProductBids(any())).thenReturn(this.bidList);

        boolean isAuctionEnded = this.auctionService.endAuction(1L);

        assertTrue(isAuctionEnded);
    }
}