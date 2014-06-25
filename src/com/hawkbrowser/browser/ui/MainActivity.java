
package com.hawkbrowser.browser.ui;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hawkbrowser.R;
import com.hawkbrowser.browser.BrowserSetting;
import com.hawkbrowser.browser.HawkBrowserApplication;
import com.hawkbrowser.common.Config;
import com.hawkbrowser.common.Constants;
import com.hawkbrowser.render.EmptyRenderViewObserver;
import com.hawkbrowser.render.RenderView;
import com.hawkbrowser.render.RenderViewHolder;
import com.hawkbrowser.render.RenderViewModel;
import com.hawkbrowser.render.RenderViewObserver;

import org.chromium.content.browser.BrowserStartupController;
import org.chromium.content.browser.ContentView;
import org.chromium.content.browser.ContentViewRenderView;
import org.chromium.content.common.ProcessInitException;
import org.chromium.ui.base.ActivityWindowAndroid;
import org.chromium.ui.base.WindowAndroid;

public class MainActivity extends Activity implements Toolbar.Observer {

    private WindowAndroid mWindow;
    private RenderViewHolder mRenderViewHolder;
    private ContentViewRenderView mContentViewRenderView;
    private RenderViewModel mRenderViewModel;
    private Toolbar mToolbar;
    private LocationBar mLocationBar;
    private View mNightModeLayer;

    private RenderViewObserver mRenderViewObserver = new EmptyRenderViewObserver() {
       
        @Override
        public void onLoadProgressChanged(RenderView view, int progress) { 
            
            if (progress > 0 && BrowserSetting.get().getNightMode())
                view.evaluateJavascript(Constants.NIGHT_MODE_JS, null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (BrowserSetting.get().getUseChromeRender())
            initChrome(savedInstanceState);
        else {
            setContentView(R.layout.main);
            initAfterRenderInit();
        }
    }

    private void initAfterRenderInit() {

        initUI();
        startRender();
        
        if (BrowserSetting.get().getNightMode())
            processNightModeViews(BrowserSetting.get().getNightMode());
    }

    private void initUI() {

        mRenderViewHolder = (RenderViewHolder) findViewById(R.id.renderview_holder);

        mLocationBar = (LocationBar) findViewById(R.id.locationbar);
        mRenderViewHolder.addObserver(mLocationBar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mRenderViewHolder.addObserver(mToolbar);

        mToolbar.setToolbarObserver(this);

        mNightModeLayer = new View(this);
        int nightModeColor = getResources().getColor(R.color.night_mode_layer_bg);
        mNightModeLayer.setBackgroundColor(nightModeColor);
    }

    private void startRender() {

        mRenderViewModel = new RenderViewModel();

        RenderView renderView;

        if (BrowserSetting.get().getUseChromeRender()) {
            renderView = mRenderViewModel.createChromeRenderView(this, mWindow);
            mRenderViewHolder.addView(mContentViewRenderView,
                    new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
            mContentViewRenderView.setCurrentContentView((ContentView) renderView.getView());
        }
        else {
            renderView = mRenderViewModel.createSystemRenderView(this);
        }

        renderView.blockImage(BrowserSetting.get().getImagelessMode());        
        renderView.addObserver(mRenderViewObserver);
        mRenderViewHolder.setCurrentRenderView(renderView);

        renderView.loadUrl(Constants.HOME_PAGE_URL);
    }

    private void initChrome(final Bundle savedInstanceState) {

        HawkBrowserApplication.initChromeResource();

        BrowserStartupController.StartupCallback callback =
                new BrowserStartupController.StartupCallback() {
                    @Override
                    public void onSuccess(boolean alreadyStarted) {
                        onChromeStartSucceed(savedInstanceState);
                    }

                    @Override
                    public void onFailure() {
                        onChromeStartFailed();
                    }
                };

        try {
            BrowserStartupController.get(this).startBrowserProcessesAsync(callback);
        } catch (ProcessInitException e) {
            onChromeStartFailed();
        }
    }

    private void onChromeStartFailed() {
        Toast.makeText(this,
                R.string.browser_process_initialize_failed,
                Toast.LENGTH_SHORT).show();
        if (Config.LOG_ENABLED)
            Log.e(Config.LOG_TAG, "Chromium browser process initialization failed");
        finish();
    }

    private void onChromeStartSucceed(Bundle savedInstanceState) {
        setContentView(R.layout.main);

        mWindow = new ActivityWindowAndroid(this);
        mWindow.restoreInstanceState(savedInstanceState);

        mContentViewRenderView = new ContentViewRenderView(this, mWindow) {
            @Override
            protected void onReadyToRender() {
            }
        };

        initAfterRenderInit();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (mToolbar.onKeyUp(keyCode, event))
            return true;

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        destroyUI();
        destroyRender();
    }

    private void destroyUI() {

        if (mNightModeLayer.getParent() != null) {
            getWindowManager().removeView(mNightModeLayer);
            mNightModeLayer = null;
        }

        mToolbar.destroy();
        mRenderViewHolder.destroy();
    }

    private void destroyRender() {

        if (null != mRenderViewModel) {
            mRenderViewModel.destroy();
            mRenderViewModel = null;
        }

        if (null != mContentViewRenderView) {
            mRenderViewHolder.removeView(mContentViewRenderView);
            mContentViewRenderView.destroy();
            mContentViewRenderView = null;
        }

        if (null != mWindow) {
            mWindow.destroy();
            mWindow = null;
        }
    }

    @Override
    public void onExit() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                finish();
            }
        });
    }

    @Override
    public void onSwitchRender() {

        destroyUI();
        destroyRender();

        if (BrowserSetting.get().getUseChromeRender()) {
            BrowserSetting.get().setUseChromeRender(false);
            initAfterRenderInit();
        } else {
            BrowserSetting.get().setUseChromeRender(true);
            initChrome(null);
        }
    }

    @Override
    public void onDayNightMode() {

        BrowserSetting.get().setNightMode(!BrowserSetting.get().getNightMode());

        processNightModeViews(BrowserSetting.get().getNightMode());
    }

    private void processNightModeViews(boolean inNightMode) {

        if (inNightMode) {

            mLocationBar.enterNightMode();
            mRenderViewModel.enterNightMode();
            mToolbar.enterNightMode();
        }
        else {

            mLocationBar.enterDayMode();
            mRenderViewModel.enterDayMode();
            mToolbar.enterDayMode();
        }   
    }

    private void processNightModeLayer(boolean inNightMode) {

        boolean isLayerAdded = mNightModeLayer.getParent() != null;

        if (inNightMode) {

            if (!isLayerAdded) {
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.RGBA_8888);
                getWindowManager().addView(mNightModeLayer, lp);
            }
        }
        else {

            if (isLayerAdded)
                getWindowManager().removeView(mNightModeLayer);
        }
    }
    
    @Override
    public void onImageMode() {
        
        BrowserSetting.get().setImagelessMode(!BrowserSetting.get().getImagelessMode());
        
        mRenderViewModel.setImagelessMode(BrowserSetting.get().getImagelessMode());
    }
}
