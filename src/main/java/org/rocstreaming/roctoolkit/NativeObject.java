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
     *  Underlying roc object native pointer.
     */
    private final long ptr;

    /**
     *  Destructor method.
     */
    private final Destructor destructor;

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
     * @param dependsOn         <code>NativeObject</code> dependency for
     *                          finalization ordering
     * @param destructor        destructor method for closing <code>NativeObject</code>.
     */
    protected NativeObject(long ptr, NativeObject dependsOn, Destructor destructor) {
        this.ptr = ptr;
        this.destructor = destructor;
        this.resource = AUTO_CLOSE_THREAD.add(this, dependsOn);
    }

    /**
     * Construct a <code>NativeObject</code>.
     *
     * @param ptr               native pointer to a roc native object.
     * @param destructor        destructor method for closing <code>NativeObject</code>.
     */
    protected NativeObject(long ptr, Destructor destructor) {
        this(ptr, null, destructor);
    }

    /**
     * Get <code>NativeObject</code> native pointer.
     *
     * @return                  the native roc object pointer associated to this
     *                          <code>NativeObject</code>.
     */
    long getPtr() {
        return this.ptr;
    }

    /**
     * Get <code>NativeObject</code> destructor.
     *
     * @return                  destructor method for closing <code>NativeObject</code>.
     */
    Destructor getDestructor() {
        return this.destructor;
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
