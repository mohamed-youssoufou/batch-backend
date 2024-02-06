package ci.yoru.hackathon.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    private Long quantity;
    private Double price;
    @Column(name = "ref_client")
    private String refProduct;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_customer")
    private Customer customer;

    @PrePersist
    private void addDate() {
        createdAt = new Date();
    }
}
