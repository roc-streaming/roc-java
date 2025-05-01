package org.rocstreaming.roctoolkit;

/**
 *  Interface providing a method for destruct a {@link NativeObject}.
 */
@FunctionalInterface
interface Destructor {

    /**
     * Close {@link NativeObject}.
     *
     * This method can be called synchronously by the
     * user or asynchronously by {@link NativeObjectCleaner}.
     *
     * <p style="color: red;">
     * Note: It's important that this method is declared {@code static} and not as an
     * instance method for avoiding object resurrection.
     * </p>
     *
     * @param resource      {@link NativeObject#ptr NativeObject.ptr} to be closed.
     *
     * @throws IllegalStateException   if the {@link NativeObject} cannot be closed because
     *                                 it still has opened {@link NativeObject} dependencies.
     */
    void close(long resource);
}
