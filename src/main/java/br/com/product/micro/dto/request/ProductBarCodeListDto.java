package br.com.product.micro.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record ProductBarCodeListDto(
        @NotEmpty(message = "The barcode list must not be empty!")
        List<@Positive(message = "Barcode must be a positive number") Long> products
) {
}
