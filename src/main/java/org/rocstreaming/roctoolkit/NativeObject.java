package org.rocstreaming.roctoolkit;

/**
 * A <code>NativeObject</code> represents an underlying native roc object.
 */
class NativeObject implements AutoCloseable {

    /**
     * <code>NativeObject</code> finalizer thread.
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
     * Construct a <code>NativeObject</code>.
     *
     * @param ptr               native pointer to a roc native object
     * @param destructor        destructor method for closing <code>NativeObject</code>.
     */
    protected NativeObject(long ptr, Destructor destructor) {
        this.resource = NATIVE_OBJECT_CLEANER.register(this, ptr, destructor);
    }

    /**
     * Get <code>NativeObject</code> native pointer.
     *
     * @return                  the native roc object pointer associated to this
     *                          <code>NativeObject</code>.
     */
    long getPtr() {
        return this.resource.getPtr();
    }

    /**
     * Close the native object and remove it from the {@link NativeObjectCleaner}.
     *
     * @throws Exception        if the underlying roc native object cannot be closed.
     */
    @Override
    public void close() throws Exception {
        NATIVE_OBJECT_CLEANER.remove(resource);
        resource.close();
    }
}
