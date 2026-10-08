package ru.dmitry.carfleet.collection;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

public class MyArrayList<E> extends AbstractList<E> implements RandomAccess {
  public static final int DEFAULT_CAPACITY = 10;

  private Object[] elements;
  private int size;

  public MyArrayList() {
    elements = new Object[DEFAULT_CAPACITY];
  }

  public MyArrayList(int initialCapacity) {
    if (initialCapacity < 0) {
      throw new IllegalArgumentException("Ёмкость не может быть отрицательной: " + initialCapacity);
    }
    elements = new Object[initialCapacity];
  }

  public MyArrayList(Collection<? extends E> source) {
    Object[] copy = source.toArray();
    elements = Arrays.copyOf(copy, Math.max(copy.length, DEFAULT_CAPACITY), Object[].class);
    size = copy.length;
  }

  @Override
  public boolean add(E element) {
    ensureCapacity(size + 1);
    elements[size] = element;
    size++;
    modCount++;
    return true;
  }

  @Override
  public void add(int index, E element) {
    checkPositionIndex(index);
    ensureCapacity(size + 1);
    System.arraycopy(elements, index, elements, index + 1, size - index);
    elements[index] = element;
    size++;
    modCount++;
  }

  @Override
  public E get(int index) {
    checkElementIndex(index);
    return elementAt(index);
  }

  @Override
  public E set(int index, E element) {
    checkElementIndex(index);
    E old = elementAt(index);
    elements[index] = element;
    return old;
  }

  @Override
  public E remove(int index) {
    checkElementIndex(index);
    E removed = elementAt(index);
    int tailLength = size - index - 1;
    if (tailLength > 0) {
      System.arraycopy(elements, index + 1, elements, index, tailLength);
    }
    size--;
    elements[size] = null;
    modCount++;
    return removed;
  }

  @Override
  public void clear() {
    Arrays.fill(elements, 0, size, null);
    size = 0;
    modCount++;
  }

  @Override
  public int size() {
    return size;
  }

  public int capacity() {
    return elements.length;
  }

  private void ensureCapacity(int minCapacity) {
    if (minCapacity > elements.length) {
      grow(minCapacity);
    }
  }

  private void grow(int minCapacity) {
    int oldCapacity = elements.length;
    int newCapacity = Math.max(oldCapacity + (oldCapacity >> 1), minCapacity);
    newCapacity = Math.max(newCapacity, DEFAULT_CAPACITY);
    Object[] newElements = new Object[newCapacity];
    System.arraycopy(elements, 0, newElements, 0, size);
    elements = newElements;
  }

  @SuppressWarnings("unchecked")
  private E elementAt(int index) {
    return (E) elements[index];
  }

  private void checkElementIndex(int index) {
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException(
          "Индекс " + index + " вне диапазона, размер списка: " + size);
    }
  }

  private void checkPositionIndex(int index) {
    if (index < 0 || index > size) {
      throw new IndexOutOfBoundsException(
          "Индекс " + index + " вне диапазона, размер списка: " + size);
    }
  }
}
