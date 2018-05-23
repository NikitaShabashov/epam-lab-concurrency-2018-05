package lesson_2018_05_22.stack;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Блокирующий стек фиксированного размера.
 *
 * @param <T> Тип данных, хранящихся в стеке.
 */
@FieldDefaults(level= AccessLevel.PRIVATE)
public class BlockingStack<T> {

    volatile int current = 0;
    final Object[] arr;
    final Lock lock;
    final Condition notEmpty;
    final Condition notFull;

    /**
     * @param size Размер стека.
     * @param fair Честность доступа к элементам стека (при добавлении и удалении).
     */
    public BlockingStack(int size, boolean fair) {
        arr = new Object[size];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull =  lock.newCondition();
    }

    /**
     * Помещает элемент на вершину стека.
     * Если стек полон - блокирует поток.
     *
     * @param element Добавляемый элемент.
     */
    @SneakyThrows
    public void push(T element) {
        lock.lock();
        try {
            while (current == arr.length) notFull.await();
            arr[current++] = element;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Извлекает элемент из вершины стека.
     * Если стек пуст - блокирует поток.
     *
     * @return Извлеченный элемент.
     */
    @SneakyThrows
    @SuppressWarnings("unchecked")
    public T pop() {
        lock.lock();
        try {
            while (current == 0) notEmpty.await();
            T itemToReturn = (T) arr[--current];
            notFull.signal();
            return itemToReturn;
        } finally {
            lock.unlock();
        }
    }
}
