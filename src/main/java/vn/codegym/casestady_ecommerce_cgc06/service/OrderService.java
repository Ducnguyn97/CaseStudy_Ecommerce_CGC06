package vn.codegym.casestady_ecommerce_cgc06.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.codegym.casestady_ecommerce_cgc06.dto.OrderItemRequest;
import vn.codegym.casestady_ecommerce_cgc06.model.Order;
import vn.codegym.casestady_ecommerce_cgc06.model.OrderDetail;
import vn.codegym.casestady_ecommerce_cgc06.model.Product;
import vn.codegym.casestady_ecommerce_cgc06.model.User;
import vn.codegym.casestady_ecommerce_cgc06.repository.IOrderDetailRepository;
import vn.codegym.casestady_ecommerce_cgc06.repository.IOrderRepository;
import vn.codegym.casestady_ecommerce_cgc06.repository.IProductRepository;
import vn.codegym.casestady_ecommerce_cgc06.repository.IUserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    
    // Constants for order status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_CONFIRMED = "CONFIRMED";
    public static final String ORDER_STATUS_PROCESSING = "PROCESSING";
    public static final String ORDER_STATUS_SHIPPED = "SHIPPED";
    public static final String ORDER_STATUS_DELIVERED = "DELIVERED";
    public static final String ORDER_STATUS_CANCELLED = "CANCELLED";
    
    private final IOrderRepository orderRepository;
    private final IOrderDetailRepository orderDetailRepository;
    private final IProductRepository productRepository;
    private final IUserRepository userRepository;
    
    @Autowired
    public OrderService(IOrderRepository orderRepository, 
                       IOrderDetailRepository orderDetailRepository, 
                       IProductRepository productRepository, 
                       IUserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }
    
    public Order createOrder(Long userId, List<OrderItemRequest> items) {
        // Tìm người dùng - sử dụng model User chứ không phải Spring Security User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        // Tạo order mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(ORDER_STATUS_PENDING);
        
        // Khởi tạo list để lưu order details
        List<OrderDetail> orderDetails = new ArrayList<>();
        double totalAmount = 0.0;
        
        // Xử lý từng item trong order
        for (OrderItemRequest item : items) {
            // Tìm product
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProductId()));
            
            // Kiểm tra stock (nếu Product có trường stock)
            if (product.getStock() != null && product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + product.getName() + 
                                         ". Available: " + product.getStock() + 
                                         ", Requested: " + item.getQuantity());
            }
            
            // Tạo order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(product.getPrice()); // Giá tại thời điểm đặt hàng
            
            orderDetails.add(orderDetail);
            
            // Tính tổng tiền
            totalAmount += product.getPrice() * item.getQuantity();
            
            // Cập nhật stock (nếu cần)
            if (product.getStock() != null) {
                int currentStock = product.getStock();
                int newStock = currentStock - item.getQuantity();
                product.setStock(newStock);
                productRepository.save(product);
            }
        }
        
        // Set tổng tiền cho order
        order.setTotalAmount(totalAmount);
        
        // Lưu order trước
        Order savedOrder = orderRepository.save(order);
        
        // Set order cho tất cả order details và lưu
        orderDetails.forEach(detail -> detail.setOrder(savedOrder));
        orderDetailRepository.saveAll(orderDetails);
        
        // Set order details cho order để return đầy đủ thông tin
        savedOrder.setOrderDetails(orderDetails);
        
        return savedOrder;
    }
    //method quan ly status
    public Order updateOrderStatus(Long orderId, String newStatus){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }
    public List<Order> getAllOrdersByStatus(String status){
        return orderRepository.findAll().stream()
                .filter(order -> order.getStatus().equals(status)).toList();
    }
}