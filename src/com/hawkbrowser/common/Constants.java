package com.hawkbrowser.common;

public final class Constants {

    public static final String HOME_PAGE_URL = "http://hao.360.cn";
    public static final String SEARCH_URL = "http://www.so.com/s?q=";
    
    public static final String NIGHT_MODE_JS = "(function nightMode() { " +
            
                "var arr = document.getElementsByTagName('style');" + 
                "for(var i = 0; i < arr.length; i++) {" +   
                "    if(arr[i].id == 'color_style_night_mode'){" +
                "        return;" + 
                "    }" + 
                "}" +    
            
                "var styleEl = document.createElement('style');" + 
                "styleEl.id='color_style_night_mode';" + 
                "styleEl.innerHTML = ' * { background-color: #202020 !important;} " + 
                "a { color: #0066CC !important;} * { color: #DDDDDD !important;} ';" +  
                "document.body.appendChild(styleEl);" + 
            "})();";
    
    public static final String DAY_MODE_JS = "(function dayMode() { " +

                "var arr = document.getElementsByTagName('style');" + 
                "for(var i = 0; i < arr.length; i++) {" +   
                "    if(arr[i].id == 'color_style_night_mode'){" +
                "        document.body.removeChild(arr[i]);" + 
                "        break;" + 
                "    }" + 
                "}" +        
            "})();";
}
