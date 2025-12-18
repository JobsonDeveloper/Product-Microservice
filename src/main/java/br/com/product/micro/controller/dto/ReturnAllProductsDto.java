package br.com.product.micro.controller.dto;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReturnAllProductsDto(
        String id,
        String name,
        Long code,
        Double value,
        String classification,
        LocalDate manufacturingDate,
        LocalDate expirationDate,
        LocalDateTime createdAt
) {
}
