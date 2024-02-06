package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.entities.Product;
import lombok.extern.java.Log;
import org.springframework.batch.core.ItemReadListener;

@Log
public class ProductReaderListener implements ItemReadListener<Product> {
    @Override
    public void onReadError(Exception e) {
        log.info("error when reader : " + e.getLocalizedMessage());
    }
}
