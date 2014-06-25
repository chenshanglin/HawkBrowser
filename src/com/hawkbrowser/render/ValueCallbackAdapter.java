
package com.hawkbrowser.render;

import android.webkit.ValueCallback;

import org.chromium.content.browser.ContentViewCore;

public class ValueCallbackAdapter implements ValueCallback<String>,
        ContentViewCore.JavaScriptCallback {

    private RenderView.ValueCallback mValueCallback;

    @Override
    public void onReceiveValue(String value) {

        if (null != mValueCallback)
            mValueCallback.onReceiveValue(value);
    }

    @Override
    public void handleJavaScriptResult(String value) {
        if (null != mValueCallback)
            mValueCallback.onReceiveValue(value);
    }

    public ValueCallbackAdapter(RenderView.ValueCallback callback) {
        mValueCallback = callback;
    }
}
