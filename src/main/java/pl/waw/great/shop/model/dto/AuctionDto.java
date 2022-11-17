package pl.waw.great.shop.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class AuctionDto {

    private String starts;

    private String ends;

    private List<BidDto> bids;

    private String title;

    private BigDecimal price;

    private Long quantity;

    private String description;

}
