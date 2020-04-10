package com.github.rocproject.roc;

class NativeObject {

    private long ptr;

    NativeObject() {
        ptr = 0L;
    }

    static {
        RocLibrary.loadLibrary();
    }
}
