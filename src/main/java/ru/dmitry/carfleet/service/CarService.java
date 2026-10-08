package ru.dmitry.carfleet.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalDouble;
import ru.dmitry.carfleet.collection.MyArrayList;
import ru.dmitry.carfleet.model.Car;
import ru.dmitry.carfleet.sort.SortAlgorithm;
import ru.dmitry.carfleet.sort.SortDirection;

public class CarService {
  public static final double KW_PER_HORSE_POWER = 0.7355;
  private static final String KW_LINE_FORMAT = "%s — %d л.с. = %.1f кВт";

  private final List<Car> cars = new MyArrayList<>();

  public void add(Car car) {
    cars.add(Objects.requireNonNull(car, "car"));
  }

  public void addAll(Collection<Car> newCars) {
    newCars.forEach(this::add);
  }

  public Car get(int index) {
    return cars.get(index);
  }

  public Car remove(int index) {
    return cars.remove(index);
  }

  public int size() {
    return cars.size();
  }

  public boolean isEmpty() {
    return cars.isEmpty();
  }

  public List<Car> getAll() {
    return Collections.unmodifiableList(cars);
  }

  public List<Car> filterByMinPower(int minPower) {
    return cars.stream()
        .filter(car -> car.getHorsePower() > minPower)
        .toList();
  }

  public List<String> toPowerInKw() {
    return cars.stream()
        .map(CarService::formatPowerInKw)
        .toList();
  }

  public List<Car> sortedBy(Comparator<Car> comparator) {
    return cars.stream()
        .sorted(comparator)
        .toList();
  }

  public OptionalDouble averagePower() {
    return cars.stream()
        .mapToInt(Car::getHorsePower)
        .average();
  }

  public List<Car> sortWith(
      SortAlgorithm<Car> algorithm, Comparator<Car> comparator, SortDirection direction) {
    List<Car> copy = new MyArrayList<>(cars);
    algorithm.sort(copy, direction.apply(comparator));
    return copy;
  }

  public static double toKilowatts(int horsePower) {
    return horsePower * KW_PER_HORSE_POWER;
  }

  private static String formatPowerInKw(Car car) {
    return String.format(Locale.ROOT, KW_LINE_FORMAT,
        car.getModel(), car.getHorsePower(), toKilowatts(car.getHorsePower()));
  }
}
