package br.com.product.micro.exception;

public class ReservedProductsNotFoundException extends RuntimeException {
    public ReservedProductsNotFoundException() {
        super("Reserved products not found!");
    }
    public ReservedProductsNotFoundException(String message) {
        super(message);
    }
}
