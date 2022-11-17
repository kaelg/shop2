package pl.waw.great.shop.model.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {

    private String id;
    private List<OrderLineDto> orderLineItemList;

    private String userName;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal totalPrice;


}
