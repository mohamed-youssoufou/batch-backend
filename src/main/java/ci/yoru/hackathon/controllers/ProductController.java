package ci.yoru.hackathon.controllers;

import ci.yoru.hackathon.controllers.dto.ProductResponseDto;
import ci.yoru.hackathon.controllers.dto.UploadDtoResponse;
import ci.yoru.hackathon.services.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @GetMapping(value = "/customer/{ref_customer}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProductResponseDto>> getProductByRefCustomer(@PathVariable final String ref_customer) {
        val dto = service.getProductsByCustomerRef(ref_customer);
        return ResponseEntity.ok()
                .body(dto);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UploadDtoResponse> create(@RequestPart MultipartFile file) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(file));
    }
}
