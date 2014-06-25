package com.hawkbrowser.render;

public class SingleRenderViewObserver extends EmptyRenderViewObserver {

    private RenderView mCurrentRenderView;
    
    public RenderView renderView() {
        return mCurrentRenderView;
    }
    
    public void onRenderViewChanged(RenderView oldView, RenderView newView) {
        
        assert mCurrentRenderView == oldView;
        
        if (null != mCurrentRenderView)
            mCurrentRenderView.removeObserver(this);
        
        newView.addObserver(this);
        mCurrentRenderView = newView;
    }    
}
