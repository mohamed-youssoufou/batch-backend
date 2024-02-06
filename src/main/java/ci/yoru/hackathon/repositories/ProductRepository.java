package ci.yoru.hackathon.repositories;

import ci.yoru.hackathon.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductRepository extends JpaRepository<Product, Long> { }
