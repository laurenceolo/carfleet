package ru.dmitry.carfleet.sort;

import java.util.Comparator;
import java.util.List;

public interface SortAlgorithm<T> {
  void sort(List<T> list, Comparator<? super T> comparator);

  String name();
}
