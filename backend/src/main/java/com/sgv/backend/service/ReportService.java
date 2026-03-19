package com.sgv.backend.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sgv.backend.model.Product;
import com.sgv.backend.model.Sale;
import com.sgv.backend.repository.ProductRepository;
import com.sgv.backend.repository.SaleRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public ReportService(ProductRepository productRepository, SaleRepository saleRepository) {
        this.productRepository = productRepository;
        this.saleRepository = saleRepository;
    }

    public byte[] exportExcel() throws IOException {
        List<Product> products = productRepository.findAll();
        List<Sale> sales = saleRepository.findAll();

        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            XSSFSheet stockSheet = workbook.createSheet("Estoque");
            Row stockHeader = stockSheet.createRow(0);
            stockHeader.createCell(0).setCellValue("Produto");
            stockHeader.createCell(1).setCellValue("Categoria");
            stockHeader.createCell(2).setCellValue("Estoque");
            stockHeader.createCell(3).setCellValue("Preco");

            for (int index = 0; index < products.size(); index++) {
                Product product = products.get(index);
                Row row = stockSheet.createRow(index + 1);
                row.createCell(0).setCellValue(product.getName());
                row.createCell(1).setCellValue(product.getCategory());
                row.createCell(2).setCellValue(product.getStock());
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
            }

            XSSFSheet salesSheet = workbook.createSheet("Vendas");
            Row salesHeader = salesSheet.createRow(0);
            salesHeader.createCell(0).setCellValue("Venda");
            salesHeader.createCell(1).setCellValue("Data");
            salesHeader.createCell(2).setCellValue("Total");

            for (int index = 0; index < sales.size(); index++) {
                Sale sale = sales.get(index);
                Row row = salesSheet.createRow(index + 1);
                row.createCell(0).setCellValue(sale.getId());
                row.createCell(1).setCellValue(sale.getCreatedAt().toString());
                row.createCell(2).setCellValue(sale.getTotal().doubleValue());
            }

            workbook.write(output);
            return output.toByteArray();
        }
    }

    public byte[] exportPdf() throws IOException {
        List<Product> products = productRepository.findAll();
        List<Sale> sales = saleRepository.findAll();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String productItems = products.stream()
                .map(product -> "<li>%s - estoque: %d - preco: R$ %s</li>"
                        .formatted(product.getName(), product.getStock(), product.getPrice()))
                .reduce("", String::concat);

        String html = """
                <html>
                <body style='font-family: Arial, sans-serif; padding: 24px;'>
                    <h1>Sistema de Gestao de Vendas</h1>
                    <p>Total de produtos: %d</p>
                    <p>Total de vendas: %d</p>
                    <p>Receita total: R$ %s</p>
                    <h2>Estoque</h2>
                    <ul>%s</ul>
                </body>
                </html>
                """.formatted(products.size(), sales.size(), totalRevenue, productItems);

        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(output);
            builder.run();
            return output.toByteArray();
        } catch (Exception exception) {
            throw new IOException("Falha ao gerar PDF", exception);
        }
    }
}
