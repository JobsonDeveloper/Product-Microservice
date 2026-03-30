package br.com.product.micro.event.dto;

import br.com.product.micro.domain.Status;

public record PaymentEventDto(
        String saleId,
        Status status
) {
}
