package pl.waw.great.shop.model.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BidDto {

    private BigDecimal amount;

    private String userName;

    private String productTitle;

}
