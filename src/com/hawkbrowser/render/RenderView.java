
package com.hawkbrowser.render;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderView {
    
    public interface ValueCallback {
        void onReceiveValue(String value);
    }
    
    protected List<RenderViewObserver> mObservers = new ArrayList<RenderViewObserver>();

    public abstract void loadUrl(String url);

    public abstract View getView();
    
    public abstract void requestFocus();
        
    public abstract boolean canGoBack();
    
    public abstract void goBack();
    
    public abstract boolean canGoForward();
    
    public abstract void goForward();
    
    public abstract void stopLoading();
    
    public abstract void reload();
    
    public abstract void evaluateJavascript(String script, 
            ValueCallback resultCallback);
    
    public abstract void blockImage(boolean flag);
    
    public void addObserver(RenderViewObserver observer) {
        mObservers.add(observer);
    }
    
    public void removeObserver(RenderViewObserver observer) {
        mObservers.remove(observer);
    }
    
    public void destroy() {
        mObservers.clear();
    }
}
