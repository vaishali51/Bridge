package com.example.ramesh.chatapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    EditText MyInputText;
    Spinner MyTarget;
    Button MyTranslateButton;
    TextView MyOutputText;
    String Target;
    private TextToSpeech textToSpeech;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyInputText = (EditText) findViewById(R.id.InputText);
        MyTarget = (Spinner) findViewById(R.id.Target);
        MyTranslateButton = (Button) findViewById(R.id.TranslateButton);
        MyOutputText = (TextView) findViewById(R.id.OutputText);

        MyTranslateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String InputString= MyInputText.getText().toString();
                //String Target=MyTarget.getText().toString();
                String API_KEY = "trnsl.1.1.20180421T080208Z.a562b322b8b83091.82550c2bbb57afa2ac0b5442374a359774762fea";
                String defaultUrl="https://translate.yandex.net/api/v1.5/tr.json/translate";
                Target = findLangCode(MyTarget.getSelectedItem().toString().toUpperCase());
                String stringUrl=defaultUrl+"?key="+API_KEY+"&text="+InputString+"&lang="+Target;

                if(Objects.equals(Target, "ho")){
                    StringBuilder OutputString= new StringBuilder();
                    StringTokenizer st = new StringTokenizer(InputString, " ");
                    int count = st.countTokens();
                    for(int i=0;i<(count/3)+1;i++)
                        OutputString.append("Hodor ");
                    MyOutputText.setText(OutputString.toString());
                }

                else{
                StringRequest stringRequest = new StringRequest(Request.Method.GET, stringUrl,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    String OutputString;
                                    //getting the whole json object from the response
                                    JSONObject obj = new JSONObject(response);
                                    JSONArray arr = obj.getJSONArray("text");
                                    OutputString = arr.getString(0);
                                    MyOutputText.setText(OutputString);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            },
                                    new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    //displaying the error in toast if occurs

                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                                    //TextView error= findViewById(R.id.error);
                                    //error.setText(e.getMessage());
                                }
                            });
                //creating a request queue
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                //adding the string request to request queue
                requestQueue.add(stringRequest);
            }}
        });
        //Text to speech
        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getspeakInput(View view) {
        String listen = MyOutputText.getText().toString();
        textToSpeech.speak(listen, TextToSpeech.QUEUE_FLUSH, null, null);
    }


        //voice to text
    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 10);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MyInputText.setText(result.get(0));
                }
                break;
        }
    }








    public static String findLangCode(String lang){
        String langShort = "en";

        if(Objects.equals(lang, "AFRIKAANS")){
            langShort="af";
        }
        if(Objects.equals(lang, "ALBANIAN")){
            langShort="sq";
        }
        if(Objects.equals(lang, "AMHARIC")){
            langShort="am";
        }
        if(Objects.equals(lang, "ARABIC")){
            langShort="ar";
        }
        if(Objects.equals(lang, "ARMENIAN")){
            langShort="hy";
        }
        if(Objects.equals(lang, "AZERBAIJANI")){
            langShort="az";
        }
        if(Objects.equals(lang, "BASHKIR")){
            langShort="ba";
        }
        if(Objects.equals(lang, "BASQUE")){
            langShort="eu";
        }
        if(Objects.equals(lang, "BENGALI")){
            langShort="bn";
        }
        if(Objects.equals(lang, "BELARUSIAN")){
            langShort="be";
        }
        if(Objects.equals(lang, "BOSNINAN")){
            langShort="bs";
        }
        if(Objects.equals(lang, "BULGARIAN")){
            langShort="bg";
        }
        if(Objects.equals(lang, "CATALAN")){
            langShort="ca";
        }
        if(Objects.equals(lang, "CHINESE")){
            langShort="zh";
        }
        if(Objects.equals(lang, "CROATIAN")){
            langShort="hr";
        }
        if(Objects.equals(lang, "CZECH")){
            langShort="cs";
        }
        if(Objects.equals(lang, "DANISH")){
            langShort="da";
        }
        if(Objects.equals(lang, "DUTCH")){
            langShort="nl";
        }
        if(Objects.equals(lang, "ENGLISH")){
            langShort="en";
        }
        if(Objects.equals(lang, "ESTONIAN")){
            langShort="et";
        }
        if(Objects.equals(lang, "FINNISH")){
            langShort="fi";
        }
        if(Objects.equals(lang, "FRENCH")){
            langShort="fr";
        }
        if(Objects.equals(lang, "GALICIAN")){
            langShort="gl";
        }
        if(Objects.equals(lang, "GEORGIAN")){
            langShort="ka";
        }
        if(Objects.equals(lang, "GERMAN")){
            langShort="de";
        }
        if(Objects.equals(lang, "GREEK")){
            langShort="el";
        }
        if(Objects.equals(lang, "GUJARATI")){
            langShort="gu";
        }
        if(Objects.equals(lang, "HAITIAN")){
            langShort="ht";
        }
        if(Objects.equals(lang, "HEBREW")){
            langShort="he";
        }
        if(Objects.equals(lang, "HINDI")){
            langShort="hi";
        }
        if(Objects.equals(lang, "HODOR")){
            langShort="ho";
        }
        if(Objects.equals(lang, "HUNGARIAN")){
            langShort="hu";
        }
        if(Objects.equals(lang, "ICELANDIC")){
            langShort="is";
        }
        if(Objects.equals(lang, "INDONESIAN")){
            langShort="id";
        }
        if(Objects.equals(lang, "IRISH")){
            langShort="ga";
        }
        if(Objects.equals(lang, "ITALIAN")){
            langShort="it";
        }
        if(Objects.equals(lang, "JAPANESE")){
            langShort="ja";
        }
        if(Objects.equals(lang, "JAVANESE")){
            langShort="jv";
        }
        if(Objects.equals(lang, "KANNADA")){
            langShort="kn";
        }
        if(Objects.equals(lang, "KOREAN")){
            langShort="ko";
        }
        if(Objects.equals(lang, "LATIN")){
            langShort="la";
        }
        if(Objects.equals(lang, "LATVIAN")){
            langShort="lv";
        }
        if(Objects.equals(lang, "LITHUANIAN")){
            langShort="lt";
        }
        if(Objects.equals(lang, "MACEDONIAN")){
            langShort="mk";
        }
        if(Objects.equals(lang, "MALAY")){
            langShort="ms";
        }
        if(Objects.equals(lang, "MALAYALAM")){
            langShort="ml";
        }
        if(Objects.equals(lang, "MALTESE")){
            langShort="mt";
        }
        if(Objects.equals(lang, "MARATHI")){
            langShort="mr";
        }
        if(Objects.equals(lang, "NEPALI")){
            langShort="ne";
        }
        if(Objects.equals(lang, "NORWEGIAN")){
            langShort="no";
        }
        if(Objects.equals(lang, "PERSIAN")){
            langShort="fa";
        }
        if(Objects.equals(lang, "POLISH")){
            langShort="pl";
        }
        if(Objects.equals(lang, "PORTUGUESE")){
            langShort="pt";
        }
        if(Objects.equals(lang, "PUNJABI")){
            langShort="pa";
        }
        if(Objects.equals(lang, "ROMANIAN")){
            langShort="ro";
        }
        if(Objects.equals(lang, "RUSSIAN")){
            langShort="ru";
        }
        if(Objects.equals(lang, "SERBIAN")){
            langShort="sr";
        }
        if(Objects.equals(lang, "SLOVAK")){
            langShort="sk";
        }
        if(Objects.equals(lang, "SLOVENIAN")){
            langShort="sl";
        }
        if(Objects.equals(lang, "SPANISH")){
            langShort="es";
        }
        if(Objects.equals(lang, "SWAHILI")){
            langShort="sw";
        }
        if(Objects.equals(lang, "SWEDISH")){
            langShort="sv";
        }
        if(Objects.equals(lang, "TAMIL")){
            langShort="ta";
        }
        if(Objects.equals(lang, "TELUGU")){
            langShort="te";
        }
        if(Objects.equals(lang, "THAI")){
            langShort="th";
        }
        if(Objects.equals(lang, "TURKISH")){
            langShort="tr";
        }
        if(Objects.equals(lang, "UKRAINIAN")){
            langShort="uk";
        }
        if(Objects.equals(lang, "URDU")){
            langShort="ur";
        }
        if(Objects.equals(lang, "VIETNAMESE")){
            langShort="vi";
        }
        if(Objects.equals(lang, "WELSH")){
            langShort="cy";
        }
        if(Objects.equals(lang, "YIDDISH")){
            langShort="yi";
        }

        return langShort;
    }

}
