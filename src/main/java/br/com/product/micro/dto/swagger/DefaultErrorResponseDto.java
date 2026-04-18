package br.com.product.micro.dto.swagger;

public record DefaultErrorResponseDto(
        String status,
        String message
) {
}
