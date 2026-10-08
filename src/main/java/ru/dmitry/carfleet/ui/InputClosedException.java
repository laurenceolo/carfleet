package ru.dmitry.carfleet.ui;

public class InputClosedException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public InputClosedException() {
    super("Поток ввода закрыт");
  }
}
