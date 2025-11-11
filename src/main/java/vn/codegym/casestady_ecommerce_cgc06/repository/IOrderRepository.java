package vn.codegym.casestady_ecommerce_cgc06.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.casestady_ecommerce_cgc06.model.Order;

import java.util.List;

@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findByUserId(Long userId);

}
