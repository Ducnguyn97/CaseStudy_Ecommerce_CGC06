package vn.codegym.casestady_ecommerce_cgc06.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String address;
}
