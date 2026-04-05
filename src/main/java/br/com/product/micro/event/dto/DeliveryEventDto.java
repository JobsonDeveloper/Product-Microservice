package br.com.product.micro.event.dto;

import br.com.product.micro.domain.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeliveryEventDto(
        String saleId,
        Status status
) {
}
