package ru.dmitry.carfleet.model;

import java.util.Comparator;

public enum CarField {
  MODEL("модель", Car.BY_MODEL),
  HORSE_POWER("мощность", Car.BY_HORSE_POWER),
  YEAR("год выпуска", Car.BY_YEAR);

  private final String title;
  private final Comparator<Car> comparator;

  CarField(String title, Comparator<Car> comparator) {
    this.title = title;
    this.comparator = comparator;
  }

  public String getTitle() {
    return title;
  }

  public Comparator<Car> getComparator() {
    return comparator;
  }
}
