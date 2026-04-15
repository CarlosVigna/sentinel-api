package com.sentinel.controller;

import com.sentinel.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ✅ TEXTO (copiar/colar)
    @GetMapping("/text")
    public ResponseEntity<String> generateTextReport() {
        return ResponseEntity.ok(reportService.generateTextReport());
    }

    // ✅ PDF (download)
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> generatePdfReport() throws Exception {
        byte[] pdf = reportService.generatePdfReport();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=relatorio.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }
}