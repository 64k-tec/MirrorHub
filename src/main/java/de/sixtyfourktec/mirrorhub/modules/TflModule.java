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
import de.sixtyfourktec.mirrorhub.data.TrainJourney;
import de.sixtyfourktec.mirrorhub.helpers.DownloadCallback;
import de.sixtyfourktec.mirrorhub.helpers.Downloader;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Handler;
import android.util.Log;

import android.annotation.SuppressLint;

public class TflModule extends Module<ArrayList<TrainJourney>> {

    private static final String TAG = "TflModule";
    private static TflModule instance = null;

    private static final String undergroundStr = " Underground Station";
    private static final String requestUrl =
        "https://api.tfl.gov.uk/Journey/JourneyResults/" +
        BuildConfig.TFL_FROM_ID +
        "/to/" +
        BuildConfig.TFL_TO_ID +
        "?nationalSearch=False&timeIs=Departing&journeyPreference=LeastTime" +
        "&mode=tube&walkingSpeed=Average&cyclePreference=None&alternativeCycle=False" +
        "&alternativeWalking=False&applyHtmlMarkup=False&useMultiModalCall=False" +
        "&app_id=" + BuildConfig.TFL_API_ID +
        "&app_key=" + BuildConfig.TFL_API_KEY;

    private Handler handler;

    private class Callback implements DownloadCallback {
        @SuppressLint("SimpleDateFormat")
        public void onReceived(String result) {
            try {
                final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                final Calendar now = Calendar.getInstance();
                ArrayList<TrainJourney> list = new ArrayList<TrainJourney>(5);
                final JSONArray journeys = new JSONObject(result).getJSONArray("journeys");
                for (int j = 0; j < journeys.length(); j++) {
                    final JSONArray legs = journeys.getJSONObject(j).getJSONArray("legs");
                    for (int i = 0; i < legs.length(); i++) {
                        final JSONObject l = legs.getJSONObject(i);
                        /* We are only interested in tube events */
                        if (l.getJSONObject("mode").getString("id").equals("tube")) {
                            final JSONObject o = l.getJSONArray("routeOptions").getJSONObject(0);
                            boolean isDisrupted = l.getBoolean("isDisrupted");
                            String disruptionDesc = null;
                            if (isDisrupted) {
                                disruptionDesc = l.getJSONArray("disruptions").
                                    getJSONObject(0).getString("description");
                            }
                            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/London"), Locale.UK);
                            cal.setTime(df.parse(l.getString("departureTime")));
                            /* Only future events */
                            if (cal.after(now)) {
                                TrainJourney t = new TrainJourney(TrainJourney.TUBE,
                                        l.getJSONObject("departurePoint").getString("commonName").replace(undergroundStr, ""),
                                        o.getJSONArray("directions").getString(0).replace(undergroundStr, ""),
                                        o.getJSONObject("lineIdentifier").getString("name"),
                                        !isDisrupted,
                                        disruptionDesc,
                                        cal,
                                        null);
                                list.add(t);
                                Log.i(TAG, t.toString());
                            }
                        }
                    }
                }
                /* Broadcast the new train list */
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
        }
    };

    @Override
    public void start() {
        if (handler == null) {
            handler = new Handler();
        }
        handler.post(runnableCode);
    }

    @Override
    public void stop() {
        if (handler != null) {
            handler.removeCallbacks(runnableCode);
            handler = null;
        }
    }

    public static TflModule getInstance() {
        if (instance == null)
            instance = new TflModule();
        return instance;
    }

    private TflModule() {
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
