package br.com.product.micro.exception;

public class ErrorDeletingProductException extends RuntimeException {
    public ErrorDeletingProductException() {
        super("It was not possible to delete the product!");
    }
    public ErrorDeletingProductException(String message) {
        super(message);
    }
}
