package com.hawkbrowser.render;

import android.content.Context;

import com.hawkbrowser.common.Constants;
import com.hawkbrowser.render.chrome.ChromeRenderView;
import com.hawkbrowser.render.system.SystemRenderView;

import org.chromium.chrome.hawkbrowser.HawkBrowserTab;
import org.chromium.ui.base.WindowAndroid;

import java.util.ArrayList;

public class RenderViewModel {
    
    private ArrayList<RenderView> mRenderViews = new ArrayList<RenderView>();
    private ArrayList<Observer> mObservers = new ArrayList<Observer>();
    
    public interface Observer {
    	public void onRenderViewCreate(RenderView view);
    	public void onRenderViewDestroy(RenderView view);
    }

    public RenderView createChromeRenderView(Context context, WindowAndroid window) {
        
        HawkBrowserTab tab = new HawkBrowserTab(context, window);
        ChromeRenderView renderView = new ChromeRenderView(tab);
        mRenderViews.add(renderView);
        
        for(Observer o : mObservers)
        	o.onRenderViewCreate(renderView);
        
        return renderView;
    }
    
    public RenderView createSystemRenderView(Context context) {
        
        SystemRenderView renderView = new SystemRenderView(context, null);
        mRenderViews.add(renderView);
        
        for(Observer o : mObservers)
        	o.onRenderViewCreate(renderView);
        
        return renderView;
    }
    
    public void destroyView(RenderView view) {
    	
        for(Observer o : mObservers)
        	o.onRenderViewDestroy(view);
        
        view.destroy();
        mRenderViews.remove(view);
    }
    
    public void destroy() {
        
        for(RenderView view : mRenderViews) {
            destroyView(view);
        }
        
        mRenderViews.clear();
        mObservers.clear();
    }
    
    public void enterNightMode() {
        
        for (RenderView view : mRenderViews) {
            view.evaluateJavascript(Constants.NIGHT_MODE_JS, null);
        }
    }
    
    public void enterDayMode() {
        
        for (RenderView view : mRenderViews) {
            view.evaluateJavascript(Constants.DAY_MODE_JS, null);
        }
    }
    
    public void setImagelessMode(boolean flag) {
        
        for (RenderView view : mRenderViews) {
            view.blockImage(flag);
            view.reload();
        }
    }
    
    public void addObserver(Observer observer) {
    	mObservers.add(observer);
    }
    
    public void removeObserver(Observer observer) {
    	mObservers.remove(observer);
    }
}
