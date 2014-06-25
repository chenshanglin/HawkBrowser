
package com.hawkbrowser.browser.ui;


public interface PopupMenuBarObserver {

    void onAddBookmark();

    void onShowBookmark();

    void onShowHistory();

    void onRefresh();

    void onShowPersonalCenter();

    void onShowDownloadMgr();

    void onShare();

    void onExit();

    void onShowSetting();

    void onNightMode();

    void onImagelessMode();

    void onFullScreen();

    void onShowFileMgr();
    
    void onSwitchRender();
}
