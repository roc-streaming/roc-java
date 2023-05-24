package org.rocstreaming.roctoolkit;

/**
 * A <code>NativeObject</code> represents an underlying native roc object.
 */
class NativeObject implements AutoCloseable {

    /**
     * <code>NativeObject</code> finalizer thread.
     */
    final static NativeObjectCleaner NATIVE_OBJECT_CLEANER = NativeObjectCleaner.getInstance();

    /**
     *  Reference to {@link NativeObjectPhantomReference}.
     */
    final NativeObjectPhantomReference resource;

    static {
        RocLibrary.loadLibrary();
        NATIVE_OBJECT_CLEANER.start();
    }

    /**
     * Construct a <code>NativeObject</code>.
     *
     * @param ptr        native pointer to a roc native object
     * @param dependsOn  dependency for finalization ordering
     * @param destructor destructor method for closing <code>NativeObject</code>.
     */
    protected NativeObject(long ptr, NativeObject dependsOn, Destructor destructor) {
        this.resource = NATIVE_OBJECT_CLEANER.register(this, ptr, dependsOn, destructor);
    }

    /**
     * Get <code>NativeObject</code> native pointer.
     *
     * @return                  the native roc object pointer associated with this
     *                          <code>NativeObject</code>.
     */
    long getPtr() {
        return this.resource.getPtr();
    }

    /**
     * Close the native object and unregister it from the {@link NativeObjectCleaner}.
     *
     * @throws Exception        if the underlying roc native object cannot be closed.
     */
    @Override
    public void close() throws Exception {
        resource.close();
        resource.clear();
        NATIVE_OBJECT_CLEANER.unregister(resource);
    }
}
