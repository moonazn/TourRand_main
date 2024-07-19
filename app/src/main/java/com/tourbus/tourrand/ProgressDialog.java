package com.tourbus.tourrand;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.PulseRing;
import com.github.ybq.android.spinkit.style.RotatingCircle;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context)
    {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loading);
        Sprite rotatingCircle = new RotatingCircle();
        progressBar.setIndeterminateDrawable(rotatingCircle);
    }
}
