package vn.codegym.casestady_ecommerce_cgc06.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.codegym.casestady_ecommerce_cgc06.model.Category;
import vn.codegym.casestady_ecommerce_cgc06.repository.ICategoryRepository;
import vn.codegym.casestady_ecommerce_cgc06.service.ICategoryService;

import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    @Autowired
    private ICategoryRepository ICategoryRepository;

    @Override
    public Iterable<Category> findAll() {
        return ICategoryRepository.findAll();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return ICategoryRepository.findById(id);
    }

    @Override
    public Category save(Category category) {
        return ICategoryRepository.save(category);
    }

    @Override
    public void remove(Long id) {
        ICategoryRepository.deleteById(id);
    }
}
