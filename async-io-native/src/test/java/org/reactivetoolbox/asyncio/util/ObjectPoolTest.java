package org.reactivetoolbox.asyncio.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObjectPoolTest {

    @Test
    void elementCanBeBorrowedFromPool() {
        final ObjectPool<StringBuilder> pool = ObjectPool.objectPool(1, StringBuilder::new, (builder) -> builder.setLength(0));

        final int ndx = pool.alloc();
        assertEquals(ndx, 0);
        assertNotNull(pool.get(ndx));
        assertEquals(1, pool.used());
        assertEquals(1, pool.size());
    }

    @Test
    void elementIsCleanedUpUponRelease() {
        final ObjectPool<StringBuilder> pool = ObjectPool.objectPool(1, StringBuilder::new, (builder) -> builder.setLength(0));

        final int ndx = pool.alloc();
        assertEquals(ndx, 0);

        final StringBuilder obj = pool.get(ndx);

        assertNotNull(obj);
        assertEquals(1, pool.used());
        assertEquals(1, pool.size());

        obj.append("some data");
        assertEquals("some data", obj.toString());

        pool.release(ndx);

        assertEquals("", obj.toString());
        assertEquals(0, pool.used());
        assertEquals(1, pool.size());
    }

    @Test
    void poolIsResizedAsNecessary() {
        final ObjectPool<StringBuilder> pool = ObjectPool.objectPool(1, StringBuilder::new, (builder) -> builder.setLength(0));

        assertEquals(0, pool.used());
        assertEquals(1, pool.size());

        final int ndx1 = pool.alloc();
        assertEquals(1, pool.used());
        assertEquals(1, pool.size());

        final int ndx2 = pool.alloc();
        assertEquals(2, pool.used());
        assertEquals(2, pool.size());

        final int ndx3 = pool.alloc();
        assertEquals(3, pool.used());
        assertEquals(4, pool.size());

        final int ndx4 = pool.alloc();
        assertEquals(4, pool.used());
        assertEquals(4, pool.size());

        pool.release(ndx1);
        assertEquals(3, pool.used());
        assertEquals(4, pool.size());

        // Check that minimal protection from double releases is working
        pool.release(ndx1);
        assertEquals(3, pool.used());
        assertEquals(4, pool.size());

        pool.release(ndx2);
        assertEquals(2, pool.used());
        assertEquals(4, pool.size());

        pool.release(ndx3);
        assertEquals(1, pool.used());
        assertEquals(4, pool.size());

        pool.release(ndx4);
        assertEquals(0, pool.used());
        assertEquals(4, pool.size());
    }
}