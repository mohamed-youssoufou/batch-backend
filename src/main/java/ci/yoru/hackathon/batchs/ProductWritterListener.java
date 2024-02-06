package ci.yoru.hackathon.batchs;

import ci.yoru.hackathon.entities.Product;
import lombok.extern.java.Log;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

import java.util.stream.Collectors;

@Log
public class ProductWritterListener implements ItemWriteListener<Product> {
    @Override
    public void onWriteError(Exception exception, Chunk<? extends Product> items) {
        log.info("error when writter : " + exception.getMessage());
        log.info("on line : " + items.getItems().stream().map(Object::toString).collect(Collectors.joining(", ")));
    }
}
