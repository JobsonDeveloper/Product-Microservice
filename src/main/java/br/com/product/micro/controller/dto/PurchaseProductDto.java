package br.com.product.micro.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PurchaseProductDto(
        @NotNull(message = "The bar code is required!") @Min(value = 1, message = "The bar code must be valid!") Long barCode,
        @NotNull(message = "The quantity purchased is required!") @Min(value = 1, message = "The quantity purchased must be valid!") Long quantityPurchased
) {
}
