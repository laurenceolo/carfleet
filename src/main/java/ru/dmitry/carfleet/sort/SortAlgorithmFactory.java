package ru.dmitry.carfleet.sort;

public final class SortAlgorithmFactory {
  public enum Algorithm {
    BUBBLE("Пузырьковая сортировка"),
    MERGE("Сортировка слиянием");

    private final String title;

    Algorithm(String title) {
      this.title = title;
    }

    public String getTitle() {
      return title;
    }
  }

  private SortAlgorithmFactory() {
  }

  public static <T> SortAlgorithm<T> create(Algorithm algorithm) {
    return switch (algorithm) {
      case BUBBLE -> new BubbleSort<>();
      case MERGE -> new MergeSort<>();
    };
  }
}
