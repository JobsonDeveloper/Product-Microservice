package br.com.product.micro.exception;

public class ErrorCreatingProductException extends RuntimeException {
    public ErrorCreatingProductException() {super("It was not possible to create the product!");}
    public ErrorCreatingProductException(String message) {
        super(message);
    }
}
