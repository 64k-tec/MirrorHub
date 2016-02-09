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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.os.Build;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;

@SuppressLint("SimpleDateFormat")
public class CalendarFormat {

    private static String getBestShortTimePatternLevel1() {
        Locale defLoc = Locale.getDefault();
        if (defLoc.equals(Locale.GERMANY) ||
            defLoc.equals(Locale.UK))
            return "HH:mm";
        else
            return "KK:mm a";
    }

    private static String getBestShortDatePatternLevel1() {
        Locale defLoc = Locale.getDefault();
        if (defLoc.equals(Locale.GERMANY))
            return "dd.MM.";
        else if (defLoc.equals(Locale.UK))
            return "dd/MM";
        else
            return "MM/dd";
    }

    @TargetApi(18)
    private static String getBestShortTimePatternLevel18() {
        return android.text.format.DateFormat.getBestDateTimePattern(
                Locale.getDefault(), "HHmm");
    }

    @TargetApi(18)
    private static String getBestShortDatePatternLevel18() {
        return android.text.format.DateFormat.getBestDateTimePattern(
                Locale.getDefault(), "ddMM");
    }

    private static String getBestShortTimePattern() {
        if (Build.VERSION.SDK_INT < 18)
            return getBestShortTimePatternLevel1();
        else
            return getBestShortTimePatternLevel18();
    }

    private static String getBestShortDatePattern() {
        if (Build.VERSION.SDK_INT < 18)
            return getBestShortDatePatternLevel1();
        else
            return getBestShortDatePatternLevel18();
    }

    public static final String formatFull(final Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    public static final String formatShortDate(final Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(getBestShortDatePattern());
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    public static final String formatShortTime(final Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(getBestShortTimePattern());
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }

    public static final String formatShortDateTime(final Calendar cal) {
        SimpleDateFormat df = new SimpleDateFormat(
                getBestShortDatePattern() + " " + getBestShortTimePattern());
        df.setTimeZone(cal.getTimeZone());
        return df.format(cal.getTime());
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
