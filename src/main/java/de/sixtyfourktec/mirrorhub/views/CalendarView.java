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
import de.sixtyfourktec.mirrorhub.data.CalendarEvent;
import de.sixtyfourktec.mirrorhub.data.ComparableList;
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.CalendarModule;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.util.Calendar;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class CalendarView extends AnimationSwitcher<CalendarModule> {

    private class CalendarViewItem extends TableLayout {
        public CalendarViewItem(Context context, CalendarEvent event) {
            super(context);
            setPadding(5, 0, 5, 0);

            inflate(context, R.layout.calendarviewitem, this);

            ImageView icon = (ImageView)findViewById(R.id.icon);
            icon.setImageLevel(event.type);

            String start;
            if (event.allDay)
                    start = CalendarFormat.formatShortDate(event.start);
            else
                    start = CalendarFormat.formatShortDateTime(event.start);

            TextView date = (TextView)findViewById(R.id.date);
            date.setText(start);

            TextView title = (TextView)findViewById(R.id.title);
            title.setText(event.title);
            /* Necessary to make marquee work */
            title.setSelected(true);
        }
    }

    public class mcb extends ModuleCallback<ComparableList<CalendarEvent>> {
        public void onData(ComparableList<CalendarEvent> list) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < list.size(); i++) {
                l.addView(new CalendarViewItem(getContext(), list.get(i)));
            }
            CalendarView.this.exchangeView(l);
        }
        public void onNoData() {
            CalendarView.this.resetView();
        }
    };

    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= 23) {
            module = CalendarModule.getInstance(getContext());
            module.addCallback(new mcb());
        }
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
