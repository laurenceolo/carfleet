package ru.dmitry.carfleet.validation;

import java.time.Year;

public final class CarValidator {
  public static final int MAX_MODEL_LENGTH = 50;
  public static final int MIN_HORSE_POWER = 1;
  public static final int MAX_HORSE_POWER = 2000;
  public static final int MIN_YEAR = 1886;

  private CarValidator() {
  }

  public static int maxYear() {
    return Year.now().getValue();
  }

  public static String validateModel(String model) {
    if (model == null || model.isBlank()) {
      throw new ValidationException("модель не должна быть пустой");
    }
    String trimmed = model.strip();
    if (trimmed.length() > MAX_MODEL_LENGTH) {
      throw new ValidationException(
          "модель должна быть не длиннее " + MAX_MODEL_LENGTH + " символов");
    }
    return trimmed;
  }

  public static int validateHorsePower(int horsePower) {
    if (horsePower < MIN_HORSE_POWER || horsePower > MAX_HORSE_POWER) {
      throw new ValidationException(
          "мощность должна быть от " + MIN_HORSE_POWER + " до " + MAX_HORSE_POWER + " л.с.");
    }
    return horsePower;
  }

  public static int validateYear(int year) {
    int maxYear = maxYear();
    if (year < MIN_YEAR || year > maxYear) {
      throw new ValidationException("год должен быть от " + MIN_YEAR + " до " + maxYear);
    }
    return year;
  }

  public static int parseHorsePower(String text) {
    return validateHorsePower(parseInt(text, "значение мощности"));
  }

  public static int parseYear(String text) {
    return validateYear(parseInt(text, "значение года"));
  }

  private static int parseInt(String text, String fieldDescription) {
    String value = text == null ? "" : text.strip();
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new ValidationException(
          fieldDescription + " должно быть целым числом, получено: «" + value + "»");
    }
  }
}
