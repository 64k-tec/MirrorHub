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

import de.sixtyfourktec.mirrorhub.helpers.Downloader;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;

import java.util.ArrayList;

public abstract class Module<C extends Comparable> {

    private ArrayList<ModuleCallback<C>> callback_list = new ArrayList<ModuleCallback<C>>();
    protected Downloader dl = Downloader.getInstance();

    public abstract void start();
    public abstract void stop();

    public void addCallback(ModuleCallback<C> callback) {
        callback_list.add(callback);
    }

    public void removeCallback(ModuleCallback<C> callback) {
        callback_list.remove(callback);
    }

    protected void postData(C res) {
        for (int i = 0; i < callback_list.size(); i++)
            callback_list.get(i).postData(res);
    }

    protected void postNoData() {
        for (int i = 0; i < callback_list.size(); i++)
            callback_list.get(i).postNoData();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
