package org.rocstreaming.roctoolkit;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <code>NativeObjectReference</code> is associated with a {@link NativeObject} and owns its entire lifetime;
 *
 * A <code>NativeObjectReference</code> contains necessary data for closing the native object
 * after it becomes phantom reachable.
 */
class NativeObjectReference extends PhantomReference<NativeObject> implements AutoCloseable {

    /**
     *  Underlying roc object native pointer.
     */
    private final long ptr;

    /**
     *  Destructor method
     */
    private final Destructor destructor;

    /**
     *  {@link NativeObject} open status.
     */
    private final AtomicBoolean isOpen;

    /**
     * Construct a new <code>NativeObjectReference</code>.
     *
     * @param referent   {@link NativeObject} associated.
     * @param queue      Reference queue containing phantom reachable native objects.
     * @param ptr        Underlying roc object native pointer.
     * @param destructor Destructor method.
     */
    NativeObjectReference(NativeObject referent, ReferenceQueue<? super NativeObject> queue, long ptr, Destructor destructor) {
        super(referent, queue);
        this.ptr = ptr;
        this.destructor = destructor;
        this.isOpen = new AtomicBoolean(true);
    }

    /**
     * Get {@link NativeObject} native pointer.
     *
     * @return      the native roc object pointer associated to this <code>NativeObjectReference</code>.
     */
    long getPtr() {
        return ptr;
    }

    /**
     * Close the native object.
     */
    @Override
    public void close() throws Exception {
        if (isOpen.compareAndSet(true, false)) {
            destructor.close(ptr);
        }
    }

}
