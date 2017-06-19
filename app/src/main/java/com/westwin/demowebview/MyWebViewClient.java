package com.westwin.demowebview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.net.http.SslError;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.common.util.concurrent.SettableFuture;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by feisun on 2016/9/22.
 */
public class MyWebViewClient extends WebViewClient {

    private Context mContext;
    private WebView mWebView;

    private String mUrl;
    private String mHtml;

    public MyWebViewClient(Context context, WebView webView) {
        mContext = context;
        mWebView = webView;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public synchronized void setHtml(String html) {
        if (html != null) {
            Log.i("nutch", "getHtml length " + html.length() + ", TID=" + Thread.currentThread().getId());
            Toast.makeText(mContext, html, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public synchronized void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (url != null) {
            if (url.equalsIgnoreCase(mUrl)) {
            } else {
                Log.d("nutch", mUrl);
            }
        }
    }

    @Override
    public synchronized void onPageFinished(WebView view, String url) {
        // mHtmlSettable = SettableFuture.create();
        super.onPageFinished(view, url);
        mWebView.stopLoading();
        Log.d("nutch", "onPageFinished" + ", TID=" + Thread.currentThread().getId());
        if (url != null) {
            if (url.equalsIgnoreCase(mUrl)) {
                Log.d("nutch", url);
            } else {
                Log.d("nutch", mUrl);
            }

            boolean isEval = false;
            if (isEval) {
                mWebView.evaluateJavascript(
                        "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();",
                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String html) {
                                // code here
                                mHtml = html;
                            }
                        });
            } else {
                mWebView.loadUrl("javascript:window.HtmlViewer.showHTML" +
                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        Log.d("nutch", "onReceivedError" + ", TID=" + Thread.currentThread().getId());
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.d("nutch", "onReceivedError" + ", TID=" + Thread.currentThread().getId());
    }

    @Override
    public void onReceivedHttpError(
            WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.d("nutch", "onReceivedHttpError" + ", TID=" + Thread.currentThread().getId());
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        super.onReceivedSslError(view, handler, error);
        Log.d("nutch", "onReceivedSslError" + ", TID=" + Thread.currentThread().getId());
    }
}
