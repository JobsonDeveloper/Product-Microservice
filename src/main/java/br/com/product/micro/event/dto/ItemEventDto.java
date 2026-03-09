package br.com.product.micro.event.dto;

public record ItemEventDto(
        String id,
        Long barCode,
        Double value,
        Long quantity
) {
}
