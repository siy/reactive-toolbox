/*
 * Copyright (c) 2020 Sergiy Yevtushenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.reactivetoolbox.core.lang.collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.functional.Option;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.lang.collection.List.list;

class ListTest {
    @Test
    void listCanBeCreated() {
        final ArrayList<Integer> arrayList = new ArrayList<>();
        final var list = list(1, 2, 3);

        list.apply(arrayList::add);

        assertEquals(Arrays.asList(1, 2, 3), arrayList);
        assertTrue(list.equals(list));
        assertFalse(list.equals((Object) null));
        assertEquals(list(1, 2, 3), list);
        assertEquals("List(1,2,3)", list.toString());
        assertEquals(list(1, 2, 3).hashCode(), list.hashCode());
        assertNotEquals(0, list.hashCode());
        assertFalse(list.equals(""));
    }

    @Test
    void listCanBeStreamedAndCollected() {
        final var list = list(1, 2, 3, 4).stream()
                                         .map(v -> v * 2)
                                         .collect(List.toList());

        assertEquals(list(2, 4, 6, 8), list);
    }

    @Test
    void listsCanBeMerged() {
        final var list1 = list(1, 2, 3);
        final var list2 = list(4, 5, 6);

        assertEquals(list(1, 2, 3, 4, 5, 6), list1.append(list2));
        assertEquals(list(1, 2, 3, 4, 5, 6), list2.prepend(list1));
    }

    @Test
    void listCanBeSorted() {
        final var list = list(3, 2, 1);

        assertEquals(list(1, 2, 3), list.sort((a, b) -> a - b));
    }

    @Test
    void listCanBeSplitByPredicate() {
        final var result = list(4, 3, 2, 1).splitBy(e -> e >= 3);

        result.applyLeft(left -> assertEquals(list(2, 1), left))
              .applyRight(right -> assertEquals(list(4, 3), right));
    }

    @Test
    void elementsCanBeShuffled() {
        final var result = list(1, 2, 3, 4, 5, 6, 7);

        assertEquals(list(1, 3, 7, 5, 2, 6, 4), result.shuffle(new Random(0)));
        assertEquals(list(6, 3, 1, 5, 7, 2, 4), result.shuffle(new Random(1)));
    }

    @Test
    void mapNProvidesIndexes() {
        final var list = list(3, 2, 1).mapN(Integer::sum);

        assertEquals(list(3, 3, 3), list);
    }

    @Test
    void applyNProvidesIndexes() {
        final var list = list(3, 2, 1);
        final int[] values = new int[list.size()];

        list.applyN((n, v) -> values[n] = v);

        assertEquals(3, values[0]);
        assertEquals(2, values[1]);
        assertEquals(1, values[2]);
    }

    @Test
    void mapIsApplied() {
        final var list = list(3, 2, 1).map(v -> v + 1);

        assertEquals(list(4, 3, 2), list);
    }

    @Test
    void emptyListIsEmpty() {
        final var counter = new AtomicInteger(0);

        list().apply(v -> counter.incrementAndGet());
        list().applyN((n, v) -> counter.incrementAndGet());

        assertEquals(0, counter.get());
    }

    @Test
    void firstAndLastElementsCanBeRetrieved() {
        final var list = list(1, 2, 3);

        list.first()
            .whenPresent(v -> assertEquals(1, v))
            .whenEmpty(Assertions::fail);
        list.last()
            .whenPresent(v -> assertEquals(3, v))
            .whenEmpty(Assertions::fail);
    }

    @Test
    void sublistCanBeObtained() {
        final var list = list(1, 2, 3);

        assertEquals(list(1), list.first(1));
        assertEquals(list(1, 2), list.first(2));
        assertEquals(list(1, 2, 3), list.first(3));
        assertEquals(list(1, 2, 3), list.first(4));
        assertEquals(list(), list.first(-1));
    }

    @Test
    void emptyListRemainsEmpty() {
        assertEquals(list(), list().first(1));
        assertEquals(list(), list().filter(v -> true));
        assertEquals(Option.empty(), list().first());
    }

    @Test
    void listCanBeFiltered() {
        final var list = list(1, 2, 3);

        assertEquals(list(1), list.filter(v -> v < 2));
        assertEquals(list(1, 2), list.filter(v -> v < 3));
        assertEquals(list(1, 2, 3), list.filter(v -> v < 4));
        assertEquals(list(1, 2, 3), list.filter(v -> v > 0));
        assertEquals(list(), list.filter(v -> v < 0));
    }

    @Test
    void listCanBeFoldedLeft() {
        final var list = list(1, 2, 3);

        assertEquals(6, list.foldLeft(0, value -> sum -> sum + value));
        assertEquals(" 1 2 3", list.foldLeft("", value -> sum -> sum + " " + value));
    }

    @Test
    void listCanBeFoldedRight() {
        final var list = list(1, 2, 3);

        assertEquals(6, list.foldRight(0, value -> sum -> sum + value)); //order does not matter here
        assertEquals(" 3 2 1", list.foldRight("", value -> sum -> sum + " " + value));
    }
}