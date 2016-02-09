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
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.ClockModule;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class TimeView extends AnimationSwitcher {

    private ClockModule module;

    private class mcb extends ModuleCallback<Calendar> {
        public void onData(Calendar cal)
        {
            TextView view = (TextView)inflate(getContext(), R.layout.timeviewitem, null);
            view.setText(CalendarFormat.formatShortTime(cal));
            TimeView.this.exchangeView(view);
        }
        public void onNoData() {
            TimeView.this.reset();
        }
    };

    public TimeView(Context context) {
        super(context);
        init();
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        module = ClockModule.getInstance();
        module.addCallback(new mcb());

        TextView view = (TextView)inflate(getContext(), R.layout.timeviewitem, null);
        exchangeView(view);
    }

    public void start() {
        module.start();
    }

    public void stop() {
        module.stop();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
