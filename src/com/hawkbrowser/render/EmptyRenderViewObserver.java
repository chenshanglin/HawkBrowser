package com.hawkbrowser.render;

public class EmptyRenderViewObserver implements RenderViewObserver {

    @Override
    public void onUpdateUrl(RenderView view, String url) { } 
    
    @Override
    public void onLoadProgressChanged(RenderView view, int progress) { }
    
    @Override
    public void didStartLoading(RenderView view, String url) { }

    @Override
    public void didStopLoading(RenderView view, String url) { }
    
}
