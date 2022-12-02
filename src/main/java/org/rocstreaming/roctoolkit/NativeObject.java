package org.rocstreaming.roctoolkit;

/**
 * A <code>NativeObject</code> represents an underlying native roc object.
 */
class NativeObject implements AutoCloseable {

    /**
     * <code>NativeObject</code> finalizer thread.
     */
    private final static AutoCloseThread AUTO_CLOSE_THREAD = AutoCloseThread.getInstance();

    /**
     *  Reference to {@link NativeObjectReference}.
     */
    private final NativeObjectReference resource;

    static {
        RocLibrary.loadLibrary();
        AUTO_CLOSE_THREAD.start();
    }

    /**
     * Construct a <code>NativeObject</code>.
     *
     * @param ptr               native pointer to a roc native object
     * @param destructor        destructor method for closing <code>NativeObject</code>.
     */
    protected NativeObject(long ptr, Destructor destructor) {
        this.resource = AUTO_CLOSE_THREAD.add(this, ptr, destructor);
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

    public NativeObjectReference getResource() {
        return resource;
    }

    /**
     * Close the native object and remove it from the {@link AutoCloseThread}.
     *
     * @throws Exception        if the underlying roc native object cannot be closed.
     */
    @Override
    public void close() throws Exception {
        AUTO_CLOSE_THREAD.remove(resource);
        resource.close();
    }
}
