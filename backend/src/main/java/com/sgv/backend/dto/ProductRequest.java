package com.sgv.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String name,
        String category,
        @Min(0) Integer stock,
        @Min(0) BigDecimal price
) {
}
