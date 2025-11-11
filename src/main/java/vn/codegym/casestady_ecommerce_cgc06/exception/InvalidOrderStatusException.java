package vn.codegym.casestady_ecommerce_cgc06.exception;

public class InvalidOrderStatusException extends RuntimeException{
    public InvalidOrderStatusException(String message) {
        super(message);
    }
}
