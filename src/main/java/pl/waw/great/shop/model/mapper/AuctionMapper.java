package pl.waw.great.shop.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.waw.great.shop.model.Auction;
import pl.waw.great.shop.model.dto.AuctionDto;
import pl.waw.great.shop.model.dto.ProductDTO;

import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring", uses = BidMapper.class)
public interface AuctionMapper {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Mapping(source = "auction", target = "starts", qualifiedByName = "getStartDateString")
    @Mapping(source = "auction", target = "ends", qualifiedByName = "getEndDateString" )
    AuctionDto auctionToDto(Auction auction);

    Auction dtoToAuction(ProductDTO productDTO);

    @Named("getStartDateString")
    default String getStartDateString(Auction auction) {
        return auction.getStart().format(formatter);
    }

    @Named("getEndDateString")
    default String getEndDateString(Auction auction) {
        return auction.getEnds().format(formatter);
    }
}
