
package com.hawkbrowser.browser.adblock;

import android.content.Context;

import com.hawkbrowser.render.EmptyRenderViewObserver;
import com.hawkbrowser.render.RenderView;
import com.hawkbrowser.render.RenderViewModel;

public class AdblockTabHelper extends EmptyRenderViewObserver
        implements RenderViewModel.Observer {

    public static final String INSERT_ADBLOCK_CSS_JS = "(function filterContent() { " +
    
            "var arr = document.getElementsByTagName('style');" + 
            "for(var i = 0; i < arr.length; i++) {" +   
            "    if(arr[i].id == 'filter_content_id'){" +
            "        return;" + 
            "    }" + 
            "}" +    

            "var styleEl = document.createElement('style');" +
            "styleEl.id='filter_content_id';" + 
            "styleEl.innerHTML = '%s';" +
            "document.body.appendChild(styleEl);" +
            "})();";

    private Context mContext;

    class RuleMatchCallback extends AbpRuleMatcher.MatchCallback {

        public RuleMatchCallback(Object obj, String url) {
            super(obj, url);
        }

        @Override
        public void run() {

            RenderView view = (RenderView) getSource();
            String url = getUrl();

            if (null != view && !view.isDestroyed() && view.getUrl().equals(url)) {
                String injectedScript = generateInsertedCSS(getRules());

                if (injectedScript.length() > 0)
                    view.evaluateJavascript(injectedScript, null);
            }
        }
    }

    public AdblockTabHelper(Context context) {
        mContext = context;
    }

    private String generateInsertedCSS(String[] cssRules) {

        if (null == cssRules || cssRules.length == 0)
            return "";

        StringBuilder css = new StringBuilder();

        for (String rule : cssRules) {
            css.append(rule);
            css.append(" { display: none; } ");
        }

        return String.format(INSERT_ADBLOCK_CSS_JS, css.toString());
    }

    public void destroy() {

        AbpRuleMatcher.get(mContext).destroy();
        mContext = null;
    }
    
    @Override
    public void didFinishLoading(RenderView view, String url) {

        AbpRuleMatcher.get(mContext).matchRule(new RuleMatchCallback(view, url));
    }

    @Override
    public void onRenderViewCreate(RenderView view) {
        view.addObserver(this);
    }

    @Override
    public void onRenderViewDestroy(RenderView view) {
        view.removeObserver(this);
    }
}
