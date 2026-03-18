package br.com.product.micro.exception;

public class PurchasedProductsNotFoundException extends RuntimeException {
    public PurchasedProductsNotFoundException() {
        super("Purchased products not found!");
    }
    public PurchasedProductsNotFoundException(String message) {
        super(message);
    }
}
