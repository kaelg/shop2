package pl.waw.great.shop.controller;

import org.springframework.web.bind.annotation.*;
import pl.waw.great.shop.model.dto.AuctionDto;
import pl.waw.great.shop.service.AuctionService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/auction")
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/{productTitle}/{amount}")
    public AuctionDto bid(@PathVariable String productTitle, @PathVariable BigDecimal amount) {
        return this.auctionService.bid(productTitle, amount);
    }

    @GetMapping("/{productTitle}")
    public AuctionDto getAuction(@PathVariable String productTitle) {
        return this.auctionService.get(productTitle);
    }

}
