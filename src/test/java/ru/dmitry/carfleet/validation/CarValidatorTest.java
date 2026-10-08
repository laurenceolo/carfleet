package ru.dmitry.carfleet.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Year;
import org.junit.jupiter.api.Test;
import ru.dmitry.carfleet.model.Car;

class CarValidatorTest {
  private static final int CURRENT_YEAR = Year.now().getValue();

  @Test
  void modelBoundaries() {
    assertEquals("A", CarValidator.validateModel("A"));
    assertEquals("X".repeat(50), CarValidator.validateModel("X".repeat(50)));
    assertEquals("Lada", CarValidator.validateModel("  Lada  "));
    assertThrows(ValidationException.class, () -> CarValidator.validateModel("X".repeat(51)));
    assertThrows(ValidationException.class, () -> CarValidator.validateModel(""));
    assertThrows(ValidationException.class, () -> CarValidator.validateModel("   "));
    assertThrows(ValidationException.class, () -> CarValidator.validateModel(null));
  }

  @Test
  void horsePowerBoundaries() {
    assertEquals(1, CarValidator.validateHorsePower(1));
    assertEquals(2000, CarValidator.validateHorsePower(2000));
    assertThrows(ValidationException.class, () -> CarValidator.validateHorsePower(0));
    assertThrows(ValidationException.class, () -> CarValidator.validateHorsePower(2001));
    assertThrows(ValidationException.class, () -> CarValidator.validateHorsePower(-100));
  }

  @Test
  void yearBoundaries() {
    assertEquals(1886, CarValidator.validateYear(1886));
    assertEquals(CURRENT_YEAR, CarValidator.validateYear(CURRENT_YEAR));
    assertThrows(ValidationException.class, () -> CarValidator.validateYear(1885));
    assertThrows(ValidationException.class, () -> CarValidator.validateYear(CURRENT_YEAR + 1));
  }

  @Test
  void yearMessageContainsRange() {
    ValidationException e =
        assertThrows(ValidationException.class, () -> CarValidator.validateYear(1800));
    assertEquals("год должен быть от 1886 до " + CURRENT_YEAR, e.getMessage());
  }

  @Test
  void parsingRejectsNonNumbers() {
    assertEquals(150, CarValidator.parseHorsePower(" 150 "));
    assertEquals(2020, CarValidator.parseYear("2020"));
    assertThrows(ValidationException.class, () -> CarValidator.parseHorsePower("сто"));
    assertThrows(ValidationException.class, () -> CarValidator.parseHorsePower(""));
    assertThrows(ValidationException.class, () -> CarValidator.parseYear("20.5"));
    assertThrows(ValidationException.class, () -> CarValidator.parseYear(null));
    assertThrows(ValidationException.class, () -> CarValidator.parseYear("99999999999"));
  }

  @Test
  void carConstructorValidates() {
    assertThrows(ValidationException.class, () -> new Car("", 100, 2000));
    assertThrows(ValidationException.class, () -> new Car("Lada", 0, 2000));
    assertThrows(ValidationException.class, () -> new Car("Lada", 100, 1885));
  }

  @Test
  void carEqualsHashCodeAndCompareTo() {
    Car a = new Car("Lada", 100, 2000);
    Car b = new Car("Lada", 100, 2000);
    Car newer = new Car("Kia", 100, 2010);
    assertEquals(a, b);
    assertEquals(a.hashCode(), b.hashCode());
    assertTrue(a.compareTo(newer) < 0);
    assertTrue(newer.compareTo(a) > 0);
  }
}
