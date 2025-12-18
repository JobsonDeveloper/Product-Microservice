package br.com.product.micro.controller.dto;

import br.com.product.micro.domain.Product;

public record ProductCreatedSuccessfullyDto(
        String message,
        Product product
) {
}
