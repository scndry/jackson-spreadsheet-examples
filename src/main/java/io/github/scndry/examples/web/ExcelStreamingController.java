package io.github.scndry.examples.web;

import com.fasterxml.jackson.databind.SequenceWriter;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Spring Boot REST API for streaming large Excel transfers.
 *
 * <p>GET {@code /api/excel/large/download} streams the export with
 * {@link StreamingResponseBody} so the servlet thread is released immediately
 * and the file is generated on Spring MVC's async task executor. Pair with a
 * row-streaming data source (paged repository, JDBC cursor, {@code Stream<T>})
 * to keep memory bounded alongside threads.</p>
 *
 * <p>POST {@code /api/excel/large/upload} pulls the {@link MultipartFile}'s
 * {@link InputStream} through {@link SheetMappingIterator} and processes rows
 * one at a time. The cell-parsing phase keeps heap flat regardless of row
 * count — the controller never materialises a {@code List<Row>}.</p>
 *
 * <p>Spring's default {@code MultipartResolver} still buffers the request body
 * (in memory up to {@code spring.servlet.multipart.file-size-threshold}, then
 * to a temp file) before the controller is invoked. For request-time streaming
 * where the multipart body is parsed directly off the wire — multi-GB uploads
 * without disk buffering — add {@code commons-fileupload2-jakarta-servlet6},
 * disable Spring's resolver with {@code spring.servlet.multipart.enabled=false},
 * and pull the file part's {@code InputStream} via
 * {@code JakartaServletFileUpload.getItemIterator(request)} into
 * {@code mapper.sheetReaderFor(Row.class).readValues(in)}.</p>
 *
 * <p>{@link ExcelController} stays as the simple synchronous example for
 * bounded payloads.</p>
 */
@RestController
@RequestMapping("/api/excel/large")
public class ExcelStreamingController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final String FILE_FIELD = "file";

    private final SpreadsheetMapper mapper = new SpreadsheetMapper();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @DataGrid
    public static class Row {
        @DataColumn("ID") private long id;
        @DataColumn("Name") private String name;
        @DataColumn("Value") private double value;
    }

    /**
     * GET /api/excel/large/download — stream a large export.
     */
    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> download() {
        final StreamingResponseBody body = out -> {
            try (Stream<Row> rows = _rows(100_000);
                 SequenceWriter seq = mapper.sheetWriterFor(Row.class).writeValues(out)) {
                final var it = rows.iterator();
                while (it.hasNext()) seq.write(it.next());
            }
        };
        return ResponseEntity.ok()
                .contentType(XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("export.xlsx").build().toString())
                .body(body);
    }

    /**
     * POST /api/excel/large/upload — row-by-row processing of an upload.
     * Returns the processed row count; the controller never holds the full list.
     */
    @PostMapping("/upload")
    public long upload(@RequestParam(FILE_FIELD) MultipartFile file) throws IOException {
        long count = 0;
        try (InputStream in = file.getInputStream();
             SheetMappingIterator<Row> rows = mapper.sheetReaderFor(Row.class).readValues(in)) {
            while (rows.hasNext()) {
                _process(rows.next());
                count++;
            }
        }
        return count;
    }

    /** Replace with a paged repository, JDBC cursor, or any row-streaming source. */
    private Stream<Row> _rows(final int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> new Row(i, "row-" + i, i * 1.5));
    }

    /** Replace with the per-row sink — domain write, validation, queue dispatch, etc. */
    private void _process(final Row row) {
        // no-op
    }
}
