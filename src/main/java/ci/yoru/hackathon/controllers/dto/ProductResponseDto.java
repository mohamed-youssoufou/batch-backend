package ci.yoru.hackathon.controllers.dto;

import lombok.Builder;

@Builder
public record ProductResponseDto(long idProduct, String nameProduct, long idCustomer, String emailCustomer,
                                 String addressCustomer, String nameCustomer, long quantity, double price,
                                 String refProduct, String refCustomer) {
}
