package ru.dmitry.carfleet.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.dmitry.carfleet.model.Car;

class CarFileLoaderTest {
  private final CarFileLoader loader = new CarFileLoader();

  @TempDir
  Path tempDir;

  private Path write(String content) throws IOException {
    Path file = tempDir.resolve("cars.csv");
    Files.writeString(file, content, StandardCharsets.UTF_8);
    return file;
  }

  @Test
  void loadsValidFile() throws IOException {
    Path file = write("model;horsePower;year\n"
        + "# комментарий\n"
        + "Toyota Camry;150;2018\n"
        + "\n"
        + "УАЗ Патриот;150;2021\n");
    LoadResult result = loader.load(file);
    assertEquals(2, result.loadedCount());
    assertEquals(0, result.skippedCount());
    assertEquals(new Car("УАЗ Патриот", 150, 2021), result.getCars().get(1));
  }

  @Test
  void skipsBrokenLinesAndCountsThem() throws IOException {
    Path file = write("model;horsePower;year\n"
        + "Kia Rio;123;2017\n"
        + "Renault Logan;82\n"
        + "Nissan;сто;2019\n"
        + "Mazda;150;1800\n"
        + "Honda;158;2019;extra\n"
        + "Lada Vesta;106;2020\n");
    LoadResult result = loader.load(file);
    assertEquals(2, result.loadedCount());
    assertEquals(4, result.skippedCount());
    assertTrue(result.getErrors().get(0).startsWith("Строка 3:"));
    assertTrue(result.getErrors().get(2).contains("год должен быть от 1886"));
  }

  @Test
  void handlesBomAndFileWithoutHeader() throws IOException {
    Path file = write("\uFEFFKia Rio;123;2017\n");
    LoadResult result = loader.load(file);
    assertEquals(1, result.loadedCount());
    assertEquals(0, result.skippedCount());
  }

  @Test
  void emptyFileGivesEmptyResult() throws IOException {
    LoadResult result = loader.load(write(""));
    assertEquals(0, result.loadedCount());
    assertEquals(0, result.skippedCount());
  }

  @Test
  void missingFileThrowsNoSuchFile() {
    assertThrows(NoSuchFileException.class, () -> loader.load(tempDir.resolve("missing.csv")));
  }

  @Test
  void projectDataFilesLoad() throws IOException {
    LoadResult valid = loader.load(Path.of("data", "cars.csv"));
    assertEquals(12, valid.loadedCount());
    assertEquals(0, valid.skippedCount());
    LoadResult broken = loader.load(Path.of("data", "cars_with_errors.csv"));
    assertEquals(2, broken.loadedCount());
    assertEquals(10, broken.skippedCount());
  }
}
