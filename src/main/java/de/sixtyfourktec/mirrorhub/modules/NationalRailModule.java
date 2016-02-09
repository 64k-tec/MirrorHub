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

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import android.os.Handler;
import android.util.Log;

import android.annotation.SuppressLint;

public class NationalRailModule extends Module<ArrayList<TrainJourney>> {

    private static final String TAG = "NationalRailModule";
    private static NationalRailModule instance = null;

    /* Print the raw xml content in logcat */
    private static final boolean showXml = false;

    private Handler handler;
    private Request request;

    private class Callback implements DownloadCallback {
        @SuppressLint("SimpleDateFormat")
        @Override
        public void onReceived(String result) {
            try {
                final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                final Calendar now = Calendar.getInstance();
                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.UK);
                ArrayList<TrainJourney> list = new ArrayList<TrainJourney>(5);
                InputStream stream = new ByteArrayInputStream(result.getBytes(Charset.forName("UTF-8")));
                SAXBuilder sb = new SAXBuilder();
                Document doc = sb.build(stream);

                Namespace lt2 = Namespace.getNamespace("lt2", "http://thalesgroup.com/RTTI/2014-02-20/ldb/types");
                Element root = doc.getRootElement();
                ElementFilter filter1 = new ElementFilter("GetStationBoardResult");
                ElementFilter filter2 = new ElementFilter("service", lt2);
                formatter.setCalendar(cal);
                for (Element c:root.getDescendants(filter2))
                {
                    Calendar time = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.UK);
                    Calendar exp_time = null;
                    String origin = c.getChild("origin", lt2).getChild("location", lt2).getChildText("locationName", lt2);
                    String dest = c.getChild("destination", lt2).getChild("location", lt2).getChildText("locationName", lt2);
                    String line = c.getChildText("operator", lt2);
                    String std = c.getChildText("std", lt2);
                    String etd = c.getChildText("etd", lt2);
                    cal.setTime(formatter.parse(std));
                    time.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                    time.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    time.set(Calendar.SECOND, 0);
                    time.set(Calendar.MILLISECOND, 0);
                    if (!etd.equals("On time")) {
                        exp_time = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.UK);
                        cal.setTime(formatter.parse(etd));
                        exp_time.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
                        exp_time.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                        exp_time.set(Calendar.SECOND, 0);
                        exp_time.set(Calendar.MILLISECOND, 0);
                    }
                    /* Only future events */
                    if (time.after(now) ||
                        (exp_time != null &&
                         exp_time.after(now))) {
                        TrainJourney t = new TrainJourney(TrainJourney.RAIL,
                                origin, dest, line, etd.equals("On time"), null, time, exp_time);
                        list.add(t);
                        Log.i(TAG, t.toString());
                    }
                }
                /* Broadcast the new train list */
                if (list.size() > 0)
                    postData(list);
                else
                    postNoData();
                if (showXml) {
                    Log.i(TAG, new XMLOutputter(Format.getPrettyFormat()).outputString(doc));
                }
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
            dl.download(request, new Callback());
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

    public static NationalRailModule getInstance() {
        if (instance == null)
            instance = new NationalRailModule();
        return instance;
    }

    private NationalRailModule() {
        /* Create the soap envelope document */
        Document doc = new Document();
        Namespace soap = Namespace.getNamespace("soap", "http://schemas.xmlsoap.org/soap/envelope/");
        Namespace ldb = Namespace.getNamespace("ldb", "http://thalesgroup.com/RTTI/2014-02-20/ldb/");
        Namespace typ = Namespace.getNamespace("typ", "http://thalesgroup.com/RTTI/2010-11-01/ldb/commontypes");
        Element env = new Element("Envelope", soap);
        /* Header */
        env.addContent(new Element("Header", soap).
                addContent(new Element("AccessToken", typ).
                    addContent(new Element("TokenValue", typ).
                        addContent(BuildConfig.NATIONALRAIL_API_KEY))));
        /* Body */
        Element dep_request = new Element("GetDepartureBoardRequest", ldb).
            addContent(new Element("crs", ldb).
                    addContent(BuildConfig.NATIONALRAIL_CRS)).
            addContent(new Element("numRows", ldb).
                    addContent("5"));
        if (!BuildConfig.NATIONALRAIL_FILTER_CRS.isEmpty()) {
            dep_request.addContent(new Element("filterCrs", ldb).
                    addContent(BuildConfig.NATIONALRAIL_FILTER_CRS)).
                addContent(new Element("filterType", ldb).
                        addContent(BuildConfig.NATIONALRAIL_FILTER_TYPE));
        }
        env.addContent(new Element("Body", soap).addContent(dep_request));
        doc.setContent(env);
        String xml = new XMLOutputter(Format.getPrettyFormat()).outputString(doc);
        if (showXml) {
            Log.i(TAG, xml);
        }

        /* Create a static request object we can reuse */
        RequestBody body = RequestBody.create(MediaType.parse("text/xml; charset=utf-8"), xml);
        request = new Request.Builder()
            .url("https://lite.realtime.nationalrail.co.uk/OpenLDBWS/ldb6.asmx")
            .post(body)
            .build();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
