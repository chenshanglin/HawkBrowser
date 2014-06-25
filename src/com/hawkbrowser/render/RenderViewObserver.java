package com.hawkbrowser.render;


public interface RenderViewObserver {
    
    public void onUpdateUrl(RenderView view, String url);
    
    public void onLoadProgressChanged(RenderView view, int progress);
    
    public void didStartLoading(RenderView view, String url);

    public void didStopLoading(RenderView view, String url);
}
