package org.reactivetoolbox.io.async.util;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class StackingCollector<T> {
    private final AtomicReference<Node<T>> head = new AtomicReference<>();

    private StackingCollector() {}

    public static <T> StackingCollector<T> stackingCollector() {
        return new StackingCollector<>();
    }

    public void push(final T action) {
        final var newHead = new Node<>(action);
        Node<T> oldHead;

        do {
            oldHead = head.get();
            newHead.nextNode = oldHead;
        } while (!head.compareAndSet(oldHead, newHead));
    }

    public boolean swapAndApply(final Consumer<T> consumer) {
        Node<T> savedHead;

        do {
            savedHead = head.get();
        } while (!head.compareAndSet(savedHead, null));

        final var hasElements = savedHead != null;

        while (savedHead != null) {
            consumer.accept(savedHead.element);
            savedHead = savedHead.nextNode;
        }

        return hasElements;
    }

    static final class Node<T> {
        public T element;
        public Node<T> nextNode;

        public Node(final T element) {
            this.element = element;
        }
    }
}
