package br.com.product.micro.controller.dto.swagger;

import br.com.product.micro.controller.dto.ReturnAllProductsDto;
import br.com.product.micro.controller.dto.ReturnProductDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Paginated response")
public class PageProductResponseDto {
    private List<ReturnAllProductsDto> content;
    private PageableResponseDto pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
}

