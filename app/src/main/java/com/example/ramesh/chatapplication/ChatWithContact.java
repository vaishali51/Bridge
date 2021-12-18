package com.example.ramesh.chatapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

public class ChatWithContact extends AppCompatActivity {

    String currentUserName;
    private FirebaseListAdapter<ChatMessage> adapter;
    SharedPreferences sharedPreferences;
    public RelativeLayout chtbx;
    String Target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        populateCurrentUser();

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        displayChatMessages();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText message = (EditText) findViewById(R.id.input);
                //Read the input field and push the new Instance
                //of chat message to Firebase Database
                ChatMessage chatMessage = new ChatMessage(message.getText().toString(),
                        currentUserName);
                chatMessage.setLanguage(sharedPreferences.getString("language", "English"));
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child(getIntent().getStringExtra("currentUserId")).push().setValue(chatMessage);
                FirebaseDatabase.getInstance().getReference().child("Users").child(getIntent().getStringExtra("currentUserId")).child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().setValue(chatMessage);

                //clear the input
                message.setText("");
            }
        });

    }

    private void populateCurrentUser() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("Name").getValue(String.class);

                currentUserName = name;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void displayChatMessages() {

        ListView list = (ListView) findViewById(R.id.list_of_messages);
        adapter = new FirebaseListAdapter<ChatMessage>(ChatWithContact.this, ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Chats").child(getIntent().getStringExtra("currentUserId"))) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Get references to the view of message
                chtbx = (RelativeLayout) v.findViewById(R.id.chat_box);
                final TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);

                String name = model.getMessageUser();
                //String currentName=  (FirebaseAuth.getInstance().getCurrentUser().getDisplayName())!= null?FirebaseAuth.getInstance().getCurrentUser().getDisplayName():"";
                if (!(FirebaseAuth.getInstance().getCurrentUser() != null && currentUserName.equals(name))) {
                    chtbx.setBackground(getResources().getDrawable(R.drawable.chtbox_2));
                    chtbx.setGravity(Gravity.END);
                } else {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)chtbx.getLayoutParams();
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    chtbx.setLayoutParams(params);

                    RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)messageUser.getLayoutParams();
                    params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params2.setMarginEnd(25);
                    messageUser.setLayoutParams(params2);
                }

                //set values
                //messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                //messageTime.setText(java.text.DateFormat.getInstance().format(model.getMessageTime()));
                messageTime.setText(model.getMessageTime());

                //Format the time before showing it
                //messageTime.setText(model.getMessageTime(), DateFormat.format("dd-MM-yyyy(HH:mm:ss)"));
                String API_KEY = "trnsl.1.1.20180421T080208Z.a562b322b8b83091.82550c2bbb57afa2ac0b5442374a359774762fea";
                String defaultUrl = "https://translate.yandex.net/api/v1.5/tr.json/translate";

                String language = getSharedPreferences("MyPrefs", MODE_PRIVATE).getString("language", "en");
                Target=findLangCode(language.toUpperCase());
                String stringUrl = defaultUrl + "?key=" + API_KEY + "&text=" + model.getMessageText() + "&lang=" + Target;
                Log.d("tj", "Target: " + Target);

                if(Objects.equals(Target, "ho")){
                    StringBuilder OutputString= new StringBuilder();
                    StringTokenizer st = new StringTokenizer(model.getMessageText(), " ");
                    int count = st.countTokens();
                    for(int i=0;i<(count/3)+1;i++)
                        OutputString.append("Hodor ");
                    messageText.setText(OutputString.toString());
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
                                        messageText.setText(OutputString);


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
                    RequestQueue requestQueue = Volley.newRequestQueue(ChatWithContact.this);
                    //adding the string request to request queue
                    requestQueue.add(stringRequest);
                }

            }
        };
        list.setAdapter(adapter);
    }

    //voice to text
    public void getSpeechInput2(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 11);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(ChatWithContact.this, "Welcome! " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                displayChatMessages();
            } else {
                Toast.makeText(ChatWithContact.this, "We couldn't sign you in..", Toast.LENGTH_SHORT).show();
                //finish();
                startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.mipmap.logo)
                        .setTheme(R.style.LoginTheme)
                        .build(), 10
                );
            }

        }
        if (requestCode == 2) {
            Log.d("Vaishali", "onActivityResult: request code 2");
            displayChatMessages();
        }
        EditText message = (EditText) findViewById(R.id.input);

        if (requestCode == 11) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                message.setText(result.get(0));
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();*/

            /*AuthUI.getInstance().signOut(ChatRoom.this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ChatRoom.this, "Signed out", Toast.LENGTH_SHORT).show();
                            //finish();
                            startActivityForResult(AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.mipmap.logo)
                                    .setTheme(R.style.LoginTheme)
                                    .build(), 10
                            );
                        }
                    });*/
        //}
         if (item.getItemId() == R.id.select_language) {
            SelectLanguage cdd = new SelectLanguage(ChatWithContact.this);
            cdd.show();
            Window window = cdd.getWindow();
            window.setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            cdd.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    displayChatMessages();
                }
            });
        }
        return true;
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
