package ru.dmitry.carfleet.ui;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.function.Function;
import ru.dmitry.carfleet.validation.ValidationException;

public class ConsoleInput {
  private final Scanner scanner;
  private final PrintStream out;

  public ConsoleInput(InputStream in, PrintStream out) {
    this.scanner = new Scanner(in, StandardCharsets.UTF_8);
    this.out = out;
  }

  public String readLine(String prompt) {
    out.print(prompt);
    out.flush();
    if (!scanner.hasNextLine()) {
      throw new InputClosedException();
    }
    return scanner.nextLine().strip();
  }

  public int readInt(String prompt) {
    while (true) {
      String line = readLine(prompt);
      try {
        return Integer.parseInt(line);
      } catch (NumberFormatException e) {
        out.println("Ошибка: «" + line + "» не является целым числом. Попробуйте ещё раз.");
      }
    }
  }

  public int readIntInRange(String prompt, int min, int max) {
    while (true) {
      int value = readInt(prompt);
      if (value >= min && value <= max) {
        return value;
      }
      out.println("Ошибка: введите число от " + min + " до " + max + ".");
    }
  }

  public <T> T readValidated(String prompt, Function<String, T> parser) {
    while (true) {
      String line = readLine(prompt);
      try {
        return parser.apply(line);
      } catch (ValidationException e) {
        out.println("Ошибка: " + e.getMessage() + " — попробуйте ещё раз.");
      }
    }
  }
}
