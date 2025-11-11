package vn.codegym.casestady_ecommerce_cgc06.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)

    private LocalDateTime orderDate;
    @Column(nullable = false)

    private double totalAmount;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)

    private User user;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
    @Column(nullable = false, length = 20)
    private String status;

    public Order(LocalDateTime orderDate, double totalAmount, User user, OrderStatus status) {
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.user = user;
        this.status = status;
    }
}
