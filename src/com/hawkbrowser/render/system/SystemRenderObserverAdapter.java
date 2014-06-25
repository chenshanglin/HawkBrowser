
package com.hawkbrowser.render.system;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hawkbrowser.browser.BrowserSetting;
import com.hawkbrowser.common.Constants;
import com.hawkbrowser.render.RenderView;
import com.hawkbrowser.render.RenderViewObserver;

import java.util.List;

// package visible only
class SystemRenderObserverAdapter {

    private List<RenderViewObserver> mObservers;
    private RenderView mRenderView;
    private WebView mWebView;

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            
            for (RenderViewObserver observer : mObservers)
                observer.didStartLoading(mRenderView, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            
            for (RenderViewObserver observer : mObservers)
                observer.didStopLoading(mRenderView, url);
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            for (RenderViewObserver observer : mObservers)
                observer.onLoadProgressChanged(mRenderView, newProgress);
        }
    };

    public SystemRenderObserverAdapter(final RenderView renderView, WebView webView,
            final List<RenderViewObserver> observers) {

        mRenderView = renderView;
        mObservers = observers;
        mWebView = webView;

        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
    }

    public void destroy() {

        mWebView.setWebViewClient(null);
        mWebView.setWebChromeClient(null);

        mRenderView = null;
        mWebView = null;
        mObservers = null;
        mWebViewClient = null;
        mWebChromeClient = null;
    }

}
