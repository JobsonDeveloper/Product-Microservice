package br.com.product.micro.event.dto;

import br.com.product.micro.domain.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SaleEventDto(
        String id,
        Status status,
        List<ItemEventDto> items
) {
}
