package com.tourbus.tourrand;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;

public class InviteDialog extends Dialog {
    private Button positiveBtn;
    private Button negativeBtn;
    private MainActivity mainActivity;

    public InviteDialog(@NonNull Context context) {
        super(context);
        this.mainActivity = (MainActivity) context;
    }
//https://velog.io/@dooo_it_ly/AndroidJava-Dialog-Custom
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
