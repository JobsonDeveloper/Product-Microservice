package br.com.product.micro.dto.response;

import br.com.product.micro.domain.Product;

import java.util.List;

public record ProductsDataDto(
        String message,
        List<Product> products
) {
}
