package ru.dmitry.carfleet.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import ru.dmitry.carfleet.collection.MyArrayList;
import ru.dmitry.carfleet.model.Car;
import ru.dmitry.carfleet.validation.CarValidator;
import ru.dmitry.carfleet.validation.ValidationException;

public class CarFileLoader {
  public static final String DEFAULT_PATH = "data/cars.csv";
  public static final String HEADER = "model;horsePower;year";
  private static final String SEPARATOR = ";";
  private static final String COMMENT_PREFIX = "#";
  private static final char BYTE_ORDER_MARK = '\uFEFF';
  private static final int FIELD_COUNT = 3;
  private static final int MODEL_INDEX = 0;
  private static final int HORSE_POWER_INDEX = 1;
  private static final int YEAR_INDEX = 2;

  public LoadResult load(Path path) throws IOException {
    List<Car> cars = new MyArrayList<>();
    List<String> errors = new MyArrayList<>();
    try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
      String line;
      int lineNumber = 0;
      boolean headerChecked = false;
      while ((line = reader.readLine()) != null) {
        lineNumber++;
        String trimmed = stripBom(line).strip();
        if (trimmed.isEmpty() || trimmed.startsWith(COMMENT_PREFIX)) {
          continue;
        }
        if (!headerChecked) {
          headerChecked = true;
          if (HEADER.equalsIgnoreCase(trimmed.replace(" ", ""))) {
            continue;
          }
        }
        try {
          cars.add(parseLine(trimmed));
        } catch (ValidationException e) {
          errors.add("Строка " + lineNumber + ": " + e.getMessage());
        }
      }
    }
    return new LoadResult(cars, errors);
  }

  public Car parseLine(String line) {
    String[] parts = line.split(SEPARATOR, -1);
    if (parts.length != FIELD_COUNT) {
      throw new ValidationException(
          "ожидалось " + FIELD_COUNT + " поля через «" + SEPARATOR + "», получено " + parts.length);
    }
    String model = CarValidator.validateModel(parts[MODEL_INDEX]);
    int horsePower = CarValidator.parseHorsePower(parts[HORSE_POWER_INDEX]);
    int year = CarValidator.parseYear(parts[YEAR_INDEX]);
    return new Car(model, horsePower, year);
  }

  private static String stripBom(String line) {
    if (!line.isEmpty() && line.charAt(0) == BYTE_ORDER_MARK) {
      return line.substring(1);
    }
    return line;
  }
}
