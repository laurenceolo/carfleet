package ru.dmitry.carfleet.sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.dmitry.carfleet.collection.MyArrayList;
import ru.dmitry.carfleet.model.Car;

class SortAlgorithmTest {
  private static final long SEED = 42L;

  static Stream<SortAlgorithm<Integer>> algorithms() {
    return Stream.of(new BubbleSort<>(), new MergeSort<>());
  }

  private static void assertSortedLikeListSort(
      SortAlgorithm<Integer> algorithm, List<Integer> input, SortDirection direction) {
    Comparator<Integer> comparator = direction.apply(Comparator.<Integer>naturalOrder());
    List<Integer> expected = new ArrayList<>(input);
    expected.sort(comparator);
    List<Integer> actual = new MyArrayList<>(input);
    algorithm.sort(actual, comparator);
    assertEquals(expected, actual, algorithm.name() + " " + direction);
  }

  private static void checkBothDirections(SortAlgorithm<Integer> algorithm, List<Integer> input) {
    for (SortDirection direction : SortDirection.values()) {
      assertSortedLikeListSort(algorithm, input, direction);
    }
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void emptyList(SortAlgorithm<Integer> algorithm) {
    checkBothDirections(algorithm, List.of());
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void singleElement(SortAlgorithm<Integer> algorithm) {
    checkBothDirections(algorithm, List.of(7));
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void duplicates(SortAlgorithm<Integer> algorithm) {
    checkBothDirections(algorithm, List.of(3, 1, 3, 2, 1, 3, 2, 2));
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void alreadySorted(SortAlgorithm<Integer> algorithm) {
    checkBothDirections(algorithm, List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void reverseOrder(SortAlgorithm<Integer> algorithm) {
    checkBothDirections(algorithm, List.of(9, 8, 7, 6, 5, 4, 3, 2, 1));
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void randomLists(SortAlgorithm<Integer> algorithm) {
    Random random = new Random(SEED);
    for (int size = 0; size < 60; size++) {
      List<Integer> input = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        input.add(random.nextInt(20) - 10);
      }
      checkBothDirections(algorithm, input);
    }
  }

  @ParameterizedTest
  @MethodSource("algorithms")
  void worksWithStandardArrayList(SortAlgorithm<Integer> algorithm) {
    List<Integer> list = new ArrayList<>(List.of(5, 3, 4, 1, 2));
    algorithm.sort(list, Comparator.naturalOrder());
    assertEquals(List.of(1, 2, 3, 4, 5), list);
  }

  static Stream<SortAlgorithmFactory.Algorithm> algorithmTypes() {
    return Stream.of(SortAlgorithmFactory.Algorithm.values());
  }

  @ParameterizedTest
  @MethodSource("algorithmTypes")
  void sortIsStableForCars(SortAlgorithmFactory.Algorithm type) {
    SortAlgorithm<Car> algorithm = SortAlgorithmFactory.create(type);
    Car first = new Car("Alpha", 150, 2018);
    Car second = new Car("Beta", 100, 2018);
    Car third = new Car("Gamma", 150, 2015);
    Car fourth = new Car("Delta", 150, 2020);
    List<Car> cars = new MyArrayList<>(List.of(first, second, third, fourth));
    algorithm.sort(cars, Car.BY_HORSE_POWER);
    assertEquals(List.of(second, first, third, fourth), cars);
    assertSame(first, cars.get(1));
  }

  @ParameterizedTest
  @MethodSource("algorithmTypes")
  void factoryReturnsMatchingImplementation(SortAlgorithmFactory.Algorithm type) {
    SortAlgorithm<Car> algorithm = SortAlgorithmFactory.create(type);
    Class<?> expected = type == SortAlgorithmFactory.Algorithm.BUBBLE
        ? BubbleSort.class : MergeSort.class;
    assertSame(expected, algorithm.getClass());
    assertTrue(!algorithm.name().isBlank());
  }
}
