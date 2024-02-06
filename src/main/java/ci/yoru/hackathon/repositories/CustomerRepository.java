package ci.yoru.hackathon.repositories;

import ci.yoru.hackathon.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
