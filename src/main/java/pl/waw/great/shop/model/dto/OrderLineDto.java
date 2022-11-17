package pl.waw.great.shop.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderLineDto {

    private String productTitle;

    private Long quantity;

}
