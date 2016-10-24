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
import de.sixtyfourktec.mirrorhub.views.TrainJourneyView;
import de.sixtyfourktec.mirrorhub.views.CalendarView;
import de.sixtyfourktec.mirrorhub.views.MirrorHubView;
import de.sixtyfourktec.mirrorhub.views.ViewCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class OverlayView extends ViewFlipper implements ViewCallback, MirrorHubView {

    private ArrayList<MirrorHubView> views = new ArrayList<MirrorHubView>();
    private Context context;

    public OverlayView(Context context) {
        super(context);
        init(context);
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        final Animation in = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_in_left);
        final Animation out = AnimationUtils.loadAnimation(context,
                android.R.anim.slide_out_right);

        setInAnimation(in);
        setOutAnimation(out);

        views.add(new TrainJourneyView(context));
        views.add(new MusicView(context));

        for (int i = 0; i < views.size(); i++)
            views.get(i).addViewCallback(this);

        setFlipInterval(8*1000);
	}

    public void addViewCallback(ViewCallback vc) {
        // ignored for now
    }

    public void start() {
        for (int i = 0; i < views.size(); i++)
            views.get(i).start();
    }

    public void stop() {
        for (int i = 0; i < views.size(); i++)
            views.get(i).stop();
    }

    public void enableCalendarView() {
        MirrorHubView mhv = new CalendarView(context);
        mhv.addViewCallback(this);
        mhv.start();
    }

    @Override
    public void onData(View v) {
        if (indexOfChild(v) == -1) {
            addView(v, -1,
                    new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1));
            if (getChildCount() > 1)
                startFlipping();
        }
    }

    @Override
    public void onNoData(View v) {
        if (indexOfChild(v) != -1) {
            removeView(v);
            if (getChildCount() < 2)
                stopFlipping();
        }
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
