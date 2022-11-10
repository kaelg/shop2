package pl.waw.great.shop.model.dto;

import pl.waw.great.shop.config.AuctionType;

import java.math.BigDecimal;

public class ProductListElementDto {
    private String img;
    private String title;

    private BigDecimal price;

    private AuctionType auctionType;

    public ProductListElementDto() {
    }

    public ProductListElementDto(String img, String title) {
        this.img = img;
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AuctionType getAuctionType() {
        return auctionType;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }
}
