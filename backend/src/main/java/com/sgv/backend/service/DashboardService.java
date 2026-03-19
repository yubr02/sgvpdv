package com.sgv.backend.service;

import com.sgv.backend.dto.DashboardResponse;
import com.sgv.backend.model.Product;
import com.sgv.backend.model.Sale;
import com.sgv.backend.repository.ProductRepository;
import com.sgv.backend.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public DashboardService(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    public DashboardResponse getSummary() {
        List<Product> products = productRepository.findAll();
        List<Sale> sales = saleRepository.findAll();

        BigDecimal revenue = sales.stream()
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalStock = products.stream()
                .map(Product::getStock)
                .reduce(0, Integer::sum);

        Map<LocalDate, BigDecimal> grouped = new LinkedHashMap<>();
        sales.stream()
                .sorted(Comparator.comparing(Sale::getCreatedAt))
                .forEach(sale -> grouped.merge(sale.getCreatedAt().toLocalDate(), sale.getTotal(), BigDecimal::add));

        List<Map<String, Object>> salesByDay = new ArrayList<>();
        grouped.forEach((date, value) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            item.put("value", value);
            salesByDay.add(item);
        });

        List<Map<String, Object>> lowStock = new ArrayList<>();
        products.stream()
                .filter(product -> product.getStock() <= 5)
                .sorted(Comparator.comparing(Product::getStock))
                .forEach(product -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", product.getId());
                    item.put("name", product.getName());
                    item.put("stock", product.getStock());
                    lowStock.add(item);
                });

        List<Map<String, Object>> recentSales = new ArrayList<>();
        saleRepository.findTop5ByOrderByCreatedAtDesc().forEach(sale -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", sale.getId());
            item.put("createdAt", sale.getCreatedAt().toString());
            item.put("total", sale.getTotal());
            recentSales.add(item);
        });

        return new DashboardResponse(
                products.size(),
                sales.size(),
                totalStock,
                revenue,
                salesByDay,
                lowStock,
                recentSales
        );
    }
}
