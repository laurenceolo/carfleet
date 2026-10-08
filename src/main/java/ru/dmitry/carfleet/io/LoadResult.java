package ru.dmitry.carfleet.io;

import java.util.Collections;
import java.util.List;
import ru.dmitry.carfleet.model.Car;

public final class LoadResult {
  private final List<Car> cars;
  private final List<String> errors;

  public LoadResult(List<Car> cars, List<String> errors) {
    this.cars = Collections.unmodifiableList(cars);
    this.errors = Collections.unmodifiableList(errors);
  }

  public List<Car> getCars() {
    return cars;
  }

  public List<String> getErrors() {
    return errors;
  }

  public int loadedCount() {
    return cars.size();
  }

  public int skippedCount() {
    return errors.size();
  }
}
