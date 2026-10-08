package ru.dmitry.carfleet.sort;

import java.util.Comparator;

public enum SortDirection {
  ASC("по возрастанию"),
  DESC("по убыванию");

  private final String title;

  SortDirection(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public <T> Comparator<T> apply(Comparator<T> comparator) {
    return this == DESC ? comparator.reversed() : comparator;
  }
}
