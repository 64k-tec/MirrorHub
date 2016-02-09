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
import de.sixtyfourktec.mirrorhub.data.SunTimes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Handler;
import android.util.Log;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

public class SunTimesModule extends Module<SunTimes> {

    private static final String TAG = "SunTimesModule";
    private static SunTimesModule instance = null;

    private Handler handler;

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Update ...");
            SunTimes s;
            Calendar now = Calendar.getInstance();
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                    new Location(BuildConfig.SUNTIMES_LATITUDE,
                                 BuildConfig.SUNTIMES_LONGITUTE),
                                 BuildConfig.SUNTIMES_TIMEZONE);
            Calendar event = calculator.getOfficialSunriseCalendarForDate(now);
            if (now.before(event))
                /* Now is before sunrise today */
                s = new SunTimes(SunTimes.SUNRISE, event);
            else {
                event = calculator.getOfficialSunsetCalendarForDate(now);
                if (now.before(event))
                    /* Now is before sunset today */
                    s = new SunTimes(SunTimes.SUNSET, event);
                else {
                    /* Now is after sunset today, but before sunrise tomorrow */
                    now.set(Calendar.HOUR, 0);
                    now.set(Calendar.MINUTE, 0);
                    now.set(Calendar.SECOND, 1);
                    now.add(Calendar.DAY_OF_YEAR, 1);
                    event = calculator.getOfficialSunriseCalendarForDate(now);
                    s = new SunTimes(SunTimes.SUNRISE, event);
                }
            }

            Log.i(TAG, s.toString());
            postData(s);

            /* Rerun this when the current event have passed */
            long delay = (event.getTimeInMillis() - System.currentTimeMillis()) + 100;
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

    public static SunTimesModule getInstance() {
        if (instance == null)
            instance = new SunTimesModule();
        return instance;
    }

    private SunTimesModule() {
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
