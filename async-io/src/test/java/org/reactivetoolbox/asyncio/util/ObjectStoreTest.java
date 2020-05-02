package org.reactivetoolbox.asyncio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectStoreTest {

    @Test
    void elementsCanBePlacedIntoObjectPool() {
        final ObjectStore<String> pool = ObjectStore.objectPool(1);

        assertEquals(0, pool.alloc("0"));
        assertEquals(1, pool.alloc("1"));
        assertEquals(2, pool.alloc("2"));

        assertEquals("0", pool.release(0));
        assertEquals("1", pool.release(1));
        assertEquals("2", pool.release(2));
    }

    @Test
    void objectPoolReusesIndexesIfTheyAreAvailable() {
        final ObjectStore<String> pool = ObjectStore.objectPool(1);

        assertEquals(0, pool.count());

        assertEquals(0, pool.alloc("0"));
        assertEquals(1, pool.alloc("1"));
        assertEquals(2, pool.alloc("2"));

        assertEquals(3, pool.count());

        assertEquals("0", pool.release(0));
        assertEquals(2, pool.count());
        assertEquals("1", pool.release(1));
        assertEquals(1, pool.count());
        assertEquals("2", pool.release(2));
        assertEquals(0, pool.count());

        assertEquals(2, pool.alloc("+2"));
        assertEquals(1, pool.count());
        assertEquals("+2", pool.release(2));
        assertEquals(0, pool.count());

        assertEquals(2, pool.alloc("2"));
        assertEquals(1, pool.alloc("1"));
        assertEquals(0, pool.alloc("0"));

        assertEquals("0", pool.release(0));
        assertEquals("1", pool.release(1));
        assertEquals("2", pool.release(2));
        assertEquals(0, pool.count());

    }
}