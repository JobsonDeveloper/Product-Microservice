package br.com.product.micro.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleCreatedEventDto(
        String id,
        List<ItemEventDto> items
) {
}
