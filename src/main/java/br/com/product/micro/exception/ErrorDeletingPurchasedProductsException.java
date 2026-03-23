package br.com.product.micro.exception;

public class ErrorDeletingPurchasedProductsException extends RuntimeException {
    public ErrorDeletingPurchasedProductsException() {super("It was not possible to delete purchased product!");}
    public ErrorDeletingPurchasedProductsException(String message) {
        super(message);
    }
}
