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

    public boolean swapAndApplyLIFO(final Consumer<T> consumer) {
        //Note: this is very performance critical method, so internals are inlined
        //Warning: do not change unless you clearly understand what are you doing!
        Node<T> head;

        do {
            head = this.head.get();
        } while (!this.head.compareAndSet(head, null));

        final var hasElements = head != null;

        while (head != null) {
            consumer.accept(head.element);
            head = head.nextNode;
        }

        return hasElements;
    }

    public boolean swapAndApplyFIFO(final Consumer<T> consumer) {
        //Note: this is very performance critical method, so internals are inlined
        //Warning: do not change unless you clearly understand what are you doing!
        Node<T> head;

        do {
            head = this.head.get();
        } while (!this.head.compareAndSet(head, null));

        Node<T> current = head;
        Node<T> prev = null;
        Node<T> next = null;

        while(current != null) {
            next = current.nextNode;
            current.nextNode = prev;
            prev = current;
            current = next;
        }

        final var hasElements = prev != null;

        while (prev != null) {
            consumer.accept(prev.element);
            prev = prev.nextNode;
        }

        return hasElements;
    }

    public Node<T> swapHead() {
        Node<T> head;

        do {
            head = this.head.get();
        } while (!this.head.compareAndSet(head, null));

        Node<T> current = head;
        Node<T> prev = null;
        Node<T> next;

        while(current != null) {
            next = current.nextNode;
            current.nextNode = prev;
            prev = current;
            current = next;
        }

        return prev;
    }


    public static final class Node<T> {
        public T element;
        public Node<T> nextNode;

        public Node(final T element) {
            this.element = element;
        }
    }
}
