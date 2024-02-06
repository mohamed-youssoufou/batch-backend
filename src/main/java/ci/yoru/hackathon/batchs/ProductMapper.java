package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.entities.Customer;
import ci.yoru.hackathon.entities.Product;
import ci.yoru.hackathon.exceptions.ProductNotFoundException;
import ci.yoru.hackathon.repositories.CustomerRepository;
import ci.yoru.hackathon.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductMapper implements FieldSetMapper<Product> {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;


    @Override
    public Product mapFieldSet(FieldSet fieldSet) throws ParseException {
        if(productRepository
                .findBy(
                        Example.of(Product.builder().refProduct(fieldSet.readString("productRef")).build()),
                        FluentQuery.FetchableFluentQuery::first).isPresent()){
            throw new ProductNotFoundException("product ref:" + fieldSet.readString("productRef") + " already existe");
        }

        val customer = customerRepository
                .findBy(Example.of(Customer.builder().refCustomer(fieldSet.readString("ref_client")).build()),
                        FluentQuery.FetchableFluentQuery::first)
                .or(() -> Optional.of(
                            customerRepository.save(Customer.builder()
                                    .refCustomer(fieldSet.readString("ref_client"))
                                    .address(fieldSet.readString("address"))
                                    .email(fieldSet.readString("email"))
                                    .name(fieldSet.readString("client_name"))
                                    .build())
                    )
                );

        return Product
                .builder()
                .refProduct(fieldSet.readString("productRef"))
                .customer(customer.get())
                .name(fieldSet.readString("productName"))
                .price(Double.valueOf(fieldSet.readString("productPrice")))
                .quantity(Long.valueOf(fieldSet.readString("productQuantity")))
                .build();
    }
}
