package org.reactivetoolbox.io.raw;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public final class UnsafeHolder {
    private static final Unsafe instance = obtainInstance();

    private static Unsafe obtainInstance() {
        try {
            final Constructor<Unsafe> unsafeConstructor = Unsafe.class.getDeclaredConstructor();
            unsafeConstructor.setAccessible(true);
            return unsafeConstructor.newInstance();
        } catch (final InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        return null;
    }

    public static Unsafe unsafe() {
        return instance;
    }
}
