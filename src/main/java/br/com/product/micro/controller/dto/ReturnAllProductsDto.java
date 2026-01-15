package br.com.product.micro.controller.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
