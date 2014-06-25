
package com.hawkbrowser.render;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class RenderViewHolder extends FrameLayout {
    
    private RenderView mCurrentView;
    private List<RenderViewHolderObserver> mObservers = new ArrayList<RenderViewHolderObserver>();
    
    public RenderViewHolder(Context context) {
        super(context);
    }
    
    public RenderViewHolder(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderViewHolder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCurrentRenderView(RenderView view) {
        
        if(null != mCurrentView) {
            removeView(mCurrentView.getView());
        }
        
        if(null != view) {
            addView(view.getView());
            
            for(RenderViewHolderObserver observer : mObservers) {
                observer.onRenderViewChanged(mCurrentView, view);
            }
        }
        
        mCurrentView = view;
    }
        
    public void addObserver(RenderViewHolderObserver observer) {
        mObservers.add(observer);
    }
    
    public void removeObserver(RenderViewHolderObserver observer) {
        mObservers.remove(observer);
    }    
    
    public void destroy() {
        mObservers.clear();
        setCurrentRenderView(null);
    }
}
