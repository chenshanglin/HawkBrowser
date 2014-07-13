
package com.hawkbrowser.browser.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hawkbrowser.R;
import com.hawkbrowser.browser.BrowserSetting;
import com.hawkbrowser.render.RenderView;

import java.util.List;

public class TabSelectorListAdapter extends BaseAdapter
        implements AdapterView.OnItemClickListener {

    private List<RenderView> mViews;
    private int mCurrentViewIdx;
    private int mBgColor;
    private int mTextColor;
    private int mBorderBg;
    private Context mContext;

    public TabSelectorListAdapter(Context context, List<RenderView> views, int currentViewIdx) {
        mViews = views;
        ;
        mContext = context;
        mCurrentViewIdx = currentViewIdx;

        if (BrowserSetting.get().getNightMode()) {
            mBgColor = mContext.getResources().getColor(
                    R.color.night_mode_bg_default);
            mTextColor = mContext.getResources().getColor(
                    R.color.night_mode_text_color);
            mBorderBg = R.drawable.tab_selector_listitem_border_night;
        } else {
            mBgColor = mContext.getResources().getColor(
                    R.color.location_bar_bg);
            mTextColor = Color.BLACK;
            mBorderBg = R.drawable.tab_selector_listitem_border;
        }
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mViews.size())
            return mViews.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (position < 0 || position >= mViews.size())
            return null;

        ViewGroup vg = (ViewGroup) convertView;

        if (null == vg) {
            vg = (ViewGroup) LayoutInflater.from(mContext).inflate(
                    R.layout.select_tab_list_item, null);
        }

        RenderView render = mViews.get(position);

        TextView title = (TextView) vg.findViewById(R.id.selecttab_listitem_title);
        title.setText(render.getTitle());

        TextView url = (TextView) vg.findViewById(R.id.selecttab_listitem_url);
        url.setText(render.getUrl());

        if (position == mCurrentViewIdx)
            vg.setBackgroundResource(mBorderBg);

        title.setTextColor(mTextColor);

        return vg;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub

    }

}
