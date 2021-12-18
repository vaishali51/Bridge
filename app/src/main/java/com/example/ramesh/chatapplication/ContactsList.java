package com.example.ramesh.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContactsList extends AppCompatActivity {

    private ArrayList<UserDetails> users = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private UsersAdapter mentorsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_list);

        mRecyclerView = findViewById(R.id.recycler);
        getUsers();

    }

    private void getUsers() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Log.d("Vaishali", "onDataChange: "+postSnapshot.getKey()+ " "+user.getUid());
                    if (!user.getUid().equals(postSnapshot.getKey())) {

                        //if (status.equals("Teacher")) {
                        String id = postSnapshot.child("Country").getValue(String.class);
                        String name = postSnapshot.child("Name").getValue(String.class);
                        String url = postSnapshot.child("Gender").getValue(String.class);
                        String experience = postSnapshot.child("Phone").getValue(String.class);
                        String fees = postSnapshot.child("Profession").getValue(String.class);
                        String uid = postSnapshot.child("UserID").getValue(String.class);

                        UserDetails teacherDetails = new UserDetails();

                        teacherDetails.setUID(postSnapshot.getKey());
                        teacherDetails.setCountry(id);
                        teacherDetails.setGender(url);
                        teacherDetails.setName(name);
                        teacherDetails.setPhone(experience);
                        teacherDetails.setProfession(fees);
                        users.add(teacherDetails);
                    }
                    //}
                }

                mRecyclerView.setLayoutManager(new GridLayoutManager(ContactsList.this, 1));
                mentorsAdapter = new UsersAdapter(ContactsList.this, users);
                mRecyclerView.setAdapter(mentorsAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signOut();

            Intent intent = new Intent(ContactsList.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return true;
    }

        }

