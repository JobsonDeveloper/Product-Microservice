package br.com.product.micro.exception;

public class ErrorUpdatingProductException extends RuntimeException {
    public ErrorUpdatingProductException() {
        super("It was not possible to update the product!");
    }

    public ErrorUpdatingProductException(String message) {
        super(message);
    }
}
