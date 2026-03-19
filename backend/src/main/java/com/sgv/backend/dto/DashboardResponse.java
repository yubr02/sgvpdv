package com.sgv.backend.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record DashboardResponse(
        long totalProducts,
        long totalSales,
        int totalStock,
        BigDecimal totalRevenue,
        List<Map<String, Object>> salesByDay,
        List<Map<String, Object>> lowStockProducts,
        List<Map<String, Object>> recentSales
) {
}
