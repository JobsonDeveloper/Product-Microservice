package br.com.product.micro.dto.response;

import br.com.product.micro.domain.Product;

public record ProductInfoDto(
        String message,
        Product product
) {
}
