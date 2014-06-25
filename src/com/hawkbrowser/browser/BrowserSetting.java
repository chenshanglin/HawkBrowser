package com.hawkbrowser.browser;

public final class BrowserSetting {
    
    private static BrowserSetting  mInstance;
    
    private boolean mUseChromeRender = false;
    private boolean mNightMode = false;
    private boolean mImagelessMode = false;

    public static BrowserSetting get() {
        
        if(null == mInstance)
            mInstance = new BrowserSetting();
        
        return mInstance;
    }
    
    public boolean getUseChromeRender() {
        return mUseChromeRender;
    }
    
    public void setUseChromeRender(boolean flag) {
        mUseChromeRender = flag;
    }
    
    public boolean getNightMode() {
        return mNightMode;
    }
    
    public void setNightMode(boolean flag) {
        mNightMode = flag;
    }
    
    public boolean getImagelessMode() {
        return mImagelessMode;
    }
    
    public void setImagelessMode(boolean flag) {
        mImagelessMode = flag;
    }
    
    private BrowserSetting() {
        
    }
}
