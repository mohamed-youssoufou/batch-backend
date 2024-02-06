package ci.yoru.hackathon.repositories;

import ci.yoru.hackathon.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Map;

public interface BatchRepository extends JpaRepository<Customer, Long> {
    @Query(value = "SELECT PARAMETER_VALUE FROM BATCH_JOB_EXECUTION_PARAMS WHERE PARAMETER_VALUE=:filename", nativeQuery = true)
    Map<String, Object> getFilenameByName(@Param("filename") String filename);
}
