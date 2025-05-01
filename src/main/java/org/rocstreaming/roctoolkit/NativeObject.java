package org.rocstreaming.roctoolkit;

/**
 * A {@code NativeObject} represents an underlying native roc object.
 */
class NativeObject implements AutoCloseable {

    /**
     * {@code NativeObject} finalizer thread.
     */
    private final static NativeObjectCleaner NATIVE_OBJECT_CLEANER = NativeObjectCleaner.getInstance();

    /**
     *  Reference to {@link NativeObjectPhantomReference}.
     */
    private final NativeObjectPhantomReference resource;

    static {
        RocLibrary.loadLibrary();
        NATIVE_OBJECT_CLEANER.start();
    }

    /**
     * Construct a {@code NativeObject}.
     *
     * @param ptr        native pointer to a roc native object
     * @param dependsOn  dependency for finalization ordering
     * @param destructor destructor method for closing {@code NativeObject}.
     */
    protected NativeObject(long ptr, NativeObject dependsOn, Destructor destructor) {
        this.resource = NATIVE_OBJECT_CLEANER.register(this, ptr, dependsOn, destructor);
    }

    /**
     * Get {@code NativeObject} native pointer.
     *
     * @return                  the native roc object pointer associated to this
     *                          {@code NativeObject}.
     */
    long getPtr() {
        return this.resource.getPtr();
    }

    /**
     * Close the native object and unregister it from the {@link NativeObjectCleaner}.
     *
     * @throws IllegalStateException    if the underlying roc native object cannot be closed because
     *                                  it still has opened {@link NativeObject} dependencies.
     */
    @Override
    public void close() {
        resource.close();
        resource.clear();
        NATIVE_OBJECT_CLEANER.unregister(resource);
    }

    static String toHex(long value) {
        return Long.toHexString(value);
    }
}
