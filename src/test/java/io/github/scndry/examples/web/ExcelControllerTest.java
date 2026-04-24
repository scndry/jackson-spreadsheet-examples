package io.github.scndry.examples.web;

import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExcelController.class)
class ExcelControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void download() throws Exception {
        mockMvc.perform(get("/api/excel/download"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=users.xlsx"))
                .andExpect(content().contentType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    @Test
    void upload() throws Exception {
        // Create a valid Excel file
        var mapper = new SpreadsheetMapper();
        var buf = new ByteArrayOutputStream();
        mapper.writeValue(
                io.github.scndry.jackson.dataformat.spreadsheet.ser.SheetOutput.target(buf),
                List.of(new ExcelController.UserData("Alice", "alice@example.com", 30)),
                ExcelController.UserData.class);

        var file = new MockMultipartFile("file", "users.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                buf.toByteArray());

        mockMvc.perform(multipart("/api/excel/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].email").value("alice@example.com"));
    }
}
