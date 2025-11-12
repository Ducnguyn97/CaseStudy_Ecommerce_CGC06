package vn.codegym.casestady_ecommerce_cgc06.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import vn.codegym.casestady_ecommerce_cgc06.dto.LoginRequest;
import vn.codegym.casestady_ecommerce_cgc06.model.User;
import vn.codegym.casestady_ecommerce_cgc06.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
//        Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());
//
//        if (optionalUser.isEmpty()) {
//            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu!");
//        }
//
//        User user = optionalUser.get();
//
//        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
//            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu!");
//        }
//
//        return ResponseEntity.ok("Đăng nhập thành công!");
//    }

@PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu");
        }

        User user = userOpt.get();

        // Kiểm tra password
        if(!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body("Sai tên đăng nhập hoặc mật khẩu");
        }

        // Tạo response JSON chỉ gồm username + role
        Map<String, String> res = new HashMap<>();
        res.put("username", user.getUsername());
        res.put("role", user.getRole()); // "ROLE_ADMIN" hoặc "ROLE_USER"
        return ResponseEntity.ok(res);
    }
}
