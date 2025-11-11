package vn.codegym.casestady_ecommerce_cgc06.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.casestady_ecommerce_cgc06.model.OrderDetail;

import java.util.List;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {
   List<OrderDetail > findByOrderId(Long orderId);
}
