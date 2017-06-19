package com.westwin.demowebview;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.webkit.JavascriptInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mLoadButton;
    private Button mTestButton;
    private WebView mWebView;
    private MyWebViewClient myWebViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView.enableSlowWholeDocumentDraw();

        setContentView(R.layout.activity_main);

        mLoadButton = (Button) findViewById(R.id.loadButton);
        mLoadButton.setOnClickListener(new ClickListener());

        mTestButton = (Button) findViewById(R.id.testButton);
        mTestButton.setOnClickListener(new ClickListener());

        mWebView = (WebView) findViewById(R.id.webView);
        myWebViewClient = new MyWebViewClient(getApplicationContext(), mWebView);
        mWebView.setWebViewClient(myWebViewClient);
        mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
        mWebView.getSettings().setJavaScriptEnabled(true);
        // Set cache mode
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        // Enable APP cache
        mWebView.getSettings().setAppCacheEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath() + "/MyWebViewCache";
        mWebView.getSettings().setAppCachePath(cacheDirPath);
        mWebView.getSettings().setAppCacheMaxSize(5000000);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(myWebViewClient), "HtmlViewer");
    }

    private class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loadButton:
                    onLoadButtonClick();
                    break;
                case R.id.testButton:
                    onTestButtonClick();
                    break;
                default:
                    break;
            }
        }
    }

    private void onLoadButtonClick() {

        writeTest();

        if (mWebView != null) {
            String url = "https://item.jd.com/1474187619.html";
            url = "http://ifeng.com";
            url = "http://lt.cjdby.net/";
            url = "http://www.guancha.cn/politics/2017_02_10_393577.shtml";
            myWebViewClient.setmUrl(url);
            mWebView.loadUrl(url);
        }
    }

    private void onTestButtonClick() {
        if (mWebView != null) {
            /*
            // create bitmap screen capture
            Bitmap bitmap;
            View v1 = mWebView.getRootView();
            v1.setDrawingCacheEnabled(true);
            bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);
            String base64Str = convertBitmapToString(bitmap);
            if (base64Str != null) {
            }

            // Second
            int width = mWebView.getWidth();
            int height = mWebView.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            final Canvas c = new Canvas(bitmap);
            mWebView.draw(c);
            base64Str = convertBitmapToString(bitmap);
            if (base64Str != null) {
            }
            */

            /*
            Picture p = mWebView.capturePicture();
            Bitmap bitmap = pictureDrawable2Bitmap(p);
            String base64Str = convertBitmapToString(bitmap);
            if (base64Str != null) {
            }
            */

            // setContentView(layout);
            Bitmap bitmap = screenshot2(mWebView);
            List<byte[]> list = convertBitmapToString(bitmap);
            if (list != null && list.size() > 0) {
                ImageManager.getInstance().sendImageList("1111", "http://www.bing.com", list);
            }
        }
    }

    private List<byte[]> convertBitmapToString(Bitmap bitmap) {

        List<byte[]> list = new ArrayList<byte[]>();
        int maxHeight = 600;

        // bitmap to byte[]
        int bytesCount = bitmap.getByteCount();
        if (bytesCount > 100000) {
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int count = 0;
        for (int y = 0; y < height; y += maxHeight) {
            int h = height - y;
            if (h >= maxHeight) {
                h = maxHeight;
            }

            Bitmap b = Bitmap.createBitmap(bitmap, 0, y, width, h);
            if (b != null) {
                int subBytesCount = b.getByteCount();

                /*
                int subWidth = bitmap.getWidth();
                int subHeight = bitmap.getHeight();
                int size = b.getRowBytes() * b.getHeight();
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                b.copyPixelsToBuffer(byteBuffer);
                byte[] byteArray = byteBuffer.array();
                int baLen = byteArray.length;
                if (baLen > 0) {

                }
                */

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean bResult = b.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                if (bResult) {
                    byte[] ba = baos.toByteArray();
                    int baLen = ba.length;
                    if (baLen > 0) {
                        list.add(ba);
                    }
                } else {
                    return null;
                }

                b.recycle();
            }

            count++;
            Log.v("nutch", String.format("compress, h=%s, count=%d", y, count));
            if (count >= 12) {
                break;
            }
        }

        // save(bitmap);

        return list;
    }

    private static Bitmap pictureDrawable2Bitmap(Picture picture) {
        PictureDrawable pd = new PictureDrawable(picture);
        Bitmap bitmap = Bitmap.createBitmap(pd.getIntrinsicWidth(), pd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawPicture(pd.getPicture());
        return bitmap;
    }

    /**
     * WevView screenshot
     *
     * @param webView
     * @return
     */
    public static Bitmap screenshot(WebView webView, float scale11) {
        try {
            float scale = webView.getScale();
            int height = (int) (webView.getContentHeight() * scale + 0.5);
            Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), height, Bitmap.Config.ALPHA_8);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap screenshot2(WebView webView) {
        webView.measure(View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(webView.getMeasuredWidth(),
                webView.getMeasuredHeight(), Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        int iHeight = bitmap.getHeight();
        canvas.drawBitmap(bitmap, 0, iHeight, paint);
        webView.draw(canvas);
        return bitmap;
    }

    private void writeTest() {
        FileOutputStream fos = null;
        try {
            String state = Environment.getExternalStorageState();
            File file = new File(Environment.getExternalStorageDirectory(), "aaaa.txt");
            if (file.exists()) {
                // 如果文件存在 则删除
                file.delete();
            } else {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            byte[] bytes = "aaaaaaaaaaaaaaa".getBytes(Charset.forName("UTF8"));
            fos.write(bytes);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void save(Bitmap bitmap) {
        Log.e("nutch", "保存图片");
        try {
            File f = new File(Environment.getExternalStorageDirectory(), "/a.png");
            if (f.exists()) {
                f.delete();
            } else {
                f.createNewFile();
            }

            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("nutch", "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
