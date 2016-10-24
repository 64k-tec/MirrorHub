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

package de.sixtyfourktec.mirrorhub.modules;

import de.sixtyfourktec.mirrorhub.BuildConfig;
import de.sixtyfourktec.mirrorhub.data.MusicEvent;
import de.sixtyfourktec.mirrorhub.helpers.DownloadCallback;
import de.sixtyfourktec.mirrorhub.helpers.Downloader;
import de.sixtyfourktec.mirrorhub.modules.Module;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MusicModule extends Module<MusicEvent> {

    private static final String TAG = "MusicModule";
    private static MusicModule instance = null;

    private static final int eventIntervalSecs = 5; // 10 secs
    private static final String requestUrl =
        "http://" +
        BuildConfig.MUSIC_HOST +
        "/v1/music/current";

    private Handler handler;

    private class Callback implements DownloadCallback {
        public void onReceived(String result) {
            try {
                final JSONObject music = new JSONObject(result);
                if (music.getString("state").equals("play")) {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                        .url(music.getString("coverurl"))
                        .build();

                    Response response = client.newCall(request).execute();

                    MusicEvent me = new MusicEvent(
                            music.getString("artist"),
                            music.getString("album"),
                            music.getString("title"),
                            BitmapFactory.decodeStream(response.body().byteStream()));
                    Log.i(TAG, me.toString());
                    /* Broadcast the new music event */
                    postData(me);
                } else
                    postNoData();
            } catch(Exception e) {
                Log.e(TAG, "Error: " + e.toString());
                postNoData();
            }
        }
        public void onError() {
            postNoData();
        }
    }
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Update ...");
            dl.download(requestUrl, new Callback());
            handler.postDelayed(runnableCode, eventIntervalSecs * 1000);
        }
    };

    @Override
    public void start() {
        if (handler == null) {
            handler = new Handler();
            handler.post(runnableCode);
        }
    }

    @Override
    public void stop() {
        if (handler != null) {
            handler.removeCallbacks(runnableCode);
            handler = null;
        }
    }

    public static MusicModule getInstance() {
        if (instance == null)
            instance = new MusicModule();
        return instance;
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
