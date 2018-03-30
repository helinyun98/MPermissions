package top.helinyun.util.permissions.util;

import android.os.Looper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Glide Utils
 */
public class Util {

    private Util() {
    }

    /**
     * Throws an {@link java.lang.IllegalArgumentException} if called on a thread other than the main thread.
     */
    public static void assertMainThread() {
        if (!isOnMainThread()) {
            throw new IllegalArgumentException("You must call this method on the main thread");
        }
    }

    /**
     * Throws an {@link java.lang.IllegalArgumentException} if called on the main thread.
     */
    public static void assertBackgroundThread() {
        if (!isOnBackgroundThread()) {
            throw new IllegalArgumentException("YOu must call this method on a background thread");
        }
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    /**
     * Returns {@code true} if called on the main thread, {@code false} otherwise.
     */
    public static boolean isOnBackgroundThread() {
        return !isOnMainThread();
    }

    /**
     * Returns a copy of the given list that is safe to iterate over and perform actions that may
     * modify the original list.
     */
    @SuppressWarnings("UseBulkOperation")
    public static <T> List<T> getSnapshot(Collection<T> other) {
        List<T> result = new ArrayList<>(other.size());
        for (T item : other) {
            result.add(item);
        }
        return result;
    }
}
