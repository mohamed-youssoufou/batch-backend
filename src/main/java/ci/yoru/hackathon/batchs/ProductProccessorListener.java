package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.entities.Product;
import lombok.extern.java.Log;
import org.springframework.batch.core.ItemProcessListener;

@Log
public class ProductProccessorListener implements ItemProcessListener<Product, Product> {
    @Override
    public void onProcessError(Product item, Exception exception) {
        log.info("error when writter : " + exception.getMessage());
        log.info(String.format("id: %s, produc name: %s, customerEmail: %s", item.getId(), item.getName(), item.getCustomer().getEmail()));
    }
}
