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
import de.sixtyfourktec.mirrorhub.data.Forecast;
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.ForecastMetOfficeModule;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForecastView extends AnimationSwitcher {

    private ForecastMetOfficeModule module;

    private class ForecastViewItem extends LinearLayout {
        public ForecastViewItem(Context context, Forecast f) {
            super(context);
            setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
            setGravity(Gravity.CENTER);
            setOrientation(LinearLayout.VERTICAL);

            inflate(context, R.layout.forecastviewitem, this);

            TextView time = (TextView)findViewById(R.id.time);
            time.setText(CalendarFormat.formatShortTime(f.time));
            ImageView icon = (ImageView)findViewById(R.id.icon);
            icon.setImageLevel(f.code);
            TextView temperature = (TextView)findViewById(R.id.temperature);
            temperature.setText(String.format(
                        getResources().getString(R.string.forecast_temperature), f.temperature));
        }
    }

    private class mcb extends ModuleCallback<ArrayList<Forecast>> {
        public void onData(ArrayList<Forecast> list) {
            LinearLayout l = new LinearLayout(getContext());
            for (int i = 0; i < list.size(); i++) {
                l.addView(new ForecastViewItem(getContext(), list.get(i)));
            }
            ForecastView.this.exchangeView(l);
        }
        public void onNoData() {
            ForecastView.this.reset();
        }
    };

    public ForecastView(Context context) {
        super(context);
        init();
    }

    public ForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        module = ForecastMetOfficeModule.getInstance();
        module.addCallback(new mcb());
    }

    public void start() {
        module.start();
    }

    public void stop() {
        module.stop();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
