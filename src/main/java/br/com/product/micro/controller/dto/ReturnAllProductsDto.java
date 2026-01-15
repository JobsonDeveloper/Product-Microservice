package br.com.product.micro.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "Product")
public record ReturnAllProductsDto(
        String id,
        String name,
        Long barCode,
        String brand,
        Double weight,
        Long quantity,
        Double value,
        String classification,
        String description,
        LocalDate manufacturingDate,
        LocalDate expirationDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
