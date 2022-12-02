package org.rocstreaming.roctoolkit;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
     * Set to keep references to prevent being garbage collected
     */
    private final Set<NativeObjectReference> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Create a new <code>AutoCloseThread</code>.
     */
    private AutoCloseThread() {
        super("AutoCloseThread");
        setDaemon(true);
        referenceQueue = new ReferenceQueue<>();
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
     * @param nativeObj  {@link NativeObject} to add.
     * @param ptr        Underlying roc object native pointer.
     * @param destructor Destructor method.
     * @return the new {@link NativeObjectReference} associated to the {@link NativeObject}.
     */
    NativeObjectReference add(NativeObject nativeObj, long ptr, Destructor destructor) {
        NativeObjectReference reference = new NativeObjectReference(nativeObj, referenceQueue, ptr, destructor);
        set.add(reference);
        return reference;
    }

    /**
     * Remove a reference from <code>AutoCloseThread</code>.
     *
     * @param reference     the {@link NativeObjectReference} to remove.
     */
    void remove(NativeObjectReference reference) {
        set.remove(reference);
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
                set.remove(reference);
                reference.close();
            } catch (Exception e) {
            }
        }
    }

}
