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
import de.sixtyfourktec.mirrorhub.data.MusicEvent;
import de.sixtyfourktec.mirrorhub.modules.ModuleCallback;
import de.sixtyfourktec.mirrorhub.modules.MusicModule;
import de.sixtyfourktec.mirrorhub.views.AnimationSwitcher;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MusicView extends AnimationSwitcher<MusicModule> {

    private class MusicViewItem extends TableLayout {
        public MusicViewItem(Context context, MusicEvent me) {
            super(context);
            setPadding(5, 0, 5, 0);

            inflate(context, R.layout.musicviewitem, this);

            ImageView icon = (ImageView)findViewById(R.id.icon);
            icon.setImageBitmap(me.cover);
            TextView artist = (TextView)findViewById(R.id.artist);
            artist.setText(me.artist);
            TextView album = (TextView)findViewById(R.id.album);
            album.setText(me.album);
            TextView title = (TextView)findViewById(R.id.title);
            title.setText(me.title);
        }
    }

    private class mcb extends ModuleCallback<MusicEvent> {
        public void onData(MusicEvent me) {
            LinearLayout l = new LinearLayout(getContext());
            l.setOrientation(LinearLayout.VERTICAL);
            l.addView(new MusicViewItem(getContext(), me));
            MusicView.this.exchangeView(l);
        }
        public void onNoData() {
            MusicView.this.resetView();
        }
    };

    public MusicView(Context context) {
        super(context);
        init();
    }

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        module = MusicModule.getInstance();
        module.addCallback(new mcb());
    }
}

/* ex: set tabstop=4 shiftwidth=4 expandtab: */
