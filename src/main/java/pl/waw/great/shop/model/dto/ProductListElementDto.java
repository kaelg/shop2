package pl.waw.great.shop.model.dto;

import lombok.*;
import pl.waw.great.shop.config.AuctionType;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductListElementDto {
    private String img;
    private String title;

    private BigDecimal price;

    private AuctionType auctionType;

}
