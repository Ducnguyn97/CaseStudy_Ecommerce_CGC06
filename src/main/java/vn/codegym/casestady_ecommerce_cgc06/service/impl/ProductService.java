package vn.codegym.casestady_ecommerce_cgc06.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.codegym.casestady_ecommerce_cgc06.model.Product;
import vn.codegym.casestady_ecommerce_cgc06.repository.IProductRepository;
import vn.codegym.casestady_ecommerce_cgc06.service.IProductService;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    @Autowired
    private IProductRepository productRepository;

    @Override
    public Iterable<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Override
    public void remove(Long id) {
        productRepository.deleteById(id);
    }

}
