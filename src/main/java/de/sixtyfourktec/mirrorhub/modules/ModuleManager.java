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

import de.sixtyfourktec.mirrorhub.modules.CalendarModule;
import de.sixtyfourktec.mirrorhub.modules.ForecastMetOfficeModule;
import de.sixtyfourktec.mirrorhub.modules.SunTimesModule;
import de.sixtyfourktec.mirrorhub.modules.TimeToWorkModule;
import de.sixtyfourktec.mirrorhub.modules.TflModule;
import de.sixtyfourktec.mirrorhub.modules.NationalRailModule;
import de.sixtyfourktec.mirrorhub.modules.MusicModule;
import de.sixtyfourktec.mirrorhub.modules.TrainJourneyModule;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.Exception;

import android.content.Context;
import android.support.annotation.IntDef;

public class ModuleManager {

    private static final String TAG = "ModuleManager";
    private static ModuleManager instance = null;
    private Context ctx;

    private CalendarModule calendarModule;
    private ForecastMetOfficeModule forecastMetOfficeModule;
    private SunTimesModule sunTimesModule;
    private TimeToWorkModule timeToWorkModule;
    private TflModule tflModule;
    private NationalRailModule nationalRailModule;
    private MusicModule musicModule;
    private TrainJourneyModule trainJourneyModule;

    public static ModuleManager init(Context c) {
        instance = new ModuleManager(c);

        return instance;
    }

    public static ModuleManager getInstance() throws Exception {
        if (instance == null)
            throw new Exception("ModuleManager wasn't initialized. Did you call init?");

        return instance;
    }

    public void start() {
        forecastMetOfficeModule.start();
        sunTimesModule.start();
        timeToWorkModule.start();
        tflModule.start();
        nationalRailModule.start();
        musicModule.start();
        trainJourneyModule.start();
    }

    public void stop() {
        calendarModule.stop();
        forecastMetOfficeModule.stop();
        sunTimesModule.stop();
        timeToWorkModule.stop();
        tflModule.stop();
        nationalRailModule.stop();
        musicModule.stop();
        trainJourneyModule.stop();
    }

    public void enableCalendarModule() {
        calendarModule.start();
    }

    private ModuleManager(Context c) {
        ctx = c;
        calendarModule = CalendarModule.getInstance(c);
        forecastMetOfficeModule = ForecastMetOfficeModule.getInstance();
        sunTimesModule = SunTimesModule.getInstance();
        timeToWorkModule = TimeToWorkModule.getInstance();
        tflModule = TflModule.getInstance();
        nationalRailModule = NationalRailModule.getInstance();
        musicModule = MusicModule.getInstance();
        trainJourneyModule = TrainJourneyModule.getInstance();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
