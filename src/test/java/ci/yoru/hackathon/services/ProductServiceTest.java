package ci.yoru.hackathon.services;

import ci.yoru.hackathon.Utils.TestUtils;
import ci.yoru.hackathon.entities.Customer;
import ci.yoru.hackathon.entities.Product;
import ci.yoru.hackathon.exceptions.BadFileException;
import ci.yoru.hackathon.exceptions.ProductNotFoundException;
import ci.yoru.hackathon.repositories.ProductRepository;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private final ProductService service;
    private final ProductRepository productRepository;

    ProductServiceTest(){
        productRepository = Mockito.mock(ProductRepository.class);
        service = new ProductService(productRepository, "");
    }

    @Test
    void should_throw_exception_when_given_missing_product_by_customer_id() {
        Mockito.when(productRepository.findBy(Mockito.any(Example.class), Mockito.any())).thenReturn(List.of());
        Assertions.assertThrows(ProductNotFoundException.class, () -> service.getProductsByCustomerRef(TestUtils.customer_ref), String.format("customer %s have not products", TestUtils.customer_ref));
    }

    @Test
    void should_return_product_when_given_good_customer_id() {
        val products = TestUtils.createProducts();
        Mockito.when(productRepository.findBy(Mockito.any(Example.class), Mockito.any())).thenReturn(products);
        Assertions.assertEquals("pc", service.getProductsByCustomerRef(TestUtils.customer_ref).get(0).nameProduct());
    }

    @Test
    void should_throw_exception_given_bad_file_extension() {
        val file = new MockMultipartFile(
                "text.txt",
                "text.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "hello world".getBytes());
        Assertions.assertThrows(BadFileException.class, () -> service.create(file), "wrong file format, format accepted is csv");
    }

    @Test
    void should_throw_exception_given_bad_file() {
        val file = new MockMultipartFile("text.txt", new byte[]{});
        Assertions.assertThrows(BadFileException.class, () -> service.create(file), "wrong file");
    }

    @Test
    void should_failed_with_good_extension_but_wrong_file_name() {
        val file = new MockMultipartFile(
                "file",
                "text.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "hello world".getBytes());
        Assertions.assertThrows(BadFileException.class, () -> service.create(file), "wrong file format, format accepted is csv");
    }

    @Test
    void should_passed_given_good_file_extension() {
        val file = new MockMultipartFile(
                "file",
                "CLT-129-01012018.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "hello world".getBytes());
        val dto = service.create(file);
        Assertions.assertEquals("OK", dto.status());

    }
}