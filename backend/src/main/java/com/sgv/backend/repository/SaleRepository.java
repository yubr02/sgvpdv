package com.sgv.backend.repository;

import com.sgv.backend.model.Sale;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findTop5ByOrderByCreatedAtDesc();
}
