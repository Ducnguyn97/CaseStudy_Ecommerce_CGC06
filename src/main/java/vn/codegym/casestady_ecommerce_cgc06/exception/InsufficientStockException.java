package vn.codegym.casestady_ecommerce_cgc06.exception;

public class InsufficientStockException extends RuntimeException{
    public InsufficientStockException(String message) {
        super(message);
    }
}
