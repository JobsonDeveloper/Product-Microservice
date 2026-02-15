package br.com.product.micro.exception;

public class ProductAlreadyRegisteredException extends RuntimeException {
    public ProductAlreadyRegisteredException() {
        super("The product already registered!");
    }
    public ProductAlreadyRegisteredException(String message) {
        super(message);
    }
}
