package service;

import exceptions.ValidationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReceiptExporter {
    public Path exportTextReceipt(String receiptText, Path outputDirectory, String receiptNumber) throws ValidationException {
        try {
            Files.createDirectories(outputDirectory);
            Path file = outputDirectory.resolve(receiptNumber + ".txt");
            Files.writeString(file, receiptText, StandardCharsets.UTF_8);
            return file;
        } catch (IOException ex) {
            throw new ValidationException("Receipt export failed: " + ex.getMessage());
        }
    }

    public Path exportSimplePdf(String receiptText, Path outputDirectory, String receiptNumber) throws ValidationException {
        try {
            Files.createDirectories(outputDirectory);
            Path file = outputDirectory.resolve(receiptNumber + ".pdf");
            String escaped = receiptText.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
            String[] lines = escaped.split("\\R");
            StringBuilder content = new StringBuilder("BT /F1 10 Tf 40 780 Td ");
            for (String line : lines) {
                content.append("(").append(line).append(") Tj 0 -14 Td ");
            }
            content.append("ET");
            String pdf = """
                    %%PDF-1.4
                    1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj
                    2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj
                    3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj
                    4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj
                    5 0 obj << /Length %d >> stream
                    %s
                    endstream endobj
                    xref
                    0 6
                    0000000000 65535 f
                    trailer << /Root 1 0 R /Size 6 >>
                    startxref
                    0
                    %%EOF
                    """.formatted(content.length(), content);
            Files.writeString(file, pdf, StandardCharsets.UTF_8);
            return file;
        } catch (IOException ex) {
            throw new ValidationException("PDF export failed: " + ex.getMessage());
        }
    }
}
