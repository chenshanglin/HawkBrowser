
package com.hawkbrowser.browser;

import android.util.Log;

import com.hawkbrowser.common.Config;

import org.chromium.base.BaseSwitches;
import org.chromium.base.CommandLine;
import org.chromium.base.PathUtils;
import org.chromium.chrome.browser.ChromiumApplication;
import org.chromium.content.browser.ResourceExtractor;

public class HawkBrowserApplication extends ChromiumApplication {

    private static final String PRIVATE_DATA_DIRECTORY_SUFFIX = "HawkBrowser";

    private static final String[] CHROME_MANDATORY_PAKS = {
            "zh-CN.pak",
            "resources.pak",
            "chrome_100_percent.pak",
    };

    private static final String COMMAND_LINE_FILE =
            "/data/local/tmp/hawkbrowser-command-line";
    
    @Override
    public void onCreate() {
        // We want to do this at the earliest possible point in startup.
        super.onCreate();

        if (BrowserSetting.get().getUseChromeRender()) {
            
            initChromeResource();
            waitForDebuggerIfNeeded();
        }
    }
    
    public static void initChromeResource() {
        
        ResourceExtractor.setMandatoryPaksToExtract(CHROME_MANDATORY_PAKS);
        PathUtils.setPrivateDataDirectorySuffix(PRIVATE_DATA_DIRECTORY_SUFFIX);

        // Initialize the invalidations ID, just like we would in the downstream code.
        //        UniqueIdInvalidationClientNameGenerator.doInitializeAndInstallGenerator(this);

        initCommandLine();
    }

    public static void initCommandLine() {

        if (BrowserSetting.get().getUseChromeRender()) {
            if (!CommandLine.isInitialized())
                CommandLine.initFromFile(COMMAND_LINE_FILE);
        }
    }

    private void waitForDebuggerIfNeeded() {

        if (BrowserSetting.get().getUseChromeRender()) {
            if (CommandLine.getInstance().hasSwitch(BaseSwitches.WAIT_FOR_JAVA_DEBUGGER)) {

                if (Config.LOG_ENABLED)
                    Log.e(Config.LOG_TAG, "Waiting for Java debugger to connect...");

                android.os.Debug.waitForDebugger();

                if (Config.LOG_ENABLED)
                    Log.e(Config.LOG_TAG, "Java debugger connected. Resuming execution.");
            }
        }
    }

    @Override
    protected void openProtectedContentSettings() {
    }

    @Override
    protected void showSyncSettings() {
    }

    @Override
    protected void showTermsOfServiceDialog() {
    }

    @Override
    protected boolean areParentalControlsEnabled() {
        return false;
    }

}
