package ru.dmitry.carfleet.sort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BubbleSort<T> implements SortAlgorithm<T> {
  @Override
  public void sort(List<T> list, Comparator<? super T> comparator) {
    Objects.requireNonNull(list, "list");
    Objects.requireNonNull(comparator, "comparator");
    int unsortedEnd = list.size() - 1;
    boolean swapped = true;
    while (swapped && unsortedEnd > 0) {
      swapped = false;
      for (int i = 0; i < unsortedEnd; i++) {
        if (comparator.compare(list.get(i), list.get(i + 1)) > 0) {
          swap(list, i, i + 1);
          swapped = true;
        }
      }
      unsortedEnd--;
    }
  }

  @Override
  public String name() {
    return "Пузырьковая сортировка (BubbleSort)";
  }

  private void swap(List<T> list, int first, int second) {
    T temp = list.get(first);
    list.set(first, list.get(second));
    list.set(second, temp);
  }
}
