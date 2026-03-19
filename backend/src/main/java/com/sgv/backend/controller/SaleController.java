package com.sgv.backend.controller;

import com.sgv.backend.dto.SaleRequest;
import com.sgv.backend.model.Sale;
import com.sgv.backend.service.SaleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    @GetMapping
    public List<Sale> listAll() {
        return saleService.listAll();
    }

    @PostMapping
    public Sale create(@Valid @RequestBody SaleRequest request) {
        return saleService.create(request);
    }
}
