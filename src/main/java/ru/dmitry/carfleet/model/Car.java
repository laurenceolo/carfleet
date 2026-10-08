package ru.dmitry.carfleet.model;

import java.util.Comparator;
import java.util.Objects;
import ru.dmitry.carfleet.validation.CarValidator;

public final class Car implements Comparable<Car> {
  public static final Comparator<Car> BY_MODEL =
      Comparator.comparing(Car::getModel, String.CASE_INSENSITIVE_ORDER)
          .thenComparing(Car::getModel);

  public static final Comparator<Car> BY_HORSE_POWER = Comparator.comparingInt(Car::getHorsePower);
  public static final Comparator<Car> BY_YEAR = Comparator.comparingInt(Car::getYear);

  private final String model;
  private final int horsePower;
  private final int year;

  public Car(String model, int horsePower, int year) {
    this.model = CarValidator.validateModel(model);
    this.horsePower = CarValidator.validateHorsePower(horsePower);
    this.year = CarValidator.validateYear(year);
  }

  public String getModel() {
    return model;
  }

  public int getHorsePower() {
    return horsePower;
  }

  public int getYear() {
    return year;
  }

  @Override
  public int compareTo(Car other) {
    return Integer.compare(year, other.year);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Car)) {
      return false;
    }
    Car other = (Car) o;
    return horsePower == other.horsePower && year == other.year && model.equals(other.model);
  }

  @Override
  public int hashCode() {
    return Objects.hash(model, horsePower, year);
  }

  @Override
  public String toString() {
    return model + ", " + horsePower + " л.с., " + year + " г.";
  }
}
