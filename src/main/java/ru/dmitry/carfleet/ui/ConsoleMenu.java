package ru.dmitry.carfleet.ui;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.CharacterCodingException;
import java.nio.file.AccessDeniedException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import ru.dmitry.carfleet.io.CarFileLoader;
import ru.dmitry.carfleet.io.LoadResult;
import ru.dmitry.carfleet.model.Car;
import ru.dmitry.carfleet.model.CarField;
import ru.dmitry.carfleet.service.CarService;
import ru.dmitry.carfleet.sort.SortAlgorithm;
import ru.dmitry.carfleet.sort.SortAlgorithmFactory;
import ru.dmitry.carfleet.sort.SortAlgorithmFactory.Algorithm;
import ru.dmitry.carfleet.sort.SortDirection;
import ru.dmitry.carfleet.validation.CarValidator;

public class ConsoleMenu {
  private static final String MENU = String.join(System.lineSeparator(),
      "",
      "===== АВТОПАРК =====",
      "1. Добавить автомобиль вручную",
      "2. Загрузить автомобили из файла",
      "3. Показать все автомобили",
      "4. Фильтр: автомобили мощнее N л.с.",
      "5. Преобразование: мощность в кВт",
      "6. Сортировка через Stream API (выбор поля)",
      "7. Сортировка собственным алгоритмом",
      "8. Операции с коллекцией: get / remove / size",
      "0. Выход");

  private static final String COLLECTION_MENU = String.join(System.lineSeparator(),
      "",
      "--- Операции с коллекцией (MyArrayList) ---",
      "1. Показать содержимое и size()",
      "2. Получить элемент по индексу: get(i)",
      "3. Удалить элемент по индексу: remove(i)",
      "0. Назад");

  private static final int EXIT = 0;
  private static final int ADD_MANUALLY = 1;
  private static final int LOAD_FROM_FILE = 2;
  private static final int SHOW_ALL = 3;
  private static final int FILTER_BY_POWER = 4;
  private static final int POWER_IN_KW = 5;
  private static final int STREAM_SORT = 6;
  private static final int CUSTOM_SORT = 7;
  private static final int COLLECTION_OPERATIONS = 8;
  private static final int COLLECTION_BACK = 0;
  private static final int COLLECTION_SHOW = 1;
  private static final int COLLECTION_GET = 2;
  private static final int COLLECTION_REMOVE = 3;
  private static final String EMPTY_LIST_MESSAGE = "Список пуст.";
  private static final String ROW_FORMAT = "%3d. %-30s %5d л.с. %6d г.%n";

  private final CarService service;
  private final CarFileLoader loader;
  private final ConsoleInput input;
  private final PrintStream out;

  public ConsoleMenu(CarService service, CarFileLoader loader, ConsoleInput input,
      PrintStream out) {
    this.service = service;
    this.loader = loader;
    this.input = input;
    this.out = out;
  }

  public void run() {
    try {
      boolean running = true;
      while (running) {
        out.println(MENU);
        running = handleChoice(input.readLine("Выберите пункт: "));
      }
      out.println("До свидания!");
    } catch (InputClosedException e) {
      out.println();
      out.println("Ввод завершён. До свидания!");
    }
  }

  private boolean handleChoice(String line) {
    int choice;
    try {
      choice = Integer.parseInt(line);
    } catch (NumberFormatException e) {
      out.println("Ошибка: «" + line + "» — не номер пункта. Введите число от 0 до 8.");
      return true;
    }
    if (choice == EXIT) {
      return false;
    }
    dispatch(choice);
    return true;
  }

  private void dispatch(int choice) {
    switch (choice) {
      case ADD_MANUALLY -> addManually();
      case LOAD_FROM_FILE -> loadFromFile();
      case SHOW_ALL -> runIfNotEmpty(this::showAll);
      case FILTER_BY_POWER -> runIfNotEmpty(this::filterByPower);
      case POWER_IN_KW -> runIfNotEmpty(this::showPowerInKw);
      case STREAM_SORT -> runIfNotEmpty(this::streamSort);
      case CUSTOM_SORT -> runIfNotEmpty(this::customSort);
      case COLLECTION_OPERATIONS -> runIfNotEmpty(this::collectionOperations);
      default -> out.println("Ошибка: пункта " + choice + " нет. Введите число от 0 до 8.");
    }
  }

  private void runIfNotEmpty(Runnable action) {
    if (service.isEmpty()) {
      out.println(EMPTY_LIST_MESSAGE);
    } else {
      action.run();
    }
  }

  private void addManually() {
    out.println("Добавление автомобиля.");
    String model = input.readValidated("Модель: ", CarValidator::validateModel);
    int horsePower = input.readValidated("Мощность, л.с. (" + CarValidator.MIN_HORSE_POWER
        + "–" + CarValidator.MAX_HORSE_POWER + "): ", CarValidator::parseHorsePower);
    int year = input.readValidated("Год выпуска (" + CarValidator.MIN_YEAR + "–"
        + CarValidator.maxYear() + "): ", CarValidator::parseYear);
    Car car = new Car(model, horsePower, year);
    service.add(car);
    out.println("Добавлен: " + car);
  }

  private void loadFromFile() {
    String line = input.readLine(
        "Путь к файлу (Enter — " + CarFileLoader.DEFAULT_PATH + "): ");
    String pathText = line.isEmpty() ? CarFileLoader.DEFAULT_PATH : line;
    try {
      LoadResult result = loader.load(Path.of(pathText));
      result.getErrors().forEach(error -> out.println("  Пропущено. " + error));
      service.addAll(result.getCars());
      out.printf("Загружено %d, пропущено %d.%n", result.loadedCount(), result.skippedCount());
    } catch (InvalidPathException e) {
      out.println("Ошибка: некорректный путь «" + pathText + "».");
    } catch (NoSuchFileException e) {
      out.println("Ошибка: файл не найден: " + pathText);
    } catch (AccessDeniedException e) {
      out.println("Ошибка: нет доступа к файлу: " + pathText);
    } catch (CharacterCodingException e) {
      out.println("Ошибка: файл не в кодировке UTF-8: " + pathText);
    } catch (IOException e) {
      out.println("Ошибка чтения файла " + pathText + ": " + e.getMessage());
    }
  }

  private void showAll() {
    out.println("Все автомобили:");
    printIndexed(service.getAll());
    out.println("Всего: " + service.size());
    service.averagePower().ifPresent(value ->
        out.println(String.format(Locale.ROOT, "Средняя мощность: %.1f л.с.", value)));
  }

  private void filterByPower() {
    int minPower = input.readInt("Показать автомобили мощнее N л.с., N = ");
    List<Car> result = service.filterByMinPower(minPower);
    if (result.isEmpty()) {
      out.println("Нет автомобилей мощнее " + minPower + " л.с.");
      return;
    }
    out.println("Автомобили мощнее " + minPower + " л.с. (" + result.size() + "):");
    printIndexed(result);
  }

  private void showPowerInKw() {
    out.println("Мощность в кВт (1 л.с. = " + CarService.KW_PER_HORSE_POWER + " кВт):");
    service.toPowerInKw().forEach(line -> out.println("  " + line));
  }

  private void streamSort() {
    CarField field = chooseField();
    SortDirection direction = chooseDirection();
    List<Car> sorted = service.sortedBy(direction.apply(field.getComparator()));
    out.println("Stream.sorted() по полю «" + field.getTitle() + "», "
        + direction.getTitle() + ":");
    printIndexed(sorted);
  }

  private void customSort() {
    Algorithm algorithmType = chooseAlgorithm();
    CarField field = chooseField();
    SortDirection direction = chooseDirection();
    SortAlgorithm<Car> algorithm = SortAlgorithmFactory.create(algorithmType);
    List<Car> sorted = service.sortWith(algorithm, field.getComparator(), direction);
    out.println("Алгоритм: " + algorithm.name() + "; поле «" + field.getTitle() + "», "
        + direction.getTitle() + ":");
    printIndexed(sorted);
    out.println("(Отсортирована копия, исходный порядок в коллекции не изменён.)");
  }

  private void collectionOperations() {
    showCollection();
    while (true) {
      out.println(COLLECTION_MENU);
      int choice = input.readInt("Выберите действие: ");
      if (choice == COLLECTION_BACK) {
        return;
      }
      handleCollectionChoice(choice);
      if (service.isEmpty()) {
        out.println(EMPTY_LIST_MESSAGE + " Возврат в главное меню.");
        return;
      }
    }
  }

  private void handleCollectionChoice(int choice) {
    switch (choice) {
      case COLLECTION_SHOW -> showCollection();
      case COLLECTION_GET -> getByIndex();
      case COLLECTION_REMOVE -> removeByIndex();
      default -> out.println("Ошибка: действия " + choice + " нет. Введите число от 0 до 3.");
    }
  }

  private void showCollection() {
    out.println("Содержимое коллекции:");
    printIndexed(service.getAll());
    out.println("size() = " + service.size());
  }

  private void getByIndex() {
    int index = input.readInt("Индекс (0.." + (service.size() - 1) + "): ");
    try {
      out.println("get(" + index + ") = " + service.get(index));
    } catch (IndexOutOfBoundsException e) {
      out.println("Ошибка: " + e.getMessage());
    }
  }

  private void removeByIndex() {
    int index = input.readInt("Индекс для удаления (0.." + (service.size() - 1) + "): ");
    try {
      out.println("remove(" + index + ") удалил: " + service.remove(index));
      out.println("Содержимое после удаления:");
      showCollection();
    } catch (IndexOutOfBoundsException e) {
      out.println("Ошибка: " + e.getMessage());
    }
  }

  private CarField chooseField() {
    return choose("Поле для сортировки:", CarField.values(), CarField::getTitle);
  }

  private SortDirection chooseDirection() {
    return choose("Направление:", SortDirection.values(), SortDirection::getTitle);
  }

  private Algorithm chooseAlgorithm() {
    return choose("Алгоритм сортировки:", Algorithm.values(), Algorithm::getTitle);
  }

  private <T> T choose(String header, T[] options, Function<T, String> title) {
    out.println(header);
    for (int i = 0; i < options.length; i++) {
      out.println("  " + (i + 1) + ". " + title.apply(options[i]));
    }
    return options[input.readIntInRange("Ваш выбор: ", 1, options.length) - 1];
  }

  private void printIndexed(List<Car> cars) {
    for (int i = 0; i < cars.size(); i++) {
      Car car = cars.get(i);
      out.printf(ROW_FORMAT, i, car.getModel(), car.getHorsePower(), car.getYear());
    }
  }
}
