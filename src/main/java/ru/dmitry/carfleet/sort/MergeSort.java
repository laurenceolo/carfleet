package ru.dmitry.carfleet.sort;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import ru.dmitry.carfleet.collection.MyArrayList;

public class MergeSort<T> implements SortAlgorithm<T> {
  private static final int MIN_SORTABLE_SIZE = 2;

  @Override
  public void sort(List<T> list, Comparator<? super T> comparator) {
    Objects.requireNonNull(list, "list");
    Objects.requireNonNull(comparator, "comparator");
    if (list.size() < MIN_SORTABLE_SIZE) {
      return;
    }
    List<T> buffer = new MyArrayList<>(list);
    mergeSort(list, buffer, 0, list.size(), comparator);
  }

  @Override
  public String name() {
    return "Сортировка слиянием (MergeSort)";
  }

  private void mergeSort(
      List<T> list, List<T> buffer, int from, int to, Comparator<? super T> comparator) {
    if (to - from < MIN_SORTABLE_SIZE) {
      return;
    }
    int middle = (from + to) >>> 1;
    mergeSort(list, buffer, from, middle, comparator);
    mergeSort(list, buffer, middle, to, comparator);
    merge(list, buffer, from, middle, to, comparator);
  }

  private void merge(
      List<T> list, List<T> buffer, int from, int middle, int to,
      Comparator<? super T> comparator) {
    for (int i = from; i < to; i++) {
      buffer.set(i, list.get(i));
    }
    int left = from;
    int right = middle;
    for (int target = from; target < to; target++) {
      boolean takeLeft = right >= to
          || (left < middle && comparator.compare(buffer.get(left), buffer.get(right)) <= 0);
      list.set(target, takeLeft ? buffer.get(left++) : buffer.get(right++));
    }
  }
}
