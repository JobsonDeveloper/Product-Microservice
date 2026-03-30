package br.com.product.micro.exception;

public class ErrorDeletingReservedProductsException extends RuntimeException {
    public ErrorDeletingReservedProductsException() {super("It was not possible to delete reserved products!");}
    public ErrorDeletingReservedProductsException(String message) {
        super(message);
    }
}
