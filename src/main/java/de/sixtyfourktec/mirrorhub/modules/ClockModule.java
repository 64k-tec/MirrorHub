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
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.Module;

import java.lang.System;
import java.util.Calendar;

import android.os.Handler;
import android.util.Log;

public class ClockModule extends Module<Calendar> {

    private static final String TAG = "ClockModule";
    private static ClockModule instance = null;

    private Handler handler;

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Calendar now = Calendar.getInstance();
            Log.i(TAG, "Update ...");
            Log.i(TAG, "time=" + CalendarFormat.formatFull(now));
            postData(now);
            /* Rerun when the this minute passed */
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, 1);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long delay = cal.getTimeInMillis() - System.currentTimeMillis();
            if (delay < 0)
                delay = 100;
            handler.postDelayed(runnableCode, delay);
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

    public static ClockModule getInstance() {
        if (instance == null)
            instance = new ClockModule();
        return instance;
    }

    private ClockModule() {
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
