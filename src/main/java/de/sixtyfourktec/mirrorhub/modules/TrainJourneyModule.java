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
import de.sixtyfourktec.mirrorhub.data.TrainJourney;
import de.sixtyfourktec.mirrorhub.data.ComparableList;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.modules.NationalRailModule;
import de.sixtyfourktec.mirrorhub.modules.TflModule;

import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;

import android.os.Handler;
import android.util.Log;

public class TrainJourneyModule extends Module<ComparableList<TrainJourney>> {

    private static final String TAG = "TrainJourneyModule";
    private static TrainJourneyModule instance = null;

    private static final int maxEvents = 5;
    private static final int eventInterval = 60 * 2; // 2 mins

    private Handler handler;
    private NationalRailModule nr;
    private TflModule tfl;
    private ComparableList<TrainJourney> list;
    private int updated;

    private class tcb extends ModuleCallback<ComparableList<TrainJourney>> {
        @Override
        public void onData(ComparableList<TrainJourney> l)
        {
            synchronized(this) {
                list.addAll(l);
                updated++;
                submit();
            }
        }
        @Override
        public void onNoData() {
            synchronized(this) {
                updated++;
                submit();
            }
        }

        private void submit() {
            if (updated >= 2) {
                Collections.sort(list, new Comparator<TrainJourney>() {
                    @Override
                    public int compare(TrainJourney o1, TrainJourney o2) {
                        return o1.departureTime.compareTo(o2.departureTime);
                    }
                });
                /* Clamp the size of the list to maxEvents */
                ComparableList<TrainJourney> ll = new ComparableList<TrainJourney>(maxEvents);
                for (int i=0; i < list.size() && ll.size() < maxEvents; i++) {
                    ll.add(list.get(i));
                }
                for (int i=0; i < ll.size(); i++)
                    Log.i(TAG, ll.get(i).toString());
                if (ll.size() > 0)
                    TrainJourneyModule.this.postData(ll);
                else
                    TrainJourneyModule.this.postNoData();
            }
        }
    };

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Log.i(TAG, "Update ...");
            list = new ComparableList<TrainJourney>();
            updated = 0;
            tfl.start();
            nr.start();
            /* Rerun in 2 mins */
            handler.postDelayed(runnableCode, eventInterval * 1000);
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

    public static TrainJourneyModule getInstance() {
        if (instance == null)
            instance = new TrainJourneyModule();
        return instance;
    }

    public TrainJourneyModule() {
        tcb cb = new tcb();
        cb.setPostOnMainThread(false);

        nr = NationalRailModule.getInstance();
        nr.addCallback(cb);
        tfl = TflModule.getInstance();
        tfl.addCallback(cb);
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
