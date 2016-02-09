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

package de.sixtyfourktec.mirrorhub.views;

import de.sixtyfourktec.mirrorhub.R;
import de.sixtyfourktec.mirrorhub.modules.ClockModule;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import android.annotation.SuppressLint;

public class DateView extends AnimationSwitcher {

    private ClockModule module;

    private class mcb extends ModuleCallback<Calendar> {
        @SuppressLint("SimpleDateFormat")
        public Spanned getDay(Calendar cal) {
            SimpleDateFormat formatDayOfMonth = new SimpleDateFormat("EEEE");
            int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
            return Html.fromHtml(formatDayOfMonth.format(cal.getTime()) +
                    " the " + dayOfMonth + "<sup><small>" +
                    getDayOfMonthSuffix(dayOfMonth) + "</small></sup>");
        }

        private String getDayOfMonthSuffix(final int n) {
            if (n >= 11 && n <= 13) {
                return "th";
            }
            switch (n % 10) {
                case 1:
                    return "st";
                case 2:
                    return "nd";
                case 3:
                    return "rd";
                default:
                    return "th";
            }
        }
        public void onData(Calendar cal)
        {
            TextView view = (TextView)inflate(getContext(), R.layout.dateviewitem, null);
            view.setText(getDay(cal));
            DateView.this.exchangeView(view);
        }
        public void onNoData() {
            DateView.this.reset();
        }
    };

    public DateView(Context context) {
        super(context);
        init();
    }

    public DateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        module = ClockModule.getInstance();
        module.addCallback(new mcb());
        module.start();
    }

    public void start() {
        module.start();
    }

    public void stop() {
        module.stop();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
