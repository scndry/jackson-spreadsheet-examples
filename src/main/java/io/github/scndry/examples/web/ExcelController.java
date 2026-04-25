package io.github.scndry.examples.web;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import io.github.scndry.jackson.dataformat.spreadsheet.deser.SheetInput;
import io.github.scndry.jackson.dataformat.spreadsheet.ser.SheetOutput;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Spring Boot REST API for Excel file download and upload.
 *
 * <p>GET {@code /api/excel/download} exports data as an XLSX file.
 * POST {@code /api/excel/upload} imports an uploaded XLSX file into Java objects.
 * Demonstrates integration with {@code HttpServletResponse} and {@code MultipartFile}.</p>
 */
@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    private final SpreadsheetMapper mapper = new SpreadsheetMapper();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class UserData {
        private String name;
        private String email;
        private int age;
    }

    /**
     * GET /api/excel/download — generate and download Excel file.
     */
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=users.xlsx");

        var users = List.of(
                new UserData("Alice", "alice@example.com", 30),
                new UserData("Bob", "bob@example.com", 25));

        mapper.writeValue(SheetOutput.target(response.getOutputStream()), users, UserData.class);
    }

    /**
     * POST /api/excel/upload — upload and parse Excel file.
     */
    @PostMapping("/upload")
    public List<UserData> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return mapper.readValues(
                SheetInput.source(file.getInputStream()), UserData.class);
    }
}
