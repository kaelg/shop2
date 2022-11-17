package pl.waw.great.shop.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import pl.waw.great.shop.config.AuctionType;
import pl.waw.great.shop.config.CategoryType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "{titleNotBlank}")
    private String title;
    @NotBlank(message = "{descriptionNotBlank}")
    private String description;
    @NotNull
    @Min(value = 1, message = "{priceMinValue}")
    @Digits(integer = 6, fraction = 2, message = "{invalidPriceFormat}")
    private BigDecimal price;
    @NotNull
    private CategoryType categoryName;

    private Long quantity;

    private AuctionType auctionType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Builder.Default
    List<CommentDto> commentsList = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductDTO)) return false;
        ProductDTO that = (ProductDTO) o;
        return Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(price, that.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, price);
    }
}
