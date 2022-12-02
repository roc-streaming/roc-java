package org.rocstreaming.roctoolkit;

import java.lang.ref.ReferenceQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread collecting references and closing {@link NativeObject}s when they become phantom reachable.
 */
class AutoCloseThread extends Thread {

    /**
     * Singleton instance.
     */
    private final static AutoCloseThread instance = new AutoCloseThread();

    /**
     * Queue of phantom reachable {@link NativeObject}.
     */
    private final ReferenceQueue<NativeObject> referenceQueue;

    /**
     * Collection of {@link NativeObjectReference} to avoid they get garbage collected.
     */
    private final ReferenceCollector<NativeObjectReference> phantomCollector;

    /**
     * Create a new <code>AutoCloseThread</code>.
     */
    private AutoCloseThread() {
        super("AutoCloseThread");
        setDaemon(true);
        referenceQueue = new ReferenceQueue<>();
        phantomCollector = new ReferenceCollector<>();
    }

    /**
     * Get <code>AutoCloseThread</code> instance.
     *
     * @return the <code>AutoCloseThread</code> singleton instance.
     */
    static AutoCloseThread getInstance() {
        return instance;
    }

    /**
     * Add a {@link NativeObject} to <code>AutoCloseThread</code>.
     *
     * @param nativeObj     {@link NativeObject} to add.
     * @param dependsOn     {@link NativeObject} dependency.
     *
     * @return              the new {@link NativeObjectReference} associated to the {@link NativeObject}.
     */
    NativeObjectReference add(NativeObject nativeObj, NativeObject dependsOn) {
        NativeObjectReference reference = new NativeObjectReference(nativeObj, dependsOn, referenceQueue);
        phantomCollector.add(reference);
        return reference;
    }

    /**
     * Add a {@link NativeObject} to <code>AutoCloseThread</code>.
     *
     * @param nativeObj     {@link NativeObject} to add.
     *
     * @return              the new {@link NativeObjectReference} associated to the {@link NativeObject}.
     */
    NativeObjectReference add(NativeObject nativeObj) {
        return add(nativeObj, null);
    }

    /**
     * Remove a reference from <code>AutoCloseThread</code>.
     *
     * @param reference     the {@link NativeObjectReference} to remove.
     */
    void remove(NativeObjectReference reference) {
        phantomCollector.remove(reference);
    }

    /**
     * Entrypoint method of <code>AutoCloseThread</code>.
     *
     * Remove any phantom reachable {@link NativeObjectReference} from the 
     * {@link AutoCloseThread#referenceQueue} associated with this <code>AutoCloseThread</code> 
     * and <code>close</code> it.
     */
    @Override
    public void run() {
        while (isAlive()) {
            try {
                NativeObjectReference reference = (NativeObjectReference) referenceQueue.remove();
                remove(reference);
                reference.close();
            } catch (Exception e) {
            }
        }
    }

}
