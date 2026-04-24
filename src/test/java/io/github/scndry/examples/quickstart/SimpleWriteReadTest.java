package io.github.scndry.examples.quickstart;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleWriteReadTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAndReadBack() throws Exception {
        File file = tempDir.resolve("products.xlsx").toFile();

        SimpleWriteExample.write(file);

        List<Product> products = SimpleReadExample.read(file);

        assertThat(products).hasSize(3);
        assertThat(products.get(0).getName()).isEqualTo("Apple");
        assertThat(products.get(0).getQuantity()).isEqualTo(10);
        assertThat(products.get(0).getPrice()).isEqualTo(1.50);
        assertThat(products.get(2).getName()).isEqualTo("Cherry");
    }
}
