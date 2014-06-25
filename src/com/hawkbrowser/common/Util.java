package com.hawkbrowser.common;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

public final class Util {

    // for test
    public static void showToDoMessage(Context context) {
        Toast.makeText(context, "TODO", Toast.LENGTH_SHORT).show();
    }
    
    @SuppressWarnings("deprecation")
    public static Point screenSize(Context ctx) {
        WindowManager wm = (WindowManager) 
                ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        
        if(android.os.Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            display.getSize(size);
            return size;
        }
        else {
            return new Point(display.getWidth(), display.getHeight());
        }
        
    }
}
