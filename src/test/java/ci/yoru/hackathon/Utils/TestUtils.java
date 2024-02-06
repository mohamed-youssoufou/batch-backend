package ci.yoru.hackathon.Utils;

import ci.yoru.hackathon.entities.Customer;
import ci.yoru.hackathon.entities.Product;
import lombok.experimental.UtilityClass;

import java.util.Date;
import java.util.List;

@UtilityClass
public class TestUtils {
    public final String customer_ref = "CUST_1";
    public final String product_ref = "PROD_1";

    public Customer createCustomer() {
        return Customer.builder()
                .id(1L)
                .refCustomer(customer_ref)
                .address("Abidjan")
                .email("brice.lassissi@gmail.com")
                .name("Mohamed")
                .createdAt(new Date())
                .build();
    }

    public List<Product> createProducts() {
        return List.of(
                Product.builder()
                        .id(1L)
                        .name("pc")
                        .price(100d)
                        .createdAt(new Date())
                        .quantity(100L)
                        .customer(TestUtils.createCustomer())
                        .build());
    }
}
