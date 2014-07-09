
package com.hawkbrowser.browser.adblock;

import com.hawkbrowser.abp.AdFilter;
import com.hawkbrowser.common.AssetExtractor;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

// package visiable only
class AbpRuleMatcher implements Handler.Callback {

    private final String[] RULE_FILES = {
            "adblock_1.dat", "adblock_2.dat"
    };

    private final String THREAD_NAME = "AbpMatchThread";

    private final int MSG_MATCH_RULE = 1;
    private final int MSG_DESTROY = 2;

    private Context mContext;
    private Handler mUIHandler;
    private Handler mMatchHandler;
    private HandlerThread mMatchThread;
    private AdFilter mAdFilter;
    private static AbpRuleMatcher sInstance = null;

    public static abstract class MatchCallback implements Runnable {

        private Object mSourceObj;
        private String mMatchUrl;
        private String[] mRules;

        public MatchCallback(Object obj, String url) {
            mSourceObj = obj;
            mMatchUrl = url;
        }

        public String getUrl() {
            return mMatchUrl;
        }

        public Object getSource() {
            return mSourceObj;
        }

        public void setRules(String[] rules) {
            mRules = rules;
        }

        public String[] getRules() {
            return mRules;
        }
    }

    public static AbpRuleMatcher get(Context context) {

        if (null == sInstance) {
            sInstance = new AbpRuleMatcher(context);
        }

        return sInstance;
    }

    public void destroy() {

        synchronized (this) {

            mMatchHandler.obtainMessage(MSG_DESTROY).sendToTarget();
            mUIHandler = null;
            mMatchHandler = null;
        }
    }

    private AbpRuleMatcher(Context context) {

        mContext = context;
        mUIHandler = new Handler();

        mMatchThread = new HandlerThread(THREAD_NAME);
        mMatchThread.start();
        mMatchHandler = new Handler(mMatchThread.getLooper(), this);

        mMatchHandler.post(new Runnable() {
            @Override
            public void run() {
                init();
            }
        });
    }

    public void matchRule(MatchCallback callback) {

        synchronized (this) {
            if (null != mMatchHandler)
                mMatchHandler.obtainMessage(MSG_MATCH_RULE, callback).sendToTarget();
        }
    }

    private void init() {
        System.loadLibrary("abp");
        AssetExtractor.get(mContext).Extract(RULE_FILES, "adblock");
        mAdFilter = new AdFilter(AssetExtractor.get(mContext).getOutputDir());
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case MSG_MATCH_RULE: {
                backgroundMatchRules(msg.obj);
                return true;
            }

            case MSG_DESTROY: {
                backgroundDestroy();
                return true;
            }
        }

        return false;
    }

    private void backgroundDestroy() {
        mAdFilter.destroy();
        mAdFilter = null;
        mMatchThread.quit();
        mMatchThread = null;
        mContext = null;

        synchronized (this) {
            sInstance = null;
        }
    }

    private void backgroundMatchRules(Object obj) {

        MatchCallback callback = (MatchCallback) obj;

        if (null != callback) {

            String url = callback.getUrl();
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            String[] rules = mAdFilter.matchRules(url);

            if (null != rules) {
                callback.setRules(rules);

                synchronized (this) {
                    if (null != mUIHandler)
                        mUIHandler.post(callback);
                }
            }
        }
    }
}
