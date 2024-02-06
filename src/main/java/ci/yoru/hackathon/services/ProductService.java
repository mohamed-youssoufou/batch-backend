package ci.yoru.hackathon.services;

import ci.yoru.hackathon.controllers.dto.ProductResponseDto;
import ci.yoru.hackathon.controllers.dto.UploadDtoResponse;
import ci.yoru.hackathon.entities.Customer;
import ci.yoru.hackathon.entities.Product;
import ci.yoru.hackathon.exceptions.BadFileException;
import ci.yoru.hackathon.exceptions.MoveFileException;
import ci.yoru.hackathon.exceptions.ProductNotFoundException;
import ci.yoru.hackathon.mappers.ProductMapper;
import ci.yoru.hackathon.repositories.ProductRepository;
import ci.yoru.hackathon.utils.FilesUtils;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final String depositAbsoluteFullname;

    ProductService(final ProductRepository productRepository, @Value("${deposit_path}") final String depositAbsoluteFullname) {
        this.productRepository = productRepository;
        this.depositAbsoluteFullname = depositAbsoluteFullname;
    }
    public List<ProductResponseDto> getProductsByCustomerRef(String ref_customer) {
        val products = productRepository.findBy(
                Example.of(Product.builder()
                                .customer(Customer.builder()
                                        .refCustomer(Optional.ofNullable(ref_customer).orElseThrow(() -> new IllegalArgumentException("customerId is required")))
                                        .build())
                        .build()),
                FluentQuery.FetchableFluentQuery::all);
        if(products.size() == 0) throw new ProductNotFoundException(String.format("customer %s have not products", ref_customer));
        return ProductMapper.toDtos(products);
    }

    public UploadDtoResponse create(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadFileException("wrong file");
        }
        val fileName = multipartFile.getOriginalFilename();
        if (fileName != null && !FilesUtils.regex(fileName)) {
            throw new BadFileException("wrong file format, format accepted is csv");
        }
        val originalFileName = multipartFile.getOriginalFilename();
        val fileToImport = new File(depositAbsoluteFullname + originalFileName);
        try {
            multipartFile.transferTo(fileToImport);
        } catch (Exception e) {
            throw new MoveFileException("error when moved file");
        }
        return UploadDtoResponse
                .builder()
                .status("OK")
                .build();
    }
}
