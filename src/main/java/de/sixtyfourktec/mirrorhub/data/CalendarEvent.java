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

package de.sixtyfourktec.mirrorhub.data;

import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import android.support.annotation.IntDef;

public class CalendarEvent {

    @IntDef({NORMAL, ALLDAY, BIRTHDAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface CalendarEventType {}
    public static final int NORMAL = 0;
    public static final int ALLDAY = 1;
    public static final int BIRTHDAY = 2;

    @CalendarEventType
    public int type;
    public String title;
    public Calendar start;
    public Calendar end;
    public boolean allDay;

    public CalendarEvent(@CalendarEventType int type, String title, Calendar start, Calendar end, boolean allDay) {
        this.type = type;
        this.title = title;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
    }

    public String toString() {
        String s = "start=" + CalendarFormat.formatFull(start) +
            " end=" + CalendarFormat.formatFull(end) +
            " type=" + type +
            " all_day=" + allDay +
            " title=" + title;
        return s;
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
