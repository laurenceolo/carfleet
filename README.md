# Автопарк — консольное Java-приложение

Итоговое задание по дисциплине «Специализированные инструменты прикладного программирования».

Приложение хранит список автомобилей (модель, мощность, год выпуска), позволяет добавлять их вручную
и из CSV-файла, обрабатывает коллекцию через Stream API, хранит данные в собственной коллекции
`MyArrayList` и сортирует их собственными алгоритмами (пузырьком и слиянием).

**Выполнены все три уровня:** 1 — базовый, 2 — собственная коллекция, 3 — собственные сортировки.

## Требования

- JDK 17 или новее;
- Maven 3.6+ (необязательно: в репозитории есть Maven Wrapper `./mvnw`, а также вариант сборки через `javac`).

## Запуск

### Через Maven

```bash
mvn -q package && java -jar target/carfleet.jar
```

Без установленного Maven то же самое через wrapper:

```bash
./mvnw -q package && java -jar target/carfleet.jar      # Windows: mvnw.cmd -q package
```

Тесты:

```bash
mvn test
```

### Без Maven (javac + java)

macOS / Linux:

```bash
mkdir -p out
javac -encoding UTF-8 --release 17 -d out $(find src/main/java -name "*.java")
java -cp out ru.dmitry.carfleet.Main
```

