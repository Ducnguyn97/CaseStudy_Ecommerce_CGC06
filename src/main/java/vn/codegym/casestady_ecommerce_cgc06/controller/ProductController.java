package vn.codegym.casestady_ecommerce_cgc06.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.codegym.casestady_ecommerce_cgc06.model.Category;
import vn.codegym.casestady_ecommerce_cgc06.model.Image;
import vn.codegym.casestady_ecommerce_cgc06.model.Product;
import vn.codegym.casestady_ecommerce_cgc06.service.impl.CategoryService;
import vn.codegym.casestady_ecommerce_cgc06.service.impl.ProductService;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final ProductService productService;
    private final CategoryService categoryService;

    // ✅ Lấy tất cả sản phẩm
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = (List<Product>) productService.findAll();
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // ✅ Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ Thêm sản phẩm mới (với ảnh upload)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProduct(
            @RequestParam("name") String name,
            @RequestParam("price") Double price,
            @RequestParam("stock") String stock,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "images", required = false) MultipartFile[] images
    ) throws IOException {

        // 1️⃣ Tạo sản phẩm
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);

        // 2️⃣ Gán Category
        Category category = categoryService.findById(categoryId).orElse(null);
        if (category == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        product.setCategory(category);

        // 3️⃣ Lưu ảnh
        List<Image> imageEntities = new ArrayList<>();
        if (images != null) {
            for (MultipartFile file : images) {
                if (file != null && !file.isEmpty()) {
                    String webPath = storeAndBuildWebPath(file);
                    Image img = new Image();
                    img.setUrl(webPath);
                    img.setProduct(product);
                    imageEntities.add(img);
                }
            }
        }

        product.setImages(imageEntities);
        if (!imageEntities.isEmpty()) {
            product.setImageUrl(imageEntities.get(0).getUrl());
        }

        // 4️⃣ Lưu DB (cascade phải được bật ở Product.images)
        Product savedProduct = productService.save(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    private String storeAndBuildWebPath(@NotNull MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(
                Objects.requireNonNullElse(file.getOriginalFilename(), "")
        );
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot > -1) ext = original.substring(dot);
        String filename = UUID.randomUUID().toString() + ext;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path target = uploadPath.resolve(filename).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/img/" + filename; // ✅ trỏ đúng folder static/img
    }

    // ✅ Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.findById(id)
                .map(existing -> {
                    product.setId(existing.getId());
                    return new ResponseEntity<>(productService.save(product), HttpStatus.OK);
                })
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.findById(id).isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        productService.remove(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
