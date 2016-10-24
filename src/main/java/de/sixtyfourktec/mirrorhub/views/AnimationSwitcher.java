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

import de.sixtyfourktec.mirrorhub.views.ViewCallback;
import de.sixtyfourktec.mirrorhub.modules.Module;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

public class AnimationSwitcher<T extends Module> extends ViewSwitcher implements MirrorHubView {

    protected T module;
    protected View lastView;
    protected ViewCallback viewCallback;

    public void addViewCallback(ViewCallback vc) {
        this.viewCallback = vc;
    }

    public AnimationSwitcher(Context context) {
        super(context);
        init(context);
    }

    public AnimationSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        Animation in = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(context,
                android.R.anim.fade_out);

        setInAnimation(in);
        setOutAnimation(out);
    }

    public void exchangeView(View v) {
        LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1);
        addView(v, -1, p);
        showNext();
        removeView(lastView);
        lastView = v;
        if (viewCallback != null)
            viewCallback.onData(this);
    }

    public void resetView() {
        reset();
        if (viewCallback != null)
            viewCallback.onNoData(this);
    }

    public void start() {
        if (module != null)
            module.start();
    }

    public void stop() {
        if (module != null)
            module.stop();
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
