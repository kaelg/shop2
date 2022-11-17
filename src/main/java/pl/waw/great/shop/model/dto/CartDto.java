package pl.waw.great.shop.model.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDto {

    private String userName;

    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private List<CartLineItemDto> cartLineItemList;

}
