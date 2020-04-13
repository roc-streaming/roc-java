package com.github.rocproject.roc;

class NativeObject {

    private long ptr;

    NativeObject() {
        ptr = 0L;
    }

    long getPtr() {
        return this.ptr;
    }

    static {
        RocLibrary.loadLibrary();
    }
}
