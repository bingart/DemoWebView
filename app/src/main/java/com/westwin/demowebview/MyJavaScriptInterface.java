package com.westwin.demowebview;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Created by feisun on 2016/9/23.
 */
public class MyJavaScriptInterface {

    private MyWebViewClient client;

    MyJavaScriptInterface(MyWebViewClient client) {
        this.client = client;
    }

    @JavascriptInterface
    public void showHTML(String html) {
        if (html != null) {
            this.client.setHtml(html);
            Log.d("nutch", "showHTML" + ", TID=" + Thread.currentThread().getId());
        }
    }
}
