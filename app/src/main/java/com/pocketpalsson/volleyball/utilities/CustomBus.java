package com.pocketpalsson.volleyball.utilities;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class CustomBus extends Bus {
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public CustomBus() {
        super(ThreadEnforcer.ANY);
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(() -> CustomBus.super.post(event));
        }
    }
}
