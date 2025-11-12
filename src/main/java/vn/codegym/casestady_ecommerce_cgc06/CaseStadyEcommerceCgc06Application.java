package vn.codegym.casestady_ecommerce_cgc06;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vn.codegym.casestady_ecommerce_cgc06.model.User;
import vn.codegym.casestady_ecommerce_cgc06.repository.UserRepository;

@SpringBootApplication
public class CaseStadyEcommerceCgc06Application {

    public static void main(String[] args) {
        SpringApplication.run(CaseStadyEcommerceCgc06Application.class, args);
    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder encoder) {
//        return args -> {
//            if (userRepository.findByUsername("admin").isEmpty()) {
//                User admin = new User(
//                        "admin",
//                        encoder.encode("123456"),
//                        "admin@gmail.com",
//                        "0123456789",
//                        "Hà Nội",
//                        "ROLE_ADMIN"
//                );
//                userRepository.save(admin);
//                System.out.println("✅ Admin created: admin / 123456");
//            }
//            if (userRepository.findByUsername("user").isEmpty()) {
//                User user = new User(
//                        "user",
//                        encoder.encode("123456"),
//                        "user@gmail.com",
//                        "0987654321",
//                        "Hồ Chí Minh",
//                        "ROLE_USER"
//                );
//                userRepository.save(user);
//                System.out.println("✅ User created: user / 123456");
//            }
//        };
//    }
}
