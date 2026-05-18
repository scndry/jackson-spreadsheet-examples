package io.github.scndry.examples.web;

import com.fasterxml.jackson.databind.SequenceWriter;
import io.github.scndry.jackson.dataformat.spreadsheet.SheetMappingIterator;
import io.github.scndry.jackson.dataformat.spreadsheet.SpreadsheetMapper;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataColumn;
import io.github.scndry.jackson.dataformat.spreadsheet.annotation.DataGrid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
 * <p>POST {@code /api/excel/large/upload} drives the request body through
 * Apache Commons FileUpload's {@link JakartaServletFileUpload} streaming API,
 * pulls the file part's {@link InputStream} into a {@link SheetMappingIterator},
 * and processes rows one at a time. Heap stays flat regardless of file size
 * because nothing is buffered before the parser sees the bytes — the multipart
 * body, the cell stream, and the row dispatch are all pull-based.</p>
 *
 * <p>For the streaming upload to be truly request-time, Spring's default
 * {@code MultipartResolver} must not consume the body first. Set
 * {@code spring.servlet.multipart.enabled=false} in {@code application.yaml},
 * or scope the file-streaming endpoint behind a path that the resolver does
 * not intercept. The simpler synchronous controller in {@link ExcelController}
 * relies on Spring's resolver and stays the right choice for bounded payloads.</p>
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
     * POST /api/excel/large/upload — request-time streaming upload.
     * Returns the processed row count; the controller never buffers the
     * request body or holds the full row list.
     */
    @PostMapping("/upload")
    public long upload(final HttpServletRequest request) throws IOException, FileUploadException {
        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("request is not multipart/form-data");
        }
        final JakartaServletFileUpload<?, ?> upload = new JakartaServletFileUpload<>();
        final FileItemInputIterator iter = upload.getItemIterator(request);
        while (iter.hasNext()) {
            final FileItemInput item = iter.next();
            if (item.isFormField() || !FILE_FIELD.equals(item.getFieldName())) continue;
            try (InputStream in = item.getInputStream();
                 SheetMappingIterator<Row> rows = mapper.sheetReaderFor(Row.class).readValues(in)) {
                long count = 0;
                while (rows.hasNext()) {
                    _process(rows.next());
                    count++;
                }
                return count;
            }
        }
        throw new IllegalArgumentException("multipart request missing '" + FILE_FIELD + "' part");
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
