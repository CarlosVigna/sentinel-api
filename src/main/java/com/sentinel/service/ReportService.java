package com.sentinel.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.sentinel.enums.OccurrenceStatus;
import com.sentinel.model.Occurrence;
import com.sentinel.repository.OccurrenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OccurrenceRepository occurrenceRepository;

    // ✅ TEXTO COPIÁVEL
    public String generateTextReport() {
        List<Occurrence> all = occurrenceRepository.findAll();

        List<Occurrence> open = all.stream()
                .filter(o -> o.getStatus() == OccurrenceStatus.OPEN || o.getStatus() == OccurrenceStatus.IN_PROGRESS)
                .toList();

        List<Occurrence> resolved = all.stream()
                .filter(o -> o.getStatus() == OccurrenceStatus.RESOLVED)
                .toList();

        StringBuilder sb = new StringBuilder();

        sb.append("RELATÓRIO DE OCORRÊNCIAS\n");
        sb.append("Data: ").append(LocalDateTime.now()).append("\n");
        sb.append("====================================\n\n");

        // 🔴 EM ABERTO
        sb.append("🔴 EM ABERTO\n");
        sb.append("Placa | Categoria | Status | Resolvido Por | Data | Observações\n");

        for (Occurrence o : open) {
            sb.append(o.getPlate()).append(" | ")
                    .append(o.getCategory().getName()).append(" | ")
                    .append(o.getStatus()).append(" | ")
                    .append(o.getCreatedBy().getNome()).append(" | ")
                    .append(o.getCreatedAt()).append(" | ")
                    .append(o.getDescription() != null ? o.getDescription() : "-")
                    .append("\n");
        }

        sb.append("\n");

        // 🟢 RESOLVIDAS
        sb.append("🟢 RESOLVIDAS\n");
        sb.append("Placa | Categoria | Status | Criado Por | Data | Observações\n");

        for (Occurrence o : resolved) {
            sb.append(o.getPlate()).append(" | ")
                    .append(o.getCategory().getName()).append(" | ")
                    .append(o.getStatus()).append(" | ")
                    .append(o.getResolvedBy() != null ? o.getResolvedBy().getNome() : "-").append(" | ")
                    .append(o.getResolvedAt()).append(" | ")
                    .append(o.getDescription() != null ? o.getDescription() : "-")
                    .append("\n");
        }

        return sb.toString();
    }



    // ✅ PDF
    public byte[] generatePdfReport() throws Exception {
        List<Occurrence> all = occurrenceRepository.findAll();

        List<Occurrence> open = all.stream()
                .filter(o -> o.getStatus() == OccurrenceStatus.OPEN || o.getStatus() == OccurrenceStatus.IN_PROGRESS)
                .toList();

        List<Occurrence> resolved = all.stream()
                .filter(o -> o.getStatus() == OccurrenceStatus.RESOLVED)
                .toList();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("RELATÓRIO DE OCORRÊNCIAS").setBold().setFontSize(16));
        document.add(new Paragraph("Data: " + LocalDateTime.now()));
        document.add(new Paragraph(" "));

        // 🔴 EM ABERTO
        document.add(new Paragraph("EM ABERTO").setBold());

        Table openTable = new Table(6);
        openTable.addHeaderCell("Placa");
        openTable.addHeaderCell("Categoria");
        openTable.addHeaderCell("Status");
        openTable.addHeaderCell("Criado Por");
        openTable.addHeaderCell("Data");
        openTable.addHeaderCell("Observações");

        for (Occurrence o : open) {
            openTable.addCell(o.getPlate());
            openTable.addCell(o.getCategory().getName());
            openTable.addCell(o.getStatus().name());
            openTable.addCell(o.getCreatedBy().getNome());
            openTable.addCell(o.getCreatedAt().toString());
            openTable.addCell(o.getDescription() != null ? o.getDescription() : "-");
        }

        document.add(openTable);
        document.add(new Paragraph(" "));

        // 🟢 RESOLVIDAS
        document.add(new Paragraph("RESOLVIDAS").setBold());

        Table resolvedTable = new Table(6);
        resolvedTable.addHeaderCell("Placa");
        resolvedTable.addHeaderCell("Categoria");
        resolvedTable.addHeaderCell("Status");
        resolvedTable.addHeaderCell("Resolvido Por");
        resolvedTable.addHeaderCell("Data");
        resolvedTable.addHeaderCell("Observações");

        for (Occurrence o : resolved) {
            resolvedTable.addCell(o.getPlate());
            resolvedTable.addCell(o.getCategory().getName());
            resolvedTable.addCell(o.getStatus().name());
            resolvedTable.addCell(o.getResolvedBy() != null ? o.getResolvedBy().getNome() : "-");
            resolvedTable.addCell(o.getResolvedAt() != null ? o.getResolvedAt().toString() : "-");
            resolvedTable.addCell(o.getDescription() != null ? o.getDescription() : "-");
        }

        document.add(resolvedTable);

        document.close();

        return out.toByteArray();
    }
}