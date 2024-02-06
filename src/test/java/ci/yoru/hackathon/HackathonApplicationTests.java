package ci.yoru.hackathon;

import ci.yoru.hackathon.controllers.ProductController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HackathonApplicationTests {

    @Autowired
    private ProductController controller;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(controller);
    }

}
