package ci.yoru.hackathon.mappers;

import ci.yoru.hackathon.controllers.dto.ProductResponseDto;
import ci.yoru.hackathon.entities.Product;
import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ProductMapper {
    public ProductResponseDto toDto(Product products) {
        return ProductResponseDto.builder()
                .idProduct(products.getId())
                .price(products.getPrice())
                .idCustomer(products.getCustomer().getId())
                .quantity(products.getQuantity())
                .nameProduct(products.getName())
                .nameCustomer(products.getCustomer().getName())
                .addressCustomer(products.getCustomer().getAddress())
                .emailCustomer(products.getCustomer().getEmail())
                .refProduct(products.getRefProduct())
                .refCustomer(products.getCustomer().getRefCustomer())
                .build();
    }

    public List<ProductResponseDto> toDtos(List<Product> products) {
        return products.stream().map(ProductMapper::toDto).collect(Collectors.toList());
    }
}
