package com.sgv.backend.service;

import com.sgv.backend.dto.SaleItemRequest;
import com.sgv.backend.dto.SaleRequest;
import com.sgv.backend.model.Product;
import com.sgv.backend.model.Sale;
import com.sgv.backend.model.SaleItem;
import com.sgv.backend.repository.ProductRepository;
import com.sgv.backend.repository.SaleRepository;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;

    public SaleService(SaleRepository saleRepository, ProductRepository productRepository) {
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
    }

    public List<Sale> listAll() {
        return saleRepository.findAll();
    }

    @Transactional
    public Sale create(SaleRequest request) {
        Sale sale = new Sale();
        sale.setCreatedAt(LocalDateTime.now());

        List<SaleItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (SaleItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado"));

            if (product.getStock() < itemRequest.quantity()) {
                throw new IllegalArgumentException("Estoque insuficiente para " + product.getName());
            }

            SaleItem item = new SaleItem();
            item.setSale(sale);
            item.setProduct(product);
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
            items.add(item);

            product.setStock(product.getStock() - itemRequest.quantity());
            total = total.add(item.getSubtotal());
        }

        sale.setItems(items);
        sale.setTotal(total);
        return saleRepository.save(sale);
    }
}
