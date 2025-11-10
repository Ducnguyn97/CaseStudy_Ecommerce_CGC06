package vn.codegym.casestady_ecommerce_cgc06.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.codegym.casestady_ecommerce_cgc06.dto.OrderItemRequest;
import vn.codegym.casestady_ecommerce_cgc06.model.Order;
import vn.codegym.casestady_ecommerce_cgc06.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    @PostMapping("/{id}")
    public ResponseEntity<Order> createOrder(@PathVariable("id") Long userId
            , @RequestBody List<OrderItemRequest> items){
        return ResponseEntity.ok(orderService.createOrder(userId, items));
    }


}
