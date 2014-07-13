
package com.hawkbrowser.browser.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hawkbrowser.R;
import com.hawkbrowser.browser.BrowserSetting;
import com.hawkbrowser.common.Util;
import com.hawkbrowser.render.RenderView;
import com.hawkbrowser.render.RenderViewHolderObserver;
import com.hawkbrowser.render.SingleRenderViewObserver;

import java.util.List;

public class Toolbar extends LinearLayout implements View.OnClickListener, RenderViewHolderObserver {

    private View mBack;
    private View mForward;
    private long mPrevBackKeyUpTime;
    private PopupMenuBar mPopupMenuBar;
    private TabSelector mTabSelector;
    private MainActivity mBrowser;

    private SingleRenderViewObserver mRenderViewObserver = new SingleRenderViewObserver() {

        @Override
        public void didFinishLoading(RenderView view, String url) {
            updateBackForwardStatus(renderView());
        }
    };

    public Toolbar(Context context) {
        this(context, null);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public void setBrowser(MainActivity browser) {
        mBrowser = browser;
    }

    public void enterNightMode() {

        int bgColor = getContext().getResources().getColor(R.color.night_mode_bg_default);
        setBackgroundColor(bgColor);

        TextView textView = (TextView) findViewById(R.id.toolbar_selectview_text);
        textView.setTextColor(getContext().getResources().getColor(R.color.night_mode_text_color));

        if (null != mPopupMenuBar)
            mPopupMenuBar.enterNightMode();

        if (null != mTabSelector)
            mTabSelector.enterNightMode();
    }

    public void enterDayMode() {

        int bgColor = getContext().getResources().getColor(R.color.day_mode_bg_default);
        setBackgroundColor(bgColor);

        TextView textView = (TextView) findViewById(R.id.toolbar_selectview_text);
        textView.setTextColor(Color.BLACK);

        mPopupMenuBar.enterDayMode();

        if (null != mTabSelector)
            mTabSelector.enterDayMode();
    }

    private void init() {

        inflate(getContext(), R.layout.toolbar, this);

        mBack = findViewById(R.id.toolbar_back);
        mBack.setOnClickListener(this);

        mForward = findViewById(R.id.toolbar_forward);
        mForward.setOnClickListener(this);

        findViewById(R.id.toolbar_home).setOnClickListener(this);
        findViewById(R.id.toolbar_menu).setOnClickListener(this);
        findViewById(R.id.toolbar_selectview).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (null != mTabSelector && mTabSelector.isShow() 
                && (v.getId() != R.id.toolbar_selectview))
            mTabSelector.dismiss();

        if (null != mPopupMenuBar && mPopupMenuBar.isShow()
                && (v.getId() != R.id.toolbar_menu))
            mPopupMenuBar.dismiss();

        switch (v.getId()) {
            case R.id.toolbar_back: {
                mRenderViewObserver.renderView().goBack();
                break;
            }

            case R.id.toolbar_forward: {
                mRenderViewObserver.renderView().goForward();
                break;
            }

            case R.id.toolbar_home: {
                Util.showToDoMessage(getContext());
                break;
            }

            case R.id.toolbar_menu: {
                onToolbarMenu();
                break;
            }

            case R.id.toolbar_selectview: {
                selectTab();
                break;
            }
        }
    }

    private void selectTab() {

        if (null == mTabSelector) {
            if (null == mRenderViewObserver.renderView())
                return;

            int progressBarHeight = getContext().getResources().getDimensionPixelSize(
                    R.dimen.locationbar_progressbar_height);

            View renderView = mRenderViewObserver.renderView().getView();
            mTabSelector = new TabSelector(getContext(), renderView.getWidth(),
                    renderView.getHeight() + progressBarHeight);

            if (BrowserSetting.get().getNightMode())
                mTabSelector.enterNightMode();
        }

        if (mTabSelector.isShow())
            mTabSelector.dismiss();
        else {

            int currentViewIdx = 0;
            List<RenderView> views = mBrowser.getRenderViews();

            for (int i = 0; i < views.size(); ++i) {
                if (mRenderViewObserver.renderView() == views.get(i))
                    currentViewIdx = i;
            }

            mTabSelector.updateRenderViews(views, currentViewIdx);

            View anchor = findViewById(R.id.toolbar_selectview);
            mTabSelector.show(anchor);
        }
    }

    private void onToolbarMenu() {

        if (null == mPopupMenuBar) {
            if (null == mRenderViewObserver.renderView())
                return;

            int progressBarHeight = getContext().getResources().getDimensionPixelSize(
                    R.dimen.locationbar_progressbar_height);

            View renderView = mRenderViewObserver.renderView().getView();
            mPopupMenuBar = new PopupMenuBar(getContext(), renderView.getWidth(),
                    renderView.getHeight() + progressBarHeight, mBrowser);

            if (BrowserSetting.get().getNightMode())
                mPopupMenuBar.enterNightMode();
        }

        if (mPopupMenuBar.isShow())
            mPopupMenuBar.dismiss();
        else {
            View anchor = findViewById(R.id.toolbar_menu);
            mPopupMenuBar.show(anchor);
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK != keyCode)
            return false;

        if (mRenderViewObserver.renderView().canGoBack()) {
            mRenderViewObserver.renderView().goBack();
            return true;
        }

        long time = System.currentTimeMillis();

        if (mPrevBackKeyUpTime == 0 || time - mPrevBackKeyUpTime > 3000) {
            mPrevBackKeyUpTime = time;
            Toast.makeText(getContext(), R.string.press_again_to_exit,
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    @Override
    public void onRenderViewChanged(RenderView oldView, RenderView newView) {
        mRenderViewObserver.onRenderViewChanged(oldView, newView);
        updateBackForwardStatus(newView);
    }

    private void updateBackForwardStatus(RenderView view) {
        mBack.setEnabled(view.canGoBack());
        mForward.setEnabled(view.canGoForward());
    }

    public void destroy() {
        mBrowser = null;

        if (null != mPopupMenuBar) {
            mPopupMenuBar.destroy();
            mPopupMenuBar = null;
        }

        if (null != mTabSelector) {
            mTabSelector.destroy();
            mTabSelector = null;
        }
    }
}
