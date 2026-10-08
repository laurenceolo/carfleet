package ru.dmitry.carfleet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.dmitry.carfleet.model.Car;
import ru.dmitry.carfleet.sort.MergeSort;
import ru.dmitry.carfleet.sort.SortDirection;

class CarServiceTest {
  private final Car camry = new Car("Toyota Camry", 150, 2018);
  private final Car vesta = new Car("Lada Vesta", 106, 2020);
  private final Car m5 = new Car("BMW M5", 625, 2021);
  private CarService service;

  @BeforeEach
  void setUp() {
    service = new CarService();
    service.addAll(List.of(camry, vesta, m5));
  }

  @Test
  void filterIsStrict() {
    assertEquals(List.of(camry, m5), service.filterByMinPower(106));
    assertEquals(List.of(m5), service.filterByMinPower(150));
  }

  @Test
  void convertsToKilowatts() {
    assertEquals("Toyota Camry — 150 л.с. = 110.3 кВт", service.toPowerInKw().get(0));
  }

  @Test
  void streamSortDoesNotChangeOriginal() {
    assertEquals(List.of(m5, vesta, camry), service.sortedBy(Car.BY_MODEL));
    assertEquals(List.of(camry, vesta, m5), service.getAll());
  }

  @Test
  void customSortDoesNotChangeOriginal() {
    List<Car> sorted = service.sortWith(new MergeSort<>(), Car.BY_YEAR, SortDirection.DESC);
    assertEquals(List.of(m5, vesta, camry), sorted);
    assertEquals(List.of(camry, vesta, m5), service.getAll());
  }

  @Test
  void averagePowerOfEmptyIsEmpty() {
    assertTrue(new CarService().averagePower().isEmpty());
    assertEquals((150 + 106 + 625) / 3.0, service.averagePower().getAsDouble(), 1e-9);
  }
}
