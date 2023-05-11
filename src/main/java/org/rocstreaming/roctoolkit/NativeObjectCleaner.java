package org.rocstreaming.roctoolkit;

import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.rocstreaming.roctoolkit.NativeObject.toHex;

/**
 * Thread collecting references and closing {@link NativeObject}s when they become phantom reachable.
 */
class NativeObjectCleaner extends Thread {

    private static final Logger LOGGER = Logger.getLogger(NativeObjectCleaner.class.getName());

    /**
     * Singleton instance.
     */
    private final static NativeObjectCleaner instance = new NativeObjectCleaner();

    /**
     * Queue of phantom reachable {@link NativeObject}.
     */
    private final ReferenceQueue<NativeObject> referenceQueue;

    /**
     * Set to keep phantom references to prevent being garbage collected,
     * otherwise reference will be collected by GC and won't be queued to {@link NativeObjectCleaner#referenceQueue}
     * when related {@link NativeObject} collected by GC
     */
    private final Set<NativeObjectPhantomReference> set = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Create a new <code>NativeObjectCleaner</code>.
     */
    private NativeObjectCleaner() {
        super("RocNativeObjectCleaner");
        setDaemon(true);
        referenceQueue = new ReferenceQueue<>();
    }

    /**
     * Get <code>NativeObjectCleaner</code> instance.
     *
     * @return the <code>NativeObjectCleaner</code> singleton instance.
     */
    static NativeObjectCleaner getInstance() {
        return instance;
    }

    /**
     * Register a {@link NativeObject} in <code>NativeObjectCleaner</code>.
     *
     * @param nativeObj  {@link NativeObject} to add.
     * @param ptr        Underlying roc object native pointer.
     * @param dependsOn  dependency for finalization ordering
     * @param destructor Destructor method.
     * @return the new {@link NativeObjectPhantomReference} associated to the {@link NativeObject}.
     */
    NativeObjectPhantomReference register(NativeObject nativeObj, long ptr, NativeObject dependsOn, Destructor destructor) {
        NativeObjectPhantomReference reference = new NativeObjectPhantomReference(nativeObj, referenceQueue, ptr, dependsOn, destructor);
        set.add(reference);
        LOGGER.log(Level.FINE, "added reference to queue, ptr={0}", new Object[]{toHex(ptr)});
        return reference;
    }

    /**
     * Remove a reference from <code>NativeObjectCleaner</code>.
     *
     * @param reference     the {@link NativeObjectPhantomReference} to unregister.
     */
    void unregister(NativeObjectPhantomReference reference) {
        set.remove(reference);
        LOGGER.log(Level.FINE, "removed reference from queue, ptr={0}", new Object[]{toHex(reference.getPtr())});
    }

    /**
     * Entrypoint method of <code>NativeObjectCleaner</code>.
     * <p>
     * Remove any phantom reachable {@link NativeObjectPhantomReference} from the
     * {@link NativeObjectCleaner#referenceQueue} associated with this <code>NativeObjectCleaner</code>
     * and <code>close</code> it.
     */
    @Override
    public void run() {
        while (isAlive()) {
            try {
                NativeObjectPhantomReference reference = (NativeObjectPhantomReference) referenceQueue.remove();
                set.remove(reference);
                LOGGER.log(Level.FINE, "collected reference from queue, ptr={0}", new Object[]{toHex(reference.getPtr())});
                reference.close();
            } catch (Exception ignore) {
            }
        }
    }

}
