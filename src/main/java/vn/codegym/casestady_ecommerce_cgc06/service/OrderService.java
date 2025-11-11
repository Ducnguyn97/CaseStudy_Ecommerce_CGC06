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
    
    // Các trạng thái đơn hàng
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
        // Kiểm tra đầu vào
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Danh sách sản phẩm không được để trống");
        }
        
        // Tìm user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));
        
        // Tạo đơn hàng mới
        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(ORDER_STATUS_PENDING);
        
        // Tính tổng tiền và tạo order details
        double totalAmount = 0.0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        
        for (OrderItemRequest item : items) {
            // Tìm sản phẩm
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với id: " + item.getProductId()));
            
            // Kiểm tra số lượng tồn kho
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Không đủ hàng cho sản phẩm: " + product.getName() 
                        + ". Còn lại: " + product.getStock() + ", yêu cầu: " + item.getQuantity());
            }
            
            // Tạo order detail
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(product.getPrice());
            
            orderDetails.add(orderDetail);
            
            // Tính tổng tiền
            totalAmount += product.getPrice() * item.getQuantity();
            
            // Trừ số lượng tồn kho
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);
        }
        
        order.setTotalAmount(totalAmount);
        
        // Lưu order trước
        Order savedOrder = orderRepository.save(order);
        
        // Gán order cho các order details và lưu
        orderDetails.forEach(detail -> detail.setOrder(savedOrder));
        orderDetailRepository.saveAll(orderDetails);
        
        savedOrder.setOrderDetails(orderDetails);
        return savedOrder;
    }
    
    public Order updateOrderStatus(Long orderId, String newStatus) {
        // Kiểm tra đầu vào
        if (newStatus == null || newStatus.trim().isEmpty()) {
            throw new RuntimeException("Trạng thái không được để trống");
        }
        // Chuẩn hóa status
        String status = newStatus.trim().toUpperCase();
        // Kiểm tra status hợp lệ
        if (!isValidStatus(status)) {
            throw new RuntimeException("Trạng thái không hợp lệ: " + status);
        }
        // Tìm order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
        // Kiểm tra có thể thay đổi trạng thái không
        if (!canChangeStatus(order.getStatus(), status)) {
            throw new RuntimeException("Không thể chuyển từ trạng thái " + order.getStatus() + " sang " + status);
        }
        order.setStatus(status);
        return orderRepository.save(order);
    }
    public List<Order> getOrderByUserId(Long userId) {
        // Kiểm tra user tồn tại
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với id: " + userId));
        
        return orderRepository.findByUserId(userId);
    }
    
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
    }
    
    public Order cancelOrder(Long orderId) {
        // Tìm order
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với id: " + orderId));
        
        // Kiểm tra có thể hủy không
        if (ORDER_STATUS_DELIVERED.equals(order.getStatus()) || 
            ORDER_STATUS_CANCELLED.equals(order.getStatus())) {
            throw new RuntimeException("Không thể hủy đơn hàng với trạng thái: " + order.getStatus());
        }
        
        // Hoàn lại số lượng sản phẩm
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetail detail : orderDetails) {
            Product product = detail.getProduct();
            product.setStock(product.getStock() + detail.getQuantity());
            productRepository.save(product);
        }
        
        // Cập nhật trạng thái
        order.setStatus(ORDER_STATUS_CANCELLED);
        return orderRepository.save(order);
    }
    
    // Kiểm tra status có hợp lệ không
    private boolean isValidStatus(String status) {
        return ORDER_STATUS_PENDING.equals(status) ||
               ORDER_STATUS_CONFIRMED.equals(status) ||
               ORDER_STATUS_PROCESSING.equals(status) ||
               ORDER_STATUS_SHIPPED.equals(status) ||
               ORDER_STATUS_DELIVERED.equals(status) ||
               ORDER_STATUS_CANCELLED.equals(status);
    }
    
    // Kiểm tra có thể chuyển trạng thái không
    private boolean canChangeStatus(String currentStatus, String newStatus) {
        // Nếu trạng thái giống nhau thì không cần thay đổi
        if (currentStatus.equals(newStatus)) {
            return true;
        }
        
        // Từ PENDING có thể chuyển sang CONFIRMED hoặc CANCELLED
        if (ORDER_STATUS_PENDING.equals(currentStatus)) {
            return ORDER_STATUS_CONFIRMED.equals(newStatus) || ORDER_STATUS_CANCELLED.equals(newStatus);
        }
        
        // Từ CONFIRMED có thể chuyển sang PROCESSING hoặc CANCELLED
        if (ORDER_STATUS_CONFIRMED.equals(currentStatus)) {
            return ORDER_STATUS_PROCESSING.equals(newStatus) || ORDER_STATUS_CANCELLED.equals(newStatus);
        }
        
        // Từ PROCESSING có thể chuyển sang SHIPPED hoặc CANCELLED
        if (ORDER_STATUS_PROCESSING.equals(currentStatus)) {
            return ORDER_STATUS_SHIPPED.equals(newStatus) || ORDER_STATUS_CANCELLED.equals(newStatus);
        }
        
        // Từ SHIPPED chỉ có thể chuyển sang DELIVERED
        if (ORDER_STATUS_SHIPPED.equals(currentStatus)) {
            return ORDER_STATUS_DELIVERED.equals(newStatus);
        }
        
        // DELIVERED và CANCELLED là trạng thái cuối, không thể thay đổi
        return false;
    }
}