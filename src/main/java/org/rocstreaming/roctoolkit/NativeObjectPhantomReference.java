package org.rocstreaming.roctoolkit;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <code>NativeObjectPhantomReference</code> is associated with a {@link NativeObject} and owns its entire lifetime;
 *
 * A <code>NativeObjectPhantomReference</code> contains necessary data for closing the native object
 * after it becomes phantom reachable.
 */
class NativeObjectPhantomReference extends PhantomReference<NativeObject> implements AutoCloseable {

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
     * Construct a new <code>NativeObjectPhantomReference</code>.
     *
     * @param referent   {@link NativeObject} associated.
     * @param queue      Reference queue containing phantom reachable native objects.
     * @param ptr        Underlying roc object native pointer.
     * @param destructor Destructor method.
     */
    NativeObjectPhantomReference(NativeObject referent, ReferenceQueue<? super NativeObject> queue, long ptr, Destructor destructor) {
        super(referent, queue);
        this.ptr = ptr;
        this.destructor = destructor;
        this.isOpen = new AtomicBoolean(true);
    }

    /**
     * Get {@link NativeObject} native pointer.
     *
     * @return      the native roc object pointer associated to this <code>NativeObjectPhantomReference</code>.
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
