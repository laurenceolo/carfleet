package ru.dmitry.carfleet.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class MyArrayListTest {
  private static MyArrayList<Integer> listOf(int... values) {
    MyArrayList<Integer> list = new MyArrayList<>();
    for (int value : values) {
      list.add(value);
    }
    return list;
  }

  @Test
  void newListIsEmpty() {
    MyArrayList<String> list = new MyArrayList<>();
    assertEquals(0, list.size());
    assertTrue(list.isEmpty());
    assertEquals(MyArrayList.DEFAULT_CAPACITY, list.capacity());
  }

  @Test
  void addThousandElementsGrowsArray() {
    MyArrayList<Integer> list = new MyArrayList<>();
    for (int i = 0; i < 1000; i++) {
      list.add(i);
    }
    assertEquals(1000, list.size());
    assertTrue(list.capacity() >= 1000);
    for (int i = 0; i < 1000; i++) {
      assertEquals(i, list.get(i));
    }
  }

  @Test
  void growsByOneAndHalf() {
    MyArrayList<Integer> list = new MyArrayList<>();
    IntStream.range(0, MyArrayList.DEFAULT_CAPACITY + 1).forEach(list::add);
    assertEquals(15, list.capacity());
  }

  @Test
  void addAtIndexShiftsElements() {
    MyArrayList<Integer> list = listOf(1, 3);
    list.add(1, 2);
    list.add(0, 0);
    list.add(4, 4);
    assertEquals(List.of(0, 1, 2, 3, 4), list);
  }

  @Test
  void addAtIndexWithGrowth() {
    MyArrayList<Integer> list = new MyArrayList<>();
    for (int i = 0; i < 100; i++) {
      list.add(0, i);
    }
    assertEquals(100, list.size());
    assertEquals(99, list.get(0));
    assertEquals(0, list.get(99));
  }

  @Test
  void setReplacesAndReturnsOld() {
    MyArrayList<Integer> list = listOf(1, 2, 3);
    assertEquals(2, list.set(1, 20));
    assertEquals(List.of(1, 20, 3), list);
  }

  @Test
  void removeFromStart() {
    MyArrayList<Integer> list = listOf(1, 2, 3, 4);
    assertEquals(1, list.remove(0));
    assertEquals(List.of(2, 3, 4), list);
  }

  @Test
  void removeFromMiddle() {
    MyArrayList<Integer> list = listOf(1, 2, 3, 4);
    assertEquals(3, list.remove(2));
    assertEquals(List.of(1, 2, 4), list);
  }

  @Test
  void removeFromEnd() {
    MyArrayList<Integer> list = listOf(1, 2, 3, 4);
    assertEquals(4, list.remove(3));
    assertEquals(List.of(1, 2, 3), list);
    assertEquals(3, list.size());
  }

  @Test
  void removeObjectUsesAbstractListLogic() {
    MyArrayList<String> list = new MyArrayList<>();
    list.add("a");
    list.add("b");
    assertTrue(list.remove("a"));
    assertEquals(List.of("b"), list);
  }

  @Test
  void invalidIndexThrows() {
    MyArrayList<Integer> list = listOf(1, 2, 3);
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(3));
    assertThrows(IndexOutOfBoundsException.class, () -> list.set(3, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(5));
    assertThrows(IndexOutOfBoundsException.class, () -> list.add(4, 0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.add(-1, 0));
  }

  @Test
  void exceptionMessageIsReadable() {
    MyArrayList<Integer> list = listOf(1, 2, 3);
    IndexOutOfBoundsException e =
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(7));
    assertTrue(e.getMessage().contains("7"));
    assertTrue(e.getMessage().contains("3"));
  }

  @Test
  void getOnEmptyListThrows() {
    MyArrayList<Integer> list = new MyArrayList<>();
    assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));
    assertThrows(IndexOutOfBoundsException.class, () -> list.remove(0));
  }

  @Test
  void streamWorks() {
    MyArrayList<Integer> list = listOf(1, 2, 3, 4, 5);
    assertEquals(List.of(4, 16), list.stream().filter(x -> x % 2 == 0).map(x -> x * x).toList());
    assertEquals(15, list.stream().mapToInt(Integer::intValue).sum());
  }

  @Test
  void forEachAndIteratorWork() {
    MyArrayList<Integer> list = listOf(1, 2, 3);
    int sum = 0;
    for (int value : list) {
      sum += value;
    }
    assertEquals(6, sum);
  }

  @Test
  void iteratorIsFailFast() {
    MyArrayList<Integer> list = listOf(1, 2, 3);
    Iterator<Integer> iterator = list.iterator();
    iterator.next();
    list.add(4);
    assertThrows(ConcurrentModificationException.class, iterator::next);
  }

  @Test
  void copyConstructorAndClear() {
    MyArrayList<Integer> copy = new MyArrayList<>(List.of(1, 2, 3));
    assertEquals(List.of(1, 2, 3), copy);
    copy.add(4);
    assertEquals(4, copy.size());
    copy.clear();
    assertTrue(copy.isEmpty());
  }

  @Test
  void negativeCapacityRejected() {
    assertThrows(IllegalArgumentException.class, () -> new MyArrayList<Integer>(-1));
  }

  @Test
  void zeroCapacityStillGrows() {
    MyArrayList<Integer> list = new MyArrayList<>(0);
    list.add(1);
    list.add(2);
    assertEquals(List.of(1, 2), list);
  }
}
