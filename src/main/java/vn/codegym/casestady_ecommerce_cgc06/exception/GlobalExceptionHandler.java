package vn.codegym.casestady_ecommerce_cgc06.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleOrderNotFound(OrderNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UserNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientStock(InsufficientStockException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOrderStatus(InvalidOrderStatusException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return createErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("errors", errors);
        
        return ResponseEntity.badRequest().body(response);
    }
    
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", status.value());
        response.put("error", status.getReasonPhrase());
        response.put("message", message);
        
        return ResponseEntity.status(status).body(response);
    }
}