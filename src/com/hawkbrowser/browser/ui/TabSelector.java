
package com.hawkbrowser.browser.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.hawkbrowser.R;
import com.hawkbrowser.common.Util;
import com.hawkbrowser.render.RenderView;

import java.util.List;

public class TabSelector implements View.OnClickListener {

    private int mWidth;
    private int mHeight;
    private ViewGroup mView;
    private PopupWindow mPopup;
    private ListView mTabItems;

    public TabSelector(Context context, int width, int height) {

        mWidth = width;
        mHeight = height;
        init(context);
    }

    private void init(Context context) {

        LayoutInflater li = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mView = (ViewGroup) li.inflate(R.layout.popup_tab_selector, null);
        mTabItems = (ListView) mView.findViewById(R.id.popup_tab_selector_list);
        
        Button addTab = (Button) mView.findViewById(R.id.popup_tab_selector_add);
        addTab.setOnClickListener(this);
        
        
        Button closeAll = (Button) mView.findViewById(R.id.popup_tab_selector_closeall);
        closeAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.popup_tab_selector_add: {
                Util.showToDoMessage(mView.getContext());
                break;
            }

            case R.id.popup_tab_selector_closeall: {
                Util.showToDoMessage(mView.getContext());
                break;
            }
        }

    }

    public void show(View anchor) {
        if (null == mPopup) {
            mPopup = new PopupWindow(mView, mWidth, mHeight);
            mPopup.showAsDropDown(anchor, 0, 0);
        }
    }

    public boolean isShow() {
        return null != mPopup && mPopup.isShowing();
    }

    public void dismiss() {
        if (null != mPopup) {
            mPopup.dismiss();
            mPopup = null;
        }
    }

    public void enterNightMode() {

        int bgColor = mView.getContext().getResources().getColor(
                R.color.night_mode_bg_default);
        int textColor = mView.getContext().getResources().getColor(
                R.color.night_mode_text_color);

        onDayNightModeChanged(bgColor, textColor);
    }

    public void enterDayMode() {

        int bgColor = mView.getContext().getResources().getColor(
                R.color.day_mode_bg_default);
        onDayNightModeChanged(bgColor, Color.BLACK);
    }

    public void onDayNightModeChanged(int bgColor, int textColor) {

        mView.findViewById(R.id.popup_tab_selector_list).setBackgroundColor(bgColor);
        mView.findViewById(R.id.popup_tab_selector_bottom).setBackgroundColor(bgColor);

        Button addTab = (Button) mView.findViewById(R.id.popup_tab_selector_add);
        addTab.setTextColor(textColor);

        Button closeAll = (Button) mView.findViewById(R.id.popup_tab_selector_closeall);
        closeAll.setTextColor(textColor);
    }

    public void destroy() {

    }

    public void updateRenderViews(List<RenderView> renderViews, int currentViewIdx) {
        TabSelectorListAdapter adapter = new TabSelectorListAdapter(
                mView.getContext(), renderViews, currentViewIdx);
        mTabItems.setAdapter(adapter);
    }
}
