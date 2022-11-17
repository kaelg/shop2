package pl.waw.great.shop.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartLineItemDto {

    private String productTitle;

    private Long quantity;

    private BigDecimal productAmount;

    private int cartIndex;

}