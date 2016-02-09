/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Christian Pötzsch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.sixtyfourktec.mirrorhub.helpers;

import de.sixtyfourktec.mirrorhub.helpers.DownloadCallback;

import java.io.IOException;

import android.util.Log;
import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.Callback;

public class Downloader {

    private static final String TAG = "Downloader";

    private static Downloader instance = null;
    private OkHttpClient client = new OkHttpClient();

    public static Downloader getInstance() {
        if (instance == null)
            instance = new Downloader();
        return instance;
    }

    private Downloader() {
    }

    public void download(String url, final DownloadCallback cb) {
        Request request = new Request.Builder()
            .url(url)
            .build();
        download(request, cb);
    }

    public void download(Request request, final DownloadCallback cb) {
        try {
            client.newCall(request).enqueue(new Callback() {
                 @Override
                 public void onFailure(Request request, IOException e) {
//                     e.printStackTrace();
                     Log.e(TAG, "Error: " + e.toString());
                     cb.onError();
                 }

                 @Override
                 public void onResponse(Response response) throws IOException {
                     if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                     try {
                         cb.onReceived(response.body().string());
                     } catch(Exception e) {
                         Log.e(TAG, "Error: " + e.toString());
                         cb.onError();
                     }
                 }
            });
        } catch(Exception e) {
            Log.e(TAG, "Error: " + e.toString());
            cb.onError();
        }
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
