package br.com.product.micro.controller.dto;

import br.com.product.micro.domain.Product;

public record ReturnProductDto(
        String message,
        Product product
) {
}
