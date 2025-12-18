package br.com.product.micro.controller;

import br.com.product.micro.controller.dto.CreateProductDto;
import br.com.product.micro.controller.dto.ProductCreatedSuccessfullyDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@Tag(name = "Product", description = "Product operations")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/api/product/create")
    @Transactional
    @Operation(
            summary = "Create a product",
            description = "Create a new product in the system",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Product created successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductCreatedSuccessfullyDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"error\": \"Validation failed\", \"errors\": \"[...]\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Product already registered",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"CONFLICT\", \"message\": \"Product already registered!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "It was not possible to create the product",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"It was not possible to create the product!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ProductCreatedSuccessfullyDto> createProduct(@Valid @RequestBody CreateProductDto productDto) {
        String name = productDto.name();
        Long barCode = productDto.barCode();
        String brand = productDto.brand();
        Double weight = productDto.weight();
        Long quantity = productDto.quantity();
        Double value = productDto.value();
        String classification = productDto.classification();
        String description = productDto.description();
        LocalDate manufacturingDate = productDto.manufacturing();
        LocalDate expirationDate = productDto.expiration();
        LocalDateTime createdAt = LocalDateTime.now();

        Product product = Product.builder()
                .name(name)
                .barCode(barCode)
                .brand(brand)
                .weight(weight)
                .quantity(quantity)
                .barCode(barCode)
                .value(value)
                .classification(classification)
                .description(description)
                .manufacturingDate(manufacturingDate)
                .expirationDate(expirationDate)
                .createdAt(createdAt)
                .build();

        Product newProduct = productService.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ProductCreatedSuccessfullyDto(
                        "Product created successfully!",
                        newProduct
                )
        );
    }
}
