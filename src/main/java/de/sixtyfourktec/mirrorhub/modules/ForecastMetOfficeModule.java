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
import de.sixtyfourktec.mirrorhub.data.Forecast;
import de.sixtyfourktec.mirrorhub.data.ComparableList;
import de.sixtyfourktec.mirrorhub.helpers.DownloadCallback;
import de.sixtyfourktec.mirrorhub.helpers.Downloader;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Handler;
import android.util.Log;

import android.annotation.SuppressLint;

public class ForecastMetOfficeModule extends Module<ComparableList<Forecast>> {

    private static final String TAG = "ForecastMetOfficeModule";
    private static ForecastMetOfficeModule instance = null;

    private static final int maxEvents = 5;
    private static final int eventInterval = 60 * 2; // 2 mins
    private static final String requestUrl =
        "http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/" +
        BuildConfig.FORECAST_LOCATION_ID +
        "?res=3hourly" +
        "&key=" + BuildConfig.METOFFICE_API_KEY;

    private Handler handler;

    private class Callback implements DownloadCallback {
        @SuppressLint("SimpleDateFormat")
        public void onReceived(String result) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Calendar now = Calendar.getInstance();
                now.add(Calendar.HOUR, -3);
                JSONArray periods = new JSONObject(result).
                    getJSONObject("SiteRep").getJSONObject("DV").
                    getJSONObject("Location").getJSONArray("Period");
                ComparableList<Forecast> list = new ComparableList<Forecast>(maxEvents);
                /* We are looking for the next 5 events */
                for (int j = 0; j < periods.length() && list.size() < maxEvents; j++) {
                    JSONObject per = periods.getJSONObject(j);
                    JSONArray rep = per.getJSONArray("Rep");
                    Date date = formatter.parse(per.getString("value"));
                    for (int i = 0; i < rep.length() && list.size() < maxEvents; i++) {
                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"), Locale.UK);
                        JSONObject Forecast = rep.getJSONObject(i);
                        cal.setTime(date);
                        cal.set(Calendar.MINUTE, Forecast.getInt("$"));
                        /* We are only interested in future dates */
                        if (cal.compareTo(now) >= 0) {
                            Forecast f = new Forecast(cal, Forecast.getString("T"), Forecast.getString("W"));
                            list.add(f);
                            Log.i(TAG, f.toString());
                        }
                    }
                }
                /* Broadcast the new forecast list */
                if (list.size() > 0)
                    postData(list);
                else
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
            /* Rerun after interval time */
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

    public static ForecastMetOfficeModule getInstance() {
        if (instance == null)
            instance = new ForecastMetOfficeModule();
        return instance;
    }

    private ForecastMetOfficeModule() {
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
