package com.example.ramesh.chatapplication;

import android.app.Activity;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Created by hp on 4/21/2018.
 */

public class SelectLanguage extends Dialog {

    Spinner spinnerLang;
    SharedPreferences sharedPreferences;
    Button ok;


    public SelectLanguage(@NonNull Context context) {
        super(context);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.select_language);

        sharedPreferences = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        spinnerLang = (Spinner) findViewById(R.id.select_lang_spinner);
        spinnerLang.setSelection(sharedPreferences.getInt("position", 0));
        ok = (Button) findViewById(R.id.btn_ok);

        spinnerLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lang = (String) parent.getItemAtPosition(position);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("language", lang);
                editor.putInt("position",position);
                editor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}
