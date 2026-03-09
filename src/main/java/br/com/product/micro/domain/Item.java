package br.com.product.micro.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String id;
    private Long barCode;
    private Double value;
    private Long quantity;
}
