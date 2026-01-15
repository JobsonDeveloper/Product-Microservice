package br.com.product.micro.controller.dto.swagger;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Pagination informations")
public class PageableResponseDto {
    private int pageNumber;
}
