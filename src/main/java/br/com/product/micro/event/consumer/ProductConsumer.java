package br.com.product.micro.event.consumer;

import br.com.product.micro.domain.*;
import br.com.product.micro.event.dto.DeliveryEventDto;
import br.com.product.micro.event.dto.ItemEventDto;
import br.com.product.micro.event.dto.SaleEventDto;
import br.com.product.micro.exception.*;
import br.com.product.micro.repository.IProductRepository;
import br.com.product.micro.repository.IReservedRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductConsumer {
    private final IProductRepository iProductRepository;
    private final IReservedRepository iReservedRepository;

    public ProductConsumer(
            IProductRepository iProductRepository,
            IReservedRepository iReservedRepository
    ) {
        this.iProductRepository = iProductRepository;
        this.iReservedRepository = iReservedRepository;
    }

    private void saleCreated(SaleEventDto event) {
        String saleId = event.id();
        List<ItemEventDto> items = event.items();
        List<Product> productList = new ArrayList<>();

        items.forEach(product -> {
            Long barCode = product.barCode();
            Long subtrahend = product.quantity();

            Product storageProduct = iProductRepository.findByBarCode(barCode).orElseThrow(ProductNotFoundException::new);

            updateQuantity(storageProduct, subtrahend);
            storageProduct.setQuantity(subtrahend);
            productList.add(storageProduct);
        });

        Reserved reserved = Reserved.builder()
                .saleId(saleId)
                .products(productList)
                .build();

        iReservedRepository.save(reserved);
    }

    private void saleCompleted(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        iReservedRepository.deleteBySaleId(saleId);
    }

    private void saleCanceled(SaleEventDto eventDto) {
        String saleId = eventDto.id();
        Reserved reserved = iReservedRepository.findBySaleId(saleId).orElseThrow(ProductNotFoundException::new);
        List<Product> products = reserved.getProducts();

        products.forEach(item -> {
            Product storedProduct = iProductRepository.findById(item.getId()).orElseThrow(ProductNotFoundException::new);
            Long newProductQuantity = storedProduct.getQuantity() + item.getQuantity();
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

            iProductRepository.save(product);
        });

        iReservedRepository.deleteById(reserved.getId());
    }

    @KafkaListener(
            topics = "sale",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "saleKafkaListenerFactory"
    )
    public void saleListener(SaleEventDto event) {
        Status status = event.status();

        if (status.equals(Status.CREATED)) saleCreated(event);
        if (status.equals(Status.CANCELED)) saleCanceled(event);
    }

    @KafkaListener(
            topics = "delivery",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "deliveryKafkaListenerFactory"
    )
    public void deliveryListener(DeliveryEventDto event) {
        if (!event.status().equals(Status.DELIVERED)) return;
        iReservedRepository.deleteBySaleId(event.saleId());
    }

    private void updateQuantity(Product product, Long subtrahend) {
        long newProductQuantity = product.getQuantity() - subtrahend;

        if (newProductQuantity < 0) throw new InsufficientProductsException();

        product.setQuantity(newProductQuantity);

        iProductRepository.save(product);
    }
}
