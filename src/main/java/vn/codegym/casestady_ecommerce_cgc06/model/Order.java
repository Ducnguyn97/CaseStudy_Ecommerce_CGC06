package vn.codegym.casestady_ecommerce_cgc06.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Date orderDate;
    @Column(nullable = false)
    private double totalAmount;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false)
    private String status;
}
