package com.flyang.api.bind;

import android.support.annotation.UiThread;

public interface UnBinder {
    @UiThread
    void unBind();

    UnBinder EMPTY = new UnBinder() {
        @Override
        public void unBind() {
        }
    };
}