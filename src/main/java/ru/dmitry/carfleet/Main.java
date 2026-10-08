package ru.dmitry.carfleet;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import ru.dmitry.carfleet.io.CarFileLoader;
import ru.dmitry.carfleet.service.CarService;
import ru.dmitry.carfleet.ui.ConsoleInput;
import ru.dmitry.carfleet.ui.ConsoleMenu;

public final class Main {
  private Main() {
  }

  public static void main(String[] args) {
    PrintStream out = new PrintStream(
        new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
    ConsoleInput input = new ConsoleInput(System.in, out);
    ConsoleMenu menu = new ConsoleMenu(new CarService(), new CarFileLoader(), input, out);
    menu.run();
  }
}
