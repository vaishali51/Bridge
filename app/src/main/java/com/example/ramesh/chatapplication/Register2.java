package com.example.ramesh.chatapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Register2 extends AppCompatActivity {

    private Button submit;
    TextView name;
    AutoCompleteTextView country;
    private Spinner spinner;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);
        submit = findViewById(R.id.regsubmit);
        country = findViewById(R.id.country);
        name = findViewById(R.id.name_textview);
        spinner = findViewById(R.id.spinner);
        radioGroup = findViewById(R.id.gender_radio_group);

        String[] stringArray = getResources().getStringArray(R.array.country_data);
        String[] countryNameList = new String[stringArray.length];


        for (int i=0; i<stringArray.length; i++){
            String numberOnly= stringArray[i].trim().substring(0, stringArray[i].trim().indexOf(' '));
            countryNameList[i] = numberOnly;
        }

        ArrayAdapter<String> autocompletetextAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, countryNameList);

        country.setAdapter(autocompletetextAdapter);

        String[] prof = getResources().getStringArray(R.array.profession_array);
        ArrayAdapter<String> professionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, prof);
        spinner.setAdapter(professionAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty()){
                    Toast.makeText(Register2.this, "Enter name", Toast.LENGTH_SHORT).show();
                    return;
                }

        Intent intentsubmit = new Intent(Register2.this,MobileVerificationActivity.class);
        intentsubmit.putExtra("Phone", getIntent().getExtras().getString("Phone"));
        intentsubmit.putExtra("Country", country.getText().toString());
        intentsubmit.putExtra("Profession", spinner.getSelectedItem().toString());
        intentsubmit.putExtra("Name", name.getText().toString());
        //startActivity(appointment_intent, ActivityOptions.makeSceneTransitionAnimation(Register2.this).toBundle());

                int selectedId = radioGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) findViewById(selectedId);

                intentsubmit.putExtra("Gender", radioButton.getText().toString());
                startActivity(intentsubmit);

            }
        });

        //Spinner
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );
    }
}
