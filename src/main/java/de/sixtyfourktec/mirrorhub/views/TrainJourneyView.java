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
import de.sixtyfourktec.mirrorhub.data.TrainJourney;
import de.sixtyfourktec.mirrorhub.data.ComparableList;
import de.sixtyfourktec.mirrorhub.helpers.CalendarFormat;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.modules.TrainJourneyModule;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TrainJourneyView extends AnimationSwitcher<TrainJourneyModule> {

    private class TrainJourneyViewItem extends TableLayout {
        public TrainJourneyViewItem(Context context, TrainJourney tj) {
            super(context);
            setPadding(5, 0, 5, 0);

            inflate(context, R.layout.trainjourneyviewitem, this);

            ImageView icon = (ImageView)findViewById(R.id.icon);
            icon.setImageLevel(tj.type);
            TextView time = (TextView)findViewById(R.id.time);
            time.setText(CalendarFormat.formatShortTime(tj.departureTime));
            TextView destination = (TextView)findViewById(R.id.destination);
            destination.setText(tj.destination);
            TextView exp_time = (TextView)findViewById(R.id.exp_time);
            if (tj.onTime)
                exp_time.setText(getResources().getString(R.string.trainjourney_on_time));
            else
            {
                if (tj.expectedDepartureTime != null)
                    exp_time.setText(CalendarFormat.formatShortTime(tj.expectedDepartureTime));
                else if (tj.disruptionDesc != null) {
                    exp_time.setText(tj.disruptionDesc);
                    /* Necessary to make marquee work */
                    exp_time.setSelected(true);
                }
                else
                    exp_time.setText(getResources().getString(R.string.trainjourney_delays));
            }
        }
    }

    private class mcb extends ModuleCallback<ComparableList<TrainJourney>> {
        public void onData(ComparableList<TrainJourney> list) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.VERTICAL);
            for (int i = 0; i < list.size(); i++) {
                l.addView(new TrainJourneyViewItem(getContext(), list.get(i)));
            }
            TrainJourneyView.this.exchangeView(l);
        }
        public void onNoData() {
            TrainJourneyView.this.resetView();
        }
    };

    public TrainJourneyView(Context context) {
        super(context);
        init();
    }

    public TrainJourneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        module = TrainJourneyModule.getInstance();
        module.addCallback(new mcb());
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
