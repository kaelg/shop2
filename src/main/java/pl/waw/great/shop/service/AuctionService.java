package pl.waw.great.shop.service;

import org.quartz.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.exception.InvalidBidAmountException;
import pl.waw.great.shop.exception.ProductWithGivenTitleNotExistsException;
import pl.waw.great.shop.exception.UserWithGivenNameNotExistsException;
import pl.waw.great.shop.model.*;
import pl.waw.great.shop.model.dto.AuctionDto;
import pl.waw.great.shop.model.dto.ProductDTO;
import pl.waw.great.shop.model.mapper.AuctionMapper;
import pl.waw.great.shop.model.mapper.BidMapper;
import pl.waw.great.shop.model.mapper.ProductMapper;
import pl.waw.great.shop.repository.*;
import pl.waw.great.shop.scheduler.EndAuctionJob;
import pl.waw.great.shop.scheduler.EndAuctionJobService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Service
public class AuctionService {

    private final AuctionMapper auctionMapper;

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    private final BidRepository bidRepository;


    private final AuctionRepository auctionRepository;

    private final EndAuctionJobService endAuctionJobService;

    private final OrderService orderService;


    public AuctionService(AuctionMapper auctionMapper, ProductRepository productRepository, ProductMapper productMapper, CategoryRepository categoryRepository, UserRepository userRepository, BidRepository bidRepository, BidMapper bidMapper, AuctionRepository auctionRepository, EndAuctionJobService endAuctionJobService, OrderService orderService) {
        this.auctionMapper = auctionMapper;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.endAuctionJobService = endAuctionJobService;
        this.orderService = orderService;
    }

    public ProductDTO create(ProductDTO productDTO) throws SchedulerException {
        Auction auction = auctionMapper.dtoToAuction(productDTO);
        auction.setStart(LocalDateTime.now());
        auction.setEnds(getAuctionEndDate(auction));
        auction.addBid(this.createInitBid(auction));

        Category category = this.categoryRepository.findCategoryByName(productDTO.getCategoryName());
        Product createdProduct = this.productRepository.createProduct(auction);
        this.categoryRepository.addProductToCategory(createdProduct, category);
        ProductDTO createdDto = productMapper.productToDto(createdProduct);
        createdDto.setAuctionType(auction.getAuctionType());
        createdDto.setCategoryName(CategoryType.valueOf(category.getName()));

        this.scheduleAuctionEnd(auction);
        return createdDto;
    }

    public AuctionDto get(String productTitle) {
        Auction auction = this.auctionRepository.findAuctionByTitle(productTitle)
                .orElseThrow(() -> new ProductWithGivenTitleNotExistsException(productTitle));
        return auctionMapper.auctionToDto(auction);
    }

    public AuctionDto bid(String productTitle, BigDecimal amount) {
        User user = this.getAuthenticatedUser();
        Auction auction = this.auctionRepository.findAuctionByTitle(productTitle)
                .orElseThrow(() -> new ProductWithGivenTitleNotExistsException(productTitle));
        List<Bid> bids = auction.getBids();

        if (!isBidAmountValid(bids, amount)) {
            throw new InvalidBidAmountException();
        }

        Bid bid = Bid.builder()
                .amount(amount)
                .user(user)
                .auction(auction)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        auction.addBid(bid);

        return auctionMapper.auctionToDto(this.auctionRepository.update(auction));
    }

    @Transactional
    public boolean endAuction(Long auctionId) throws SchedulerException {

        Auction auctionById = this.auctionRepository.findAuctionById(auctionId);
        if (auctionById.getBids().size() == 1) {
            this.extendAuction(auctionById);
        }

        User winner = this.getAuctionWinner(auctionById);
        BigDecimal finalPrice = auctionById.getBids().get(auctionById.getBids().size() - 1).getAmount();
        auctionById.updatePrice(finalPrice);
        this.orderService.createAuctionWinnerOrder(auctionById, winner);
        return true;
    }


    private void extendAuction(Auction auction) throws SchedulerException {
        auction.setEnds(auction.getEnds().plusDays(7));
        this.scheduleAuctionEnd(auction);
    }

    private void scheduleAuctionEnd(Auction auction) throws SchedulerException {

        this.endAuctionJobService.schedule(EndAuctionJob.class, new Date(System.currentTimeMillis() + 60000L), auction.getId());
    }

    private User getAuctionWinner(Auction auction) {
        List<Bid> bids = this.bidRepository.getProductBids(auction.getTitle());
        return bids.get(bids.size() - 1).getUser();
    }

    private LocalDateTime getAuctionEndDate(Auction auction) {
        if (auction.getAuctionType().equals(AuctionType.LICYTACJA_7_DNI)) {
            return LocalDateTime.now().plusSeconds(30);
        }
        return LocalDateTime.now().plusSeconds(60);
    }

    private boolean isBidAmountValid(List<Bid> bids, BigDecimal amount) {
        if (bids.isEmpty()) {
            return true;
        }

        return bids.get(bids.size() - 1).getAmount().compareTo(amount) < 0;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return this.userRepository.findUserByTitle(authentication.getName())
                .orElseThrow(() -> new UserWithGivenNameNotExistsException(authentication.getName()));
    }

    private Bid createInitBid(Auction auction) {
        return Bid.builder()
                .amount(auction.getPrice())
                .auction(auction)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();
    }

}
