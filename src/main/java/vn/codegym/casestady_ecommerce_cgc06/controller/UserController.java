package vn.codegym.casestady_ecommerce_cgc06.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.codegym.casestady_ecommerce_cgc06.model.User;
import vn.codegym.casestady_ecommerce_cgc06.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.codegym.casestady_ecommerce_cgc06.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:63342") // Cho phép test từ Postman hoặc frontend
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    // Lấy tất cả user
    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy 1 user theo id
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()) return ResponseEntity.status(404).body("User not found");
        return ResponseEntity.ok(user.get());
    }

    // Tạo user mới (đăng ký admin không cho)
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if(userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(400).body("Username đã tồn tại");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER"); // chỉ tạo role user
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }

    // Cập nhật user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User user = optionalUser.get();
        user.setUsername(newUser.getUsername());
        user.setEmail(newUser.getEmail());
        user.setPhoneNumber(newUser.getPhoneNumber());
        user.setAddress(newUser.getAddress());
        if(newUser.getPassword() != null && !newUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        }
        userRepository.save(user);
        return ResponseEntity.ok("User updated successfully");
    }

    // Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if(userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully");
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }
    //Tìm kiếm và phân trang
    @GetMapping("/search")
    public Page<User> searchUsers(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size
    ) {
        return userService.searchUsers(keyword, page, size);
    }
}
