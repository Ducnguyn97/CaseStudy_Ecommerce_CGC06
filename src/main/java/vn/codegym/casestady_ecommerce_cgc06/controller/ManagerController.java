package vn.codegym.casestady_ecommerce_cgc06.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping("/")
    public String managerPage() {
        // forward nội bộ tới file trong /static/admin/
        return "forward:/admin/manager.html";
    }
}
