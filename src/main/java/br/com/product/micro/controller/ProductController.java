package br.com.product.micro.controller;

import br.com.product.micro.dto.request.CreateProductDto;
import br.com.product.micro.dto.request.ProductBarCodeListDto;
import br.com.product.micro.dto.request.PurchaseProductDto;
import br.com.product.micro.dto.request.UpdateProductDto;
import br.com.product.micro.dto.response.ProductDeletedSuccessfullyDto;
import br.com.product.micro.dto.response.ProductsDataDto;
import br.com.product.micro.dto.response.ProductInfoDto;
import br.com.product.micro.dto.swagger.DefaultErrorResponseDto;
import br.com.product.micro.dto.swagger.PageProductResponseDto;
import br.com.product.micro.domain.Product;
import br.com.product.micro.dto.swagger.validation.fields.FieldsErrorDto;
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
import java.util.ArrayList;
import java.util.List;

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
                            description = "Product created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Inconsistent request fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FieldsErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Product already registered",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductInfoDto> createProduct(@Valid @RequestBody CreateProductDto productDto) {
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
                new ProductInfoDto(
                        "Product created successfully!",
                        newProduct
                )
        );
    }

    @PutMapping("/api/product/update")
    @Transactional
    @Operation(
            summary = "Update a product",
            description = "Update product information",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Inconsistent request fields",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FieldsErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductInfoDto> changeProduct(@Valid @RequestBody UpdateProductDto productDto) {
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
                new ProductInfoDto(
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
                            description = "Product deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDeletedSuccessfullyDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductDeletedSuccessfullyDto> deleteProduct(@Parameter(description = "Product barcode", required = true) @PathVariable String barcode) {
        Long productBarCode = Long.parseLong(barcode);
        productService.deleteProduct(productBarCode);
        return ResponseEntity.status(HttpStatus.OK).body(new ProductDeletedSuccessfullyDto("Product deleted successfully!"));
    }

    @GetMapping("/api/product/{barcode}/information")
    @Operation(
            summary = "Get product information",
            description = "Return all information of a product by barcode",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductInfoDto> getProductInformation(@Parameter(description = "Product barcode", required = true) @PathVariable String barcode) {
        Long productBarcode = Long.parseLong(barcode);
        Product product = productService.getProduct(productBarcode);

        return ResponseEntity.status(HttpStatus.OK).body(new ProductInfoDto("Product returned successfully!", product));
    }

    @GetMapping("/api/product/list")
    @Operation(
            summary = "List products",
            description = "Return a list of products",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageProductResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<Page<Product>> listProducts(
            @RequestParam(defaultValue = "0", required = false, name = "page") int page,
            @RequestParam(defaultValue = "10", required = false, name = "size") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> products = productService.listProduct(pageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @PatchMapping("/api/product/purchase")
    @Operation(
            summary = "Remove a quantity of product",
            description = "Remove a quantity of product",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product purchased successfully",
                            content = @Content(
                                    mediaType = "applications/json",
                                    schema = @Schema(implementation = ProductInfoDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FieldsErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Insufficient products in stock",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductInfoDto> purchaseProduct(@Valid @RequestBody PurchaseProductDto productDto) {
        Product updatedProduct = productService.removeProductQuantity(productDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ProductInfoDto("Products purchased successfully!", updatedProduct));
    }

    @PostMapping("/api/product/data")
    @Operation(
            summary = "Get products information by barcode",
            description = "Returns information for various products by barcode",
            tags = {"Product"},
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Products information returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductsDataDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "503",
                            description = "Service Unavailable",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DefaultErrorResponseDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<ProductsDataDto> productsData(@Valid @RequestBody ProductBarCodeListDto productBarCodeListDto) {
        List<Long> list = productBarCodeListDto.products();
        List<Product> products = new ArrayList<>();

        list.forEach((Long code) -> {
            Product product = productService.getProduct(code);
            products.add(product);
        });

        return ResponseEntity.status(HttpStatus.OK).body(new ProductsDataDto("Products data returned successfully!", products));
    }
}

