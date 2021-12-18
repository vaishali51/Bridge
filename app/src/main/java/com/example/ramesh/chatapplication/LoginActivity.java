package com.example.ramesh.chatapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    CardView loginButton;
    EditText phoneNo;
    private static final String TAG = "Vaishali";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (CardView) findViewById(R.id.card_loginbtn);
        phoneNo = (EditText) findViewById(R.id.phone);
        spinner = findViewById(R.id.spinner_code);

        populateSpinner();

        mAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalLibrary.checkForEmpty(phoneNo.getText().toString())){
                    Toast.makeText(LoginActivity.this, "Enter mobile no to proceed", Toast.LENGTH_SHORT).show();
                } else if (!GlobalLibrary.isValidMobile(phoneNo.getText().toString())){
                    Toast.makeText(LoginActivity.this, "Enter a valid mobile no", Toast.LENGTH_SHORT).show();
                } else {
                    final String phone = spinner.getSelectedItem().toString() + phoneNo.getText().toString();

                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            found :
                            {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    String phoneNos = postSnapshot.child("Phone").getValue(String.class);
                                    if (phone.equalsIgnoreCase(phoneNos)) {
                                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                                spinner.getSelectedItem().toString() + phoneNo.getText().toString(),        // Phone number to verify
                                                60,                 // Timeout duration
                                                TimeUnit.SECONDS,   // Unit of timeout
                                                LoginActivity.this,               // Activity (for callback binding)
                                                mCallbacks);        // OnVerificationStateChangedCallbacks
                                        break found;
                                    }
                                }

                                Intent intent = new Intent(LoginActivity.this, Register2.class);
                                intent.putExtra("Phone", phone);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);

                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null) {
            Intent intent = new Intent(LoginActivity.this, ContactsList.class);
            startActivity(intent);
            finish();
        }
    }

    private void populateSpinner() {
        String[] list = getResources().getStringArray(R.array.country_data);
        String[] codeList = new String[list.length];

        Log.d(TAG, "onCreate: "+list[0]);

        for (int i=0; i<list.length; i++){
            String numberOnly= "+" + list[i].replaceAll("[^0-9]", "");
            codeList[i] = numberOnly;
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(LoginActivity.this, android.R.layout.simple_list_item_1, codeList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Toast.makeText(LoginActivity.this, "Successfully signed in", Toast.LENGTH_SHORT).show();

                            // ...
                            Intent intent = new Intent(LoginActivity.this, ContactsList.class);
                            startActivity(intent);
                            finish();

                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(LoginActivity.this, "Code invalid", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }
}
