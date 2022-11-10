package pl.waw.great.shop.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import pl.waw.great.shop.config.CategoryType;
import pl.waw.great.shop.model.Category;
import pl.waw.great.shop.model.Product;
import pl.waw.great.shop.model.dto.ProductDTO;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category", target = "categoryName", qualifiedByName = "getCategoryType")
    ProductDTO productToDto(Product product);
    Product dtoToProduct(ProductDTO productDTO);

    @Named("getCategoryType")
    default CategoryType getCategoryType(Category category) {
        return CategoryType.valueOf(category.getName());
    }


}
