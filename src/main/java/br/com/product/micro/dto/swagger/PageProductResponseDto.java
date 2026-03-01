package br.com.product.micro.dto.swagger;

import br.com.product.micro.domain.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Paginated response")
public class PageProductResponseDto {
    private List<Product> content;
    private PageableResponseDto pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private int size;
}
