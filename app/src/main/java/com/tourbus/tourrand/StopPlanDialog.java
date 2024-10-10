package com.tourbus.tourrand;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

public class StopPlanDialog  extends Dialog {
    private PlanViewActivity planViewActivity;
    public StopPlanDialog(@NonNull PlanViewActivity activity) {
        super(activity);
        this.planViewActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_stopplan);

        Button closeBtn = findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planViewActivity.hideRerollButton();
                dismiss();
            }
        });
    }
}
