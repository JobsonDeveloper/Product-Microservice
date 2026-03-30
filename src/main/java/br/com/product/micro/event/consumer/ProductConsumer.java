package br.com.product.micro.event.consumer;

import br.com.product.micro.domain.*;
import br.com.product.micro.event.dto.ItemEventDto;
import br.com.product.micro.event.dto.PaymentEventDto;
import br.com.product.micro.event.dto.SaleEventDto;
import br.com.product.micro.exception.*;
import br.com.product.micro.repository.IDeliveredRepository;
import br.com.product.micro.repository.IProductRepository;
import br.com.product.micro.repository.IPurchasedRepository;
import br.com.product.micro.repository.IReservedRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductConsumer {
    private final IProductRepository iProductRepository;
    private final IReservedRepository iReservedRepository;
    private final IPurchasedRepository iPurchasedRepository;
    private final IDeliveredRepository iDeliveredRepository;

    public ProductConsumer(
            IProductRepository iProductRepository,
            IReservedRepository iReservedRepository, IPurchasedRepository iPurchasedRepository, IDeliveredRepository iDeliveredRepository
    ) {
        this.iProductRepository = iProductRepository;
        this.iReservedRepository = iReservedRepository;
        this.iPurchasedRepository = iPurchasedRepository;
        this.iDeliveredRepository = iDeliveredRepository;
    }

    private void saleCreated(SaleEventDto event) {
        String saleId = event.id();
        List<ItemEventDto> items = event.items();
        List<Product> productList = new ArrayList<>();

        items.forEach(product -> {
            Long barCode = product.barCode();
            Long subtraendo = product.quantity();

            Optional<Product> storageProduct = iProductRepository.findByBarCode(barCode);

            if (!storageProduct.isPresent()) {
                throw new ProductNotFoundException();
            }

            Product productUpdated = updateQuantity(storageProduct.get(), subtraendo);

            storageProduct.get().setQuantity(subtraendo);
            productList.add(storageProduct.get());
        });

        Reserved reserved = Reserved.builder()
                .saleId(saleId)
                .products(productList)
                .build();
        iReservedRepository.save(reserved);
    }

    private void saleCompleted(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        Optional<Purchased> purchasedProducts = iPurchasedRepository.findBySaleId(saleId);

        if (!purchasedProducts.isPresent()) {
            throw new PurchasedProductsNotFoundException();
        }

        Purchased purchased = purchasedProducts.get();
        iPurchasedRepository.deleteById(purchased.getId());

        Delivered delivered = Delivered.builder()
                .saleId(purchased.getSaleId())
                .products(purchased.getProducts())
                .build();

        Delivered newDeliveredSale = iDeliveredRepository.save(delivered);
    }

    private void saleCanceled(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        Optional<Reserved> reserved = iReservedRepository.findBySaleId(saleId);

        if (reserved.isPresent()) {
            List<Product> products = reserved.get().getProducts();

            products.forEach(item -> {
                Optional<Product> storedProduct = iProductRepository.findById(item.getId());

                if (!storedProduct.isPresent()) {
                    throw new ProductNotFoundException();
                }

                Long newProductQuantity = storedProduct.get().getQuantity() + item.getQuantity();
                Product product = Product.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .barCode(item.getBarCode())
                        .brand(item.getBrand())
                        .weight(item.getWeight())
                        .quantity(newProductQuantity)
                        .value(item.getValue())
                        .classification(item.getClassification())
                        .description(item.getDescription())
                        .manufacturingDate(item.getManufacturingDate())
                        .expirationDate(item.getExpirationDate())
                        .createdAt(item.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();

                Product updatedStock = iProductRepository.save(product);
            });

            iReservedRepository.deleteById(reserved.get().getId());
            Optional<Reserved> deleted = iReservedRepository.findBySaleId(saleId);

            if (deleted.isPresent()) {
                throw new ErrorDeletingReservedProductsException();
            }
        } else {
            Optional<Purchased> purchased = iPurchasedRepository.findBySaleId(saleId);

            if (purchased.isPresent()) {
                List<Product> products = purchased.get().getProducts();

                products.forEach(item -> {
                    Optional<Product> storedProduct = iProductRepository.findById(item.getId());

                    if (!storedProduct.isPresent()) {
                        throw new ProductNotFoundException();
                    }

                    Long newProductQuantity = storedProduct.get().getQuantity() + item.getQuantity();
                    Product product = Product.builder()
                            .id(item.getId())
                            .name(item.getName())
                            .barCode(item.getBarCode())
                            .brand(item.getBrand())
                            .weight(item.getWeight())
                            .quantity(newProductQuantity)
                            .value(item.getValue())
                            .classification(item.getClassification())
                            .description(item.getDescription())
                            .manufacturingDate(item.getManufacturingDate())
                            .expirationDate(item.getExpirationDate())
                            .createdAt(item.getCreatedAt())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    Product updatedStock = iProductRepository.save(product);
                });

                iPurchasedRepository.deleteById(purchased.get().getId());
                Optional<Purchased> deleted = iPurchasedRepository.findBySaleId(saleId);

                if (deleted.isPresent()) {
                    throw new ErrorDeletingPurchasedProductsException();
                }
            }
        }
    }

    @KafkaListener(
            topics = "sale",
            groupId = "product-group",
            containerFactory = "saleKafkaListenerFactory"
    )
    public void saleListener(SaleEventDto event) {
        Status status = event.status();

        if (status.equals(Status.CREATED)) {
            saleCreated(event);
        }

        if (status.equals(Status.DELIVERED)) {
            saleCompleted(event);
        }

        if (status.equals(Status.CANCELED)) {
            saleCanceled(event);
        }
    }

    @KafkaListener(
            topics = "payment",
            groupId = "product-group",
            containerFactory = "paymentKafkaListenerFactory"
    )
    public void paymentListener(PaymentEventDto paymentEventDto) {
        Status status = paymentEventDto.status();

        if (status.equals(Status.PAID)) {
            String saleId = paymentEventDto.saleId();

            Optional<Reserved> reservedProducts = iReservedRepository.findBySaleId(saleId);

            if (!reservedProducts.isPresent()) {
                throw new ReservedProductsNotFoundException();
            }

            Purchased purchasedProduct = Purchased.builder()
                    .saleId(saleId)
                    .products(reservedProducts.get().getProducts())
                    .build();

            Purchased newPurchasedProduct = iPurchasedRepository.save(purchasedProduct);

            if (newPurchasedProduct.getId() != null) {
                iReservedRepository.delete(reservedProducts.get());
            }
        }
    }

    private Product updateQuantity(Product product, Long subtraendo) {
        Long newProductQuantity = product.getQuantity() - subtraendo;

        if (newProductQuantity < 0) {
            throw new InsufficientProductsException();
        }

        product.setQuantity(newProductQuantity);
        Product updated = iProductRepository.save(product);

        if (updated.getId() == null) {
            throw new ErrorUpdatingProductException();
        }

        return updated;
    }
}
