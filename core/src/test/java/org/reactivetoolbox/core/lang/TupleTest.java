package org.reactivetoolbox.core.lang;

import org.junit.jupiter.api.Test;
import org.reactivetoolbox.core.lang.Tuple.Tuple0;
import org.reactivetoolbox.core.lang.Tuple.Tuple1;
import org.reactivetoolbox.core.lang.Tuple.Tuple2;
import org.reactivetoolbox.core.lang.Tuple.Tuple3;
import org.reactivetoolbox.core.lang.Tuple.Tuple4;
import org.reactivetoolbox.core.lang.Tuple.Tuple5;
import org.reactivetoolbox.core.lang.Tuple.Tuple6;
import org.reactivetoolbox.core.lang.Tuple.Tuple7;
import org.reactivetoolbox.core.lang.Tuple.Tuple8;
import org.reactivetoolbox.core.lang.Tuple.Tuple9;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.reactivetoolbox.core.lang.Tuple.tuple;

class TupleTest {
    @Test
    void tuple0CanBeCreatedAndMapped() {
        final var tuple = tuple();

        assertEquals(0, Tuple0.size());
        assertEquals(1, tuple.map(() -> 1));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple().equals(tuple));
        assertEquals(tuple().hashCode(), tuple.hashCode());
        assertEquals(tuple().toString(), tuple.toString());
        assertNotEquals(tuple(1), tuple);
    }

    @Test
    void tuple1CanBeCreatedAndMapped() {
        final var tuple = tuple(10);

        assertEquals(1, Tuple1.size());
        assertEquals("10", tuple.map((p1) -> "" + p1));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10).equals(tuple));
        assertEquals(tuple(10).hashCode(), tuple.hashCode());
        assertEquals(tuple(10).toString(), tuple.toString());
        assertNotEquals(tuple(11).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple2CanBeCreatedAndMapped() {
        final var tuple = tuple(10, "key");

        assertEquals(2, Tuple2.size());
        assertEquals("10key", tuple.map((p1, p2) -> "" + p1 + p2));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key").equals(tuple));
        assertEquals(tuple(10, "key").hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key").toString(), tuple.toString());
        assertNotEquals(tuple(10, "key-").toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple3CanBeCreatedAndMapped() {
        final var uuid = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid);

        assertEquals(3, Tuple3.size());
        assertEquals("10key" + uuid.toString(), tuple.map((p1, p2, p3) -> "" + p1 + p2 + p3));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid).equals(tuple));
        assertEquals(tuple(10, "key", uuid).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", UUID.randomUUID()).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple4CanBeCreatedAndMapped() {
        final var uuid = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid, 32L);

        assertEquals("10key" + uuid.toString() + "32", tuple.map((p1, p2, p3, p4) -> "" + p1 + p2 + p3 + p4));

        assertEquals(4, Tuple4.size());
        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid, 32L).equals(tuple));
        assertEquals(tuple(10, "key", uuid, 32L).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid, 32L).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid, 33L).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple5CanBeCreatedAndMapped() {
        final var uuid = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid, 32L, 45);

        assertEquals("10key" + uuid.toString() + "3245", tuple.map((p1, p2, p3, p4, p5) -> "" + p1 + p2 + p3 + p4 + p5));

        assertEquals(5, Tuple5.size());
        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid, 32L, 45).equals(tuple));
        assertEquals(tuple(10, "key", uuid, 32L, 45).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid, 32L, 45).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid, 32L, 46).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple6CanBeCreatedAndMapped() {
        final var uuid = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid, 32L, 45, "other");

        assertEquals(6, Tuple6.size());
        assertEquals("10key" + uuid.toString() + "3245other", tuple.map((p1, p2, p3, p4, p5, p6) -> "" + p1 + p2 + p3 + p4 + p5 + p6));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid, 32L, 45, "other").equals(tuple));
        assertEquals(tuple(10, "key", uuid, 32L, 45, "other").hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid, 32L, 45, "other").toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid, 32L, 45, "other-").toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple7CanBeCreatedAndMapped() {
        final var uuid1 = UUID.randomUUID();
        final var uuid2 = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid1, 32L, 45, "other", uuid2);

        assertEquals(7, Tuple7.size());
        assertEquals("10key" + uuid1.toString() + "3245other" + uuid2.toString(),
                     tuple.map((p1, p2, p3, p4, p5, p6, p7) -> "" + p1 + p2 + p3 + p4 + p5 + p6 + p7));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid1, 32L, 45, "other", uuid2).equals(tuple));
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid1, 32L, 45, "other", UUID.randomUUID()).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple8CanBeCreatedAndMapped() {
        final var uuid1 = UUID.randomUUID();
        final var uuid2 = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L);

        assertEquals(8, Tuple8.size());
        assertEquals("10key" + uuid1.toString() + "3245other" + uuid2.toString() + "67",
                     tuple.map((p1, p2, p3, p4, p5, p6, p7, p8) -> "" + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L).equals(tuple));
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 66L).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }

    @Test
    void tuple9CanBeCreatedAndMapped() {
        final var uuid1 = UUID.randomUUID();
        final var uuid2 = UUID.randomUUID();
        final var tuple = tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L, 89);

        assertEquals(9, Tuple9.size());
        assertEquals("10key" + uuid1.toString() + "3245other" + uuid2.toString() + "6789",
                     tuple.map((p1, p2, p3, p4, p5, p6, p7, p8, p9) -> "" + p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9));

        assertFalse(tuple.equals(null));
        assertTrue(tuple.equals(tuple));
        assertTrue(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L, 89).equals(tuple));
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L, 89).hashCode(), tuple.hashCode());
        assertEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L, 89).toString(), tuple.toString());
        assertNotEquals(tuple(10, "key", uuid1, 32L, 45, "other", uuid2, 67L, 90).toString(), tuple.toString());
        assertNotEquals(tuple(), tuple);
    }
}