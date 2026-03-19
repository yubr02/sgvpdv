package com.sgv.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaleRequest(@Valid @NotEmpty List<SaleItemRequest> items) {
}
