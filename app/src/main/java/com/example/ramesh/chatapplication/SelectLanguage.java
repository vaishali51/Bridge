package com.example.ramesh.chatapplication;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.widget.Button;

/**
 * Created by hp on 4/21/2018.
 */

public class SelectLanguage extends Dialog {
    public SelectLanguage(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_language);

    }
}
