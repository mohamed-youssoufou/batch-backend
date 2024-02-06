package ci.yoru.hackathon.controllers;

import ci.yoru.hackathon.Utils.TestUtils;
import ci.yoru.hackathon.controllers.dto.UploadDtoResponse;
import ci.yoru.hackathon.mappers.ProductMapper;
import ci.yoru.hackathon.services.ProductService;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;

@WebMvcTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @MockBean
    private ProductService service;
    @Autowired
    private MockMvc mockMvc;


    @Test
    void should_create_send_file() throws Exception {
        val file = new MockMultipartFile(
                "file",
                "text.csv",
                MediaType.TEXT_PLAIN_VALUE,
                "hello world".getBytes());
        when(service.create(file)).thenReturn(UploadDtoResponse.builder().status("OK").build());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .multipart("/api/v1/products")
                                .file(file)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void should_return_product() throws Exception {
        val product = TestUtils.createProducts();
        when(service.getProductsByCustomerRef(TestUtils.customer_ref)).thenReturn(ProductMapper.toDtos(product));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/v1/products/customer/" + TestUtils.customer_ref)
                ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idProduct")
                        .value(1L));
    }
}