package com.sgv.backend.controller;

import com.sgv.backend.service.ReportService;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportExcel() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-sgv.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(reportService.exportExcel());
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> exportPdf() throws IOException {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-sgv.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(reportService.exportPdf());
    }
}