Windows (PowerShell):

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -encoding UTF-8 --release 17 -d out (Get-ChildItem -Recurse src/main/java -Filter *.java).FullName
java -cp out ru.dmitry.carfleet.Main
```

Запускать нужно из корня проекта, чтобы путь по умолчанию `data/cars.csv` находился.

> Программа выводит текст в UTF-8. В консоли Windows перед запуском выполните `chcp 65001`.

## Меню

```
===== АВТОПАРК =====
1. Добавить автомобиль вручную
2. Загрузить автомобили из файла
3. Показать все автомобили
4. Фильтр: автомобили мощнее N л.с.
5. Преобразование: мощность в кВт
6. Сортировка через Stream API (выбор поля)
7. Сортировка собственным алгоритмом
8. Операции с коллекцией: get / remove / size
0. Выход
```

- Нечисловой ввод и несуществующий пункт дают сообщение об ошибке, меню показывается снова.
- Конец ввода (Ctrl+D, в Windows Ctrl+Z, Enter) завершает программу без стектрейса.
- Пункты 3–8 на пустой коллекции выводят «Список пуст.».

## Формат CSV

Кодировка UTF-8, разделитель `;`, первая строка — заголовок:

```
model;horsePower;year
# строки, начинающиеся с #, и пустые строки пропускаются
Toyota Camry;150;2018
УАЗ Патриот;150;2021
```

| Поле         | Тип    | Ограничения                       |
|--------------|--------|-----------------------------------|
| `model`      | строка | непустая, не длиннее 50 символов  |
| `horsePower` | целое  | от 1 до 2000                      |
| `year`       | целое  | от 1886 до текущего года          |

Некорректная строка (не 3 поля, не число, значение вне диапазона) не прерывает загрузку: она пропускается
с сообщением «Строка N: причина», в конце выводится «Загружено X, пропущено Y».

Файлы для демонстрации:

- `data/cars.csv` — 12 корректных автомобилей;
- `data/cars_with_errors.csv` — 2 корректные строки и 10 намеренно битых.

## Где что искать: критерий → класс/метод

Пакет: `ru.dmitry.carfleet` (каталог `src/main/java/ru/dmitry/carfleet`).

### Уровень 1 — базовый

| Критерий | Где реализовано |
|---|---|
| Класс-модель, неизменяемый, `equals/hashCode/toString` | `model.Car` |
| `Comparable<Car>` по году, `Comparator`-константы | `Car.compareTo`, `Car.BY_MODEL`, `Car.BY_HORSE_POWER`, `Car.BY_YEAR`; выбор поля — `model.CarField` |
| Валидация полей, своё исключение | `validation.CarValidator`, `validation.ValidationException` |
| Меню в цикле, обработка неверного ввода и EOF | `ui.ConsoleMenu.run`, `handleChoice`, `dispatch`; `ui.InputClosedException` |
| Безопасное чтение чисел, перехват `NumberFormatException`, повтор | `ui.ConsoleInput.readInt`, `readIntInRange`, `readValidated` |
| Ручной ввод с повтором поля и причиной ошибки | `ConsoleMenu.addManually` |
| Чтение файла через try-with-resources, UTF-8 | `io.CarFileLoader.load` |
| Обработка `NoSuchFileException`, `AccessDeniedException`, `IOException` | `ConsoleMenu.loadFromFile` |
| Пропуск битых строк, итог «Загружено X, пропущено Y» | `CarFileLoader.load`, `CarFileLoader.parseLine`, `io.LoadResult` |
| Stream `filter` | `service.CarService.filterByMinPower` |
| Stream `map` (л.с. → кВт, константа 0.7355) | `CarService.toPowerInKw`, `CarService.KW_PER_HORSE_POWER` |
| Stream `sorted` с выбором поля | `CarService.sortedBy`, `ConsoleMenu.streamSort` |
| Дополнительно: средняя мощность `mapToInt().average()` | `CarService.averagePower` (пункт 3) |
| `Main` только создаёт объекты и запускает меню | `Main.main` |

### Уровень 2 — собственная коллекция

| Критерий | Где реализовано |
|---|---|
| `MyArrayList<E> extends AbstractList<E>` | `collection.MyArrayList` |
| `Object[] elements`, `int size`, начальная ёмкость 10 | поля `elements`, `size`, константа `DEFAULT_CAPACITY` |
| Расширение в 1.5 раза | `MyArrayList.grow` (вызывается из `ensureCapacity`) |
| `add(E)`, `add(int, E)` со сдвигом через `System.arraycopy` | `MyArrayList.add` |
| `get`, `set`, `remove(int)` со сдвигом и обнулением хвоста | `MyArrayList.get`, `set`, `remove` |
| `size()` | `MyArrayList.size` |
| Проверка индекса, `IndexOutOfBoundsException` с понятным сообщением | `checkElementIndex`, `checkPositionIndex` |
| `modCount` (fail-fast итераторы) | `add`, `remove`, `clear`; тест `iteratorIsFailFast` |
| Данные хранятся в `MyArrayList` | `CarService.cars = new MyArrayList<>()` |
| Пункт меню 8: вывод с индексами, `size`, `get`, `remove`, повторный вывод | `ConsoleMenu.collectionOperations`, `getByIndex`, `removeByIndex` |

### Уровень 3 — собственные сортировки и полиморфизм

| Критерий | Где реализовано |
|---|---|
| Интерфейс `SortAlgorithm<T>` | `sort.SortAlgorithm` |
| Пузырьковая сортировка с ранним выходом | `sort.BubbleSort` (флаг `swapped`) |
| Рекурсивная устойчивая сортировка слиянием | `sort.MergeSort` (`mergeSort`, `merge`) |
| Работа через `get/set` списка | обе реализации; тест `worksWithStandardArrayList` |
| `enum SortDirection { ASC, DESC }`, DESC через `reversed()` | `sort.SortDirection.apply` |
| Выбор реализации во время выполнения | `sort.SortAlgorithmFactory.create` |
| Сортируется копия, исходный порядок сохраняется | `CarService.sortWith`, `ConsoleMenu.customSort` |

### Тесты (`src/test/java`, JUnit 5)

| Класс | Что проверяет |
|---|---|
| `collection.MyArrayListTest` | добавление 1000 элементов с расширением, коэффициент 1.5, `add(int, E)`, `get`, `set`, `remove` из начала/середины/конца, `size`, исключения на неверный индекс, stream, for-each, fail-fast |
| `sort.SortAlgorithmTest` | обе сортировки: пустой список, один элемент, дубликаты, отсортированный, обратный порядок, случайные списки, ASC/DESC — сверка с `List.sort`; устойчивость; фабрика |
| `validation.CarValidatorTest` | граничные значения всех полей, разбор чисел, `equals/hashCode/compareTo` |
| `io.CarFileLoaderTest` | валидный файл, битые строки (счётчики), BOM, пустой файл, несуществующий файл, файлы из `data/` |
| `service.CarServiceTest` | filter, map (формат строки кВт), sorted, сортировка копии, средняя мощность |

## Структура проекта

```
├── pom.xml
├── mvnw, mvnw.cmd, .mvn/          Maven Wrapper
├── data/
│   ├── cars.csv
│   └── cars_with_errors.csv
└── src/
    ├── main/java/ru/dmitry/carfleet/
    │   ├── Main.java
    │   ├── model/        Car, CarField
    │   ├── validation/   CarValidator, ValidationException
    │   ├── collection/   MyArrayList
    │   ├── io/           CarFileLoader, LoadResult
    │   ├── service/      CarService
    │   ├── sort/         SortAlgorithm, BubbleSort, MergeSort, SortDirection, SortAlgorithmFactory
    │   └── ui/           ConsoleMenu, ConsoleInput, InputClosedException
    └── test/java/ru/dmitry/carfleet/   тесты JUnit 5
