package br.com.product.micro.exception;

public class InsufficientProductsException extends RuntimeException {
    public InsufficientProductsException() {
        super("Insufficient products in stock!");
    }
    public InsufficientProductsException(String message) {
        super(message);
    }
}
