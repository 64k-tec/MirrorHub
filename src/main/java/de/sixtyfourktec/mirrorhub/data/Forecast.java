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

import java.util.Calendar;
import java.lang.Integer;

public class Forecast implements Comparable<Forecast> {

    public Calendar time;
    public String temperature;
    public int code;

    public Forecast(Calendar cal, String temp, String code) {
        this.time = cal;
        this.temperature = temp;
        this.code = Integer.parseInt(code);
    }

    public String toString() {
        return "time=" + CalendarFormat.formatFull(time) +
            " temperature=" + temperature +
            " code=" + code;
    }

    public int compareTo(Forecast other) {
        return toString().compareTo(other.toString());
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