```

## Пример сессии

Ввод пользователя показан после подсказок `Выберите пункт:` и подобных. Повторы меню сокращены.

```
===== АВТОПАРК =====
...
Выберите пункт: abc
Ошибка: «abc» — не номер пункта. Введите число от 0 до 8.
...
Выберите пункт: 3
Список пуст.
...
Выберите пункт: 2
Путь к файлу (Enter — data/cars.csv):
Загружено 12, пропущено 0.
...
Выберите пункт: 2
Путь к файлу (Enter — data/cars.csv): data/cars_with_errors.csv
  Пропущено. Строка 5: ожидалось 3 поля через «;», получено 2
  Пропущено. Строка 6: значение мощности должно быть целым числом, получено: «сто сорок»
  Пропущено. Строка 7: модель не должна быть пустой
  Пропущено. Строка 8: мощность должна быть от 1 до 2000 л.с.
  Пропущено. Строка 9: мощность должна быть от 1 до 2000 л.с.
  Пропущено. Строка 10: год должен быть от 1886 до 2026
  Пропущено. Строка 11: год должен быть от 1886 до 2026
  Пропущено. Строка 12: ожидалось 3 поля через «;», получено 4
  Пропущено. Строка 14: модель должна быть не длиннее 50 символов
  Пропущено. Строка 15: значение года должно быть целым числом, получено: «abc»
Загружено 2, пропущено 10.
...
Выберите пункт: 2
Путь к файлу (Enter — data/cars.csv): nope.csv
Ошибка: файл не найден: nope.csv
...
Выберите пункт: 1
Добавление автомобиля.
Модель:
Ошибка: модель не должна быть пустой — попробуйте ещё раз.
Модель: Lada Niva Legend
Мощность, л.с. (1–2000): abc
Ошибка: значение мощности должно быть целым числом, получено: «abc» — попробуйте ещё раз.
Мощность, л.с. (1–2000): 83
Год выпуска (1886–2026): 1800
Ошибка: год должен быть от 1886 до 2026 — попробуйте ещё раз.
Год выпуска (1886–2026): 2021
Добавлен: Lada Niva Legend, 83 л.с., 2021 г.
...
Выберите пункт: 4
Показать автомобили мощнее N л.с., N = 300
Автомобили мощнее 300 л.с. (4):
  0. BMW M5                           625 л.с.   2021 г.
  1. Tesla Model S                    670 л.с.   2022 г.
  2. Ford Mustang GT                  450 л.с.   2019 г.
  3. Porsche 911 Carrera              385 л.с.   2023 г.
...
Выберите пункт: 5
Мощность в кВт (1 л.с. = 0.7355 кВт):
  Toyota Camry — 150 л.с. = 110.3 кВт
  Lada Vesta — 106 л.с. = 78.0 кВт
  ...
...
Выберите пункт: 7
Алгоритм сортировки:
  1. Пузырьковая сортировка
  2. Сортировка слиянием
Ваш выбор: 2
Поле для сортировки:
  1. модель
  2. мощность
  3. год выпуска
Ваш выбор: 3
Направление:
  1. по возрастанию
  2. по убыванию
Ваш выбор: 2
Алгоритм: Сортировка слиянием (MergeSort); поле «год выпуска», по убыванию:
  0. Porsche 911 Carrera              385 л.с.   2023 г.
  1. Tesla Model S                    670 л.с.   2022 г.
  ...
(Отсортирована копия, исходный порядок в коллекции не изменён.)
...
Выберите пункт: 8
...
Выберите действие: 2
Индекс (0..14): 99
Ошибка: Индекс 99 вне диапазона, размер списка: 15
Выберите действие: 3
Индекс для удаления (0..14): 1
remove(1) удалил: Lada Vesta, 106 л.с., 2020 г.
Содержимое после удаления:
...
size() = 14
...
Выберите пункт: 0
До свидания!
```
