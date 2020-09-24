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
     * Thread running flag.
     */
    private final AtomicBoolean running;

    /**
     * Create a new <code>AutoCloseThread</code>.
     */
    private AutoCloseThread() {
        referenceQueue = new ReferenceQueue<>();
        phantomCollector = new ReferenceCollector<>();
        running = new AtomicBoolean(false);
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
     * Get thread running flag.
     *
     * @return  true if <code>AutoCloseThread</code> is currently running,
     *          false otherwise.
     */
    boolean isRunning() {
        return running.get();
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
     * Polls the {@link ReferenceQueue} associated to this <code>AutoCloseThread</code>
     * and <code>close</code> any {@link NativeObjectReference} when it becomes phantom reachable.
     */
    @Override
    public void run() {
        running.set(true);
        while (isRunning()) {
            NativeObjectReference reference;
            synchronized (this) {
                if ((reference = (NativeObjectReference) referenceQueue.poll()) != null) {
                    try {
                        remove(reference);
                        reference.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    /**
     * Close all {@link NativeObjectReference} that are still open.
     */
    void closeAll() {
        running.set(false);
        synchronized (this) {
            phantomCollector.iterator().forEachRemaining((reference) -> {
                try {
                    remove(reference);
                    reference.close();
                } catch (Exception e) {
                }
            });
        }
    }
}