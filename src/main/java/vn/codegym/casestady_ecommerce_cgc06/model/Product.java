package vn.codegym.casestady_ecommerce_cgc06.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = true)
    private String name;
    @Column(nullable = true)
    private int quantity;
    @Column(nullable = true)
    private double price;
    @Column(nullable = true)
    private String description;
    @Column(nullable = true)
    private String imageUrl;
    @Column(nullable = true)
    private String category;
    @ManyToMany
    @JoinTable(
            name = "order_products",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private java.util.List<Order> orders;

    public Product(String name, int quantity, double price, String description, String imageUrl, String category) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
