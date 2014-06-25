
package com.hawkbrowser.render.chrome;

import android.view.ContextMenu;

import com.hawkbrowser.render.RenderView;
import com.hawkbrowser.render.RenderViewObserver;

import org.chromium.chrome.browser.TabBase;
import org.chromium.chrome.browser.TabObserver;
import org.chromium.chrome.hawkbrowser.HawkBrowserTab;
import org.chromium.content.browser.WebContentsObserverAndroid;

import java.util.List;

//package visible only
class ChromeRenderObserverAdapter extends WebContentsObserverAndroid implements TabObserver {

    private List<RenderViewObserver> mObservers;
    private RenderView mRenderView;
    private HawkBrowserTab mTab;

    public ChromeRenderObserverAdapter(HawkBrowserTab tab, RenderView renderView,
            List<RenderViewObserver> observers) {

        super(tab.getContentViewCore());
        tab.addObserver(this);

        mObservers = observers;
        mRenderView = renderView;
        mTab = tab;
    }

    public void destroy() {

        mTab.removeObserver(this);
        mTab = null;
        mObservers = null;
        mRenderView = null;
    }

    //----------- TabObserver methods ----------------------
    @Override
    public void onContentChanged(TabBase tab) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onContextMenuShown(TabBase tab, ContextMenu menu) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDestroyed(TabBase tab) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDidFailLoad(TabBase tab, boolean isProvisionalLoad, boolean isMainFrame,
            int errorCode, String description, String failingUrl) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFaviconUpdated(TabBase tab) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoadProgressChanged(TabBase tab, int progress) {
        for (RenderViewObserver observer : mObservers) {
            observer.onLoadProgressChanged(mRenderView, progress);
        }
    }

    @Override
    public void onToggleFullscreenMode(TabBase tab, boolean enable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpdateUrl(TabBase tab, String url) {

        for (RenderViewObserver observer : mObservers) {
            observer.onUpdateUrl(mRenderView, url);
        }
    }

    @Override
    public void onWebContentsSwapped(TabBase tab) {
        // TODO Auto-generated method stub

    }

    //------------------- WebContentsObserverAndroid methods ----------------
    public void didStartLoading(String url) {

        for (RenderViewObserver observer : mObservers) {
            observer.didStartLoading(mRenderView, url);
        }
    }

    public void didStopLoading(String url) {

        for (RenderViewObserver observer : mObservers) {
            observer.didStopLoading(mRenderView, url);
        }
    }
}
