package pl.waw.great.shop.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.waw.great.shop.model.Bid;
import pl.waw.great.shop.model.dto.BidDto;

@Mapper(componentModel = "spring")
public interface BidMapper {

    @Mapping(source = "bid", target = "userName", qualifiedByName = "getUserName")
    @Mapping(source = "bid", target = "productTitle", qualifiedByName = "getProductTitle")
    BidDto bidToDto(Bid bid);

    @Named("getUserName")
    default String getUserName(Bid bid) {
        return bid.getUser().getName();
    }

    @Named("getProductTitle")
    default String getProductTitle(Bid bid) {
        return bid.getAuction().getTitle();
    }

}

