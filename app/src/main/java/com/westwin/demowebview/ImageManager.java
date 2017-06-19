package com.westwin.demowebview;

import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by feisun on 2017/2/10.
 */
public class ImageManager {

    private static ImageManager Instance;

    private ImageManager() {
    }

    public static ImageManager getInstance() {
        if (Instance == null) {
            Instance = new ImageManager();
        }
        return Instance;
    }

    public synchronized void sendImageList(
            String taskId,
            String url,
            List<byte[]> list) {

        ImageRunnable r = new ImageRunnable(taskId, url, list);
        Thread t = new Thread(r);
        t.start();
    }

    public class ImageRunnable implements Runnable {

        private String mTaskId;
        private String mUrl;
        private List<byte[]> mList;

        public ImageRunnable(String taskId, String url, List<byte[]> list) {
            mTaskId = taskId;
            mUrl = url;
            mList = list;
        }

        @Override
        public void run() {
            Log.v("nutch", "ImageRunnable started");

            int id = 0;
            int count = mList.size();
            for (byte[] bs : mList) {
                try {
                    String base64 = Base64.encodeToString(bs, 0, bs.length, Base64.NO_WRAP);

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("taskId", mTaskId)
                            .addFormDataPart("url", mUrl)
                            .addFormDataPart("id", Integer.toString(id))
                            .addFormDataPart("data", base64)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://image.tlvstream.com/WebApi/Image")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.v("nutch", String.format("post ok, %d-%d", id++, count));
                    } else {
                        Log.v("nutch", String.format("post error, %d-%d", id++, count));
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
