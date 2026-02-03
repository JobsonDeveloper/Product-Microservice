package br.com.product.micro.controller;

import br.com.product.micro.controller.dto.*;
import br.com.product.micro.controller.dto.swagger.PageProductResponseDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.exception.InsufficientProductsException;
import br.com.product.micro.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

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
                                    schema = @Schema(implementation = ReturnProductDto.class)
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
    public ResponseEntity<ReturnProductDto> createProduct(@Valid @RequestBody CreateProductDto productDto) {
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
                .createdAt(null)
                .build();

        Product newProduct = productService.createProduct(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ReturnProductDto(
                        "Product created successfully!",
                        newProduct
                )
        );
    }

    @PutMapping("/api/product/update")
    @Transactional
    @Operation(
            summary = "Update a product",
            description = "Update product informations",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnProductDto.class)
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
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"Not Found\", \"message\": \"Product not found!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "It was not possible to update the product",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"It was not possible to update the product!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ReturnProductDto> changeProduct(@Valid @RequestBody UpdateProductDto productDto) {
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

        Product productUpdated = productService.updateProduct(product);

        return ResponseEntity.status(HttpStatus.OK).body(
                new ReturnProductDto(
                        "Product updated successfully!",
                        productUpdated
                )
        );
    }

    @DeleteMapping("/api/product/{barcode}/delete")
    @Operation(
            summary = "Delete a product",
            description = "Delete all products with a same bar code",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product deleted successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDeletedSuccessfullyDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"NOT_FOUND\", \"message\": \"Product not found!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Error when deleting the product!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"It was not possible to delete the product!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ProductDeletedSuccessfullyDto> deleteProduct(@Parameter(description = "Product barcode", required = true) @PathVariable String barcode) {
        Long productBarCode = Long.parseLong(barcode);

        Boolean deleted = productService.deleteProduct(productBarCode);

        return ResponseEntity.status(HttpStatus.OK).body(new ProductDeletedSuccessfullyDto("Product deleted successfully!"));
    }

    @GetMapping("/api/product/{barcode}/informations")
    @Operation(
            summary = "Get product informations",
            description = "Return all informations of a product by barcode",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product returned successfully!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ReturnProductDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"NOT_FOUND\", \"message\": \"Product not found!\" }"
                                    )
                            )
                    ),
            }
    )
    public ResponseEntity<ReturnProductDto> getProductInformations(@Parameter(description = "Product barcode", required = true) @PathVariable String barcode) {
        Long productBarcode = Long.parseLong(barcode);
        Product product = productService.getProduct(productBarcode);

        return ResponseEntity.status(HttpStatus.OK).body(new ReturnProductDto("Product returned successfully!", product));
    }

    @GetMapping("/api/product/list")
    @Operation(
            summary = "List products",
            description = "Return a list with all products",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List returned successfully!",
                            content = @Content(
                                    mediaType = "applications/json",
                                    schema = @Schema(
                                            implementation = PageProductResponseDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "It was not possible to create the product",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"INTERNAL_SERVER_ERROR\", \"message\": \"Internal server error!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<Page<ReturnAllProductsDto>> listProducts(
            @RequestParam(defaultValue = "0", required = false, name = "page") int page,
            @RequestParam(defaultValue = "10", required = false, name = "size") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ReturnAllProductsDto> products = productService.listProduct(pageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PutMapping("/api/product/purchase")
    @Operation(
            summary = "Purchase product",
            description = "Purchase a quantity of products",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product purchased successfully!",
                            content = @Content(
                                    mediaType = "applications/json",
                                    schema = @Schema(
                                            implementation = ReturnProductDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Product not found!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"NOT_FOUND\", \"message\": \"Product not found!\" }"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Insufficient products in stock!",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            example = "{ \"status\": \"CONFLICT\", \"message\": \"Insufficient products in stock!\" }"
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<ReturnProductDto> purchaseProduct(@Valid @RequestBody PurchaseProductDto productDto) {
        Product updatedProduct = productService.removeProductQuantity(productDto);

        return ResponseEntity.status(HttpStatus.OK).body(new ReturnProductDto("Products purchased successfully!", updatedProduct));
    }
}

