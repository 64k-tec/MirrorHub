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
import de.sixtyfourktec.mirrorhub.data.CalendarEvent;
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.Module;

import java.lang.System;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.provider.CalendarContract;
import android.util.Log;

import android.annotation.TargetApi;

@TargetApi(14)
public class CalendarModule extends Module<ArrayList<CalendarEvent>> {

    private static final String TAG = "CalendarModule";
    private static CalendarModule instance = null;

    private static final int maxHoursInFuture = 24 * 30; // roughly a month
    private static final int maxEvents = 5;
    private static final int eventIntervalSecs = 60 * 2; // 2 mins

    private static final String[] EVT_PROJECTION = new String[] {
        Events.CALENDAR_ID,
        Events.TITLE,
        Instances.BEGIN,
        Instances.END,
        Events.ALL_DAY,
    };

    private static final int EVT_CAL_ID  = 0;
    private static final int EVT_TITLE   = 1;
    private static final int EVT_BEGIN   = 2;
    private static final int EVT_END     = 3;
    private static final int EVT_ALL_DAY = 4;

    private static final String[] CAL_PROJECTION = new String[] {
        Calendars._ID,
        Calendars.NAME,
    };

    private static final int CAL_ID      = 0;
    private static final int CAL_NAME    = 1;

    private Context ctx;
    private Handler handler;

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Update ...");
            try {
                /* Query all calendars */
                Cursor calCursor = ctx.getContentResolver().query(Calendars.CONTENT_URI,
                        CAL_PROJECTION,
                        null, null, null);

                /* Query all events of all calendars in the given time range */
                Calendar timeStart = Calendar.getInstance();
                Calendar timeEnd = Calendar.getInstance();
                timeEnd.add(Calendar.HOUR, maxHoursInFuture);
                Uri.Builder eventsUriBuilder = Instances.CONTENT_URI.buildUpon();
                ContentUris.appendId(eventsUriBuilder, timeStart.getTimeInMillis());
                ContentUris.appendId(eventsUriBuilder, timeEnd.getTimeInMillis());
                Uri eventsUri = eventsUriBuilder.build();
                Cursor eventCursor = ctx.getContentResolver().query(eventsUri,
                        EVT_PROJECTION,
                        null, null, Instances.BEGIN + " ASC");
                ArrayList<CalendarEvent> list = new ArrayList<CalendarEvent>(maxEvents);
                if(eventCursor.getCount() > 0 && eventCursor.moveToFirst()) {
                    do {
                        final TimeZone utc = TimeZone.getTimeZone("UTC");
                        Calendar s = Calendar.getInstance(utc);
                        s.setTimeInMillis(eventCursor.getLong(EVT_BEGIN));
                        Calendar s1 = CalendarFormat.convertToTimeZone(s, TimeZone.getDefault());
                        Calendar e = Calendar.getInstance(utc);
                        e.setTimeInMillis(eventCursor.getLong(EVT_END));
                        Calendar e1 = CalendarFormat.convertToTimeZone(e, TimeZone.getDefault());

                        int type = CalendarEvent.NORMAL;
                        if (!eventCursor.getString(EVT_ALL_DAY).equals("0"))
                            type = CalendarEvent.ALLDAY;
                        /* Check which calendar this event belongs to and if
                         * this is the birthday calendar. */
                        if(calCursor.getCount() > 0 && calCursor.moveToFirst()) {
                            do {
                                if (calCursor.getLong(CAL_ID) == eventCursor.getLong(EVT_CAL_ID)) {
                                    if (calCursor.getString(CAL_NAME).equals(BuildConfig.CALENDAR_BIRTHDAY_CAL))
                                        type = CalendarEvent.BIRTHDAY;
                                    break;
                                }
                            } while(calCursor.moveToNext());
                        }

                        CalendarEvent event = new CalendarEvent(
                                type,
                                eventCursor.getString(EVT_TITLE),
                                s1,
                                e1,
                                !eventCursor.getString(EVT_ALL_DAY).equals("0"));

                        list.add(event);
                        Log.i(TAG, event.toString());
                    } while(eventCursor.moveToNext() && list.size() < maxEvents);
                }
                eventCursor.close();
                calCursor.close();
                /* Broadcast the new events list */
                if (list.size() > 0)
                    postData(list);
                else
                    postNoData();
            } catch(Exception e) {
                Log.e(TAG, "Error: " + e.toString());
                postNoData();
            }
            /* Rerun after interval time */
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

    public static CalendarModule getInstance(Context c) {
        if (instance == null)
            instance = new CalendarModule(c);
        return instance;
    }

    private CalendarModule(Context c) {
        ctx = c;
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
