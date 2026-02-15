package br.com.product.micro.controller.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateProductDto(
        @NotNull(message = "The name is required!") @Size(min = 2, message = "The name must be valid!") String name,
        @NotNull(message = "The bar code is required!") @Min(value = 1, message = "The bar code must be valid!") Long barCode,
        @NotNull(message = "The brand is required!") @Size(min = 2, message = "The brand must be valid!") String brand,
        @NotNull(message = "The weight is required!") @DecimalMin("0.01") Double weight,
        @NotNull(message = "The quantity is required!") @Min(value = 0, message = "The quantity must be valid!") Long quantity,
        @NotNull(message = "The product value is required!") @DecimalMin("0.00") Double value,
        @NotNull(message = "The classification is required!") @Size(min = 2, message = "The classification must be valid!") String classification,
        @NotNull(message = "The description is required!") @Size(min = 2, message = "The description must be valid!") String description,
        @NotNull(message = "The manufacturing date is required!") @PastOrPresent(message = "The manufacturing date must be valid!") LocalDate manufacturing,
        @NotNull(message = "The expiration date is required!") @Future(message = "The expiration date must be valid!") LocalDate expiration
) {
}
