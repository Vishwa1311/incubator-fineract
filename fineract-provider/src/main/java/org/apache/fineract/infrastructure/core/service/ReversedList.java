package org.apache.fineract.infrastructure.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/**
 * http://stackoverflow.com/questions/1098117/can-one-do-a-for-each-loop-in-java-in-reverse-order
 * @author Madhukar
 *
 * @param <T>
 */
public class ReversedList<T> implements Iterable<T> {

    private final List<T> original;

    public ReversedList(final List<T> original) {
        this.original = original;
    }

    @Override
    public Iterator<T> iterator() {
        final ListIterator<T> i = this.original.listIterator(this.original.size());

        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return i.hasPrevious();
            }

            @Override
            public T next() {
                return i.previous();
            }

            @Override
            public void remove() {
                i.remove();
            }
        };
    }

    public static <T> ReversedList<T> reversed(final List<T> original) {
        return new ReversedList<>(original);
    }
}
