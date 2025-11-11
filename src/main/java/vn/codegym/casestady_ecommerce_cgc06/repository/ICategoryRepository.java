package vn.codegym.casestady_ecommerce_cgc06.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.codegym.casestady_ecommerce_cgc06.model.Category;

@Repository
public interface ICategoryRepository extends JpaRepository<Category,Long> {
}
