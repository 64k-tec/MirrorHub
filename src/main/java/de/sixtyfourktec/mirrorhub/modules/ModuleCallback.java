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

import android.os.Handler;
import android.os.Looper;

public abstract class ModuleCallback<T extends Comparable> {

    private boolean postOnMainThread = true;
    protected T result = null;

    public void setPostOnMainThread(boolean pomt) {
        postOnMainThread = pomt;
    }

    public void postData(final T result) {

        // Check if the data has changed
        if (this.result != null && this.result.compareTo(result) == 0)
            return;
        this.result = result;

        if (postOnMainThread) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onData(result);
                }
            });
        } else
            onData(result);
    }

    public void postNoData() {
        this.result = null;

        if (postOnMainThread) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    onNoData();
                }
            });
        } else
            onNoData();
    }

    public abstract void onData(T result);
    public abstract void onNoData();
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
