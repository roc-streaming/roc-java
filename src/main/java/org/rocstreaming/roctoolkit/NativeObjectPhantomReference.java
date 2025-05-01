package org.rocstreaming.roctoolkit;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@code NativeObjectPhantomReference} is associated with a {@link NativeObject} and owns its
 * entire lifetime.
 *
 * A {@code NativeObjectPhantomReference} contains necessary data for closing the native object
 * after it becomes phantom reachable.
 */
class NativeObjectPhantomReference extends PhantomReference<NativeObject> implements AutoCloseable {

    /**
     *  Underlying roc object native pointer.
     */
    private final long ptr;

    /**
     * Dependency for finalization ordering. Keep strong reference to prevent it from being
     * collected by GC
     */
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final NativeObject dependsOn;

    /**
     *  Destructor method
     */
    private final Destructor destructor;

    /**
     *  {@link NativeObject} open status.
     */
    private volatile boolean isOpen;

    /**
     * Construct a new {@code NativeObjectPhantomReference}.
     *
     * @param referent   {@link NativeObject} associated.
     * @param queue      Reference queue containing phantom reachable native objects.
     * @param ptr        Underlying roc object native pointer.
     * @param dependsOn  Dependency for finalization ordering. Keep strong reference to prevent
     *                   it from being collected by GC
     * @param destructor Destructor method.
     */
    NativeObjectPhantomReference(NativeObject referent, ReferenceQueue<? super NativeObject> queue, long ptr, NativeObject dependsOn, Destructor destructor) {
        super(referent, queue);
        this.ptr = ptr;
        this.dependsOn = dependsOn;
        this.destructor = destructor;
        this.isOpen = true;
    }

    /**
     * Get {@link NativeObject} native pointer.
     *
     * @return      the native roc object pointer associated to this {@code NativeObjectPhantomReference}.
     */
    long getPtr() {
        return ptr;
    }

    /**
     * Close the native object.
     *
     * @throws IllegalStateException   if the {@link NativeObject} cannot be closed because
     *                                 it still has opened {@link NativeObject} dependencies.
     */
    @Override
    public synchronized void close() {
        if (isOpen) {
            destructor.close(ptr);
            // destructor.close(ptr) could throw exception e.g. if someone tried to close context while
            // sender/receiver still opened.
            // In such case NativeObjectCleaner will try to close it one more time after NativeObject
            // is collected by GC.
            isOpen = false;
        }
    }
}
