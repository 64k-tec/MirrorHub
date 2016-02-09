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
import de.sixtyfourktec.mirrorhub.data.TimeToWork;
import de.sixtyfourktec.mirrorhub.helpers.DownloadCallback;
import de.sixtyfourktec.mirrorhub.helpers.Downloader;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Handler;
import android.util.Log;

public class TimeToWorkModule extends Module<TimeToWork>{

    private static final String TAG = "TimeToWorkModule";
    private static TimeToWorkModule instance = null;

    private static final int eventInterval = 60 * 2; // 2 mins
    private static final String requestUrl =
        "https://maps.googleapis.com/maps/api/distancematrix/json" +
        "?origins=" + BuildConfig.TIMETOWORK_ORIGIN.replace(' ', '+') +
        "&destinations=" + BuildConfig.TIMETOWORK_DESTINATION.replace(' ', '+') +
        "&mode=driving&departure_time=now" +
        "&key=" + BuildConfig.MAPS_API_KEY;

    private Handler handler;

    private class Callback1 implements DownloadCallback {
        public void onReceived(String result) {
            try {
                final JSONObject element = new JSONObject(result).
                    getJSONArray("rows").getJSONObject(0).
                    getJSONArray("elements").getJSONObject(0);
                final String duration = element.getJSONObject("duration_in_traffic").
                    getString("text");
                final String distance = element.getJSONObject("distance").
                    getString("text");
                TimeToWork ttw = new TimeToWork(duration, distance);
                Log.i(TAG, ttw.toString());
                postData(ttw);
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
            /* Only show this Mo-Fr 7-10am: */
            GregorianCalendar cal = new GregorianCalendar();
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            int hod = cal.get(Calendar.HOUR_OF_DAY);
            if ((dow >= Calendar.MONDAY && dow <= Calendar.FRIDAY &&
                  hod >= 7 && hod <= 9) ||
                BuildConfig.TIMETOWORK_SHOW_ALWAYS == true)
            {
                Log.i(TAG, "Update ...");
                dl.download(requestUrl, new Callback1());
            }else
            {
                Log.i(TAG, "Skip update");
                postNoData();
            }
            /* Rerun in 2 mins */
            handler.postDelayed(runnableCode, eventInterval * 1000);
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

    public static TimeToWorkModule getInstance() {
        if (instance == null)
            instance = new TimeToWorkModule();
        return instance;
    }

    private TimeToWorkModule() {
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
