package com.example.emotionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emotionapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class NewUserActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText emailinput, passinput, usernameinput;
    private Button signupbtn;

    private List usernames;


    // I need to save all usernames because i couldnt find any method to check whether typed username exist or not and i dont want any confusion about usernames. I mean, i dont want same username between users.
    // I am gonna save all usernames in a list, after that i will save the username list to realtime database. Whenever any new user register, this method is gonna upload to list-data in the realtime database.
    // If you dont understand what i mean, just follow methods path one by one.


    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        emailinput = findViewById(R.id.emailsignupedittext);
        passinput = findViewById(R.id.passwordsignupedittext);
        signupbtn = findViewById(R.id.signupbutton);
        usernameinput = findViewById(R.id.username);
        usernames = new ArrayList();
        mDatabase = FirebaseDatabase.getInstance("https://emotions-e40fb-default-rtdb.europe-west1.firebasedatabase.app").getReference();



        // Initialize Firebase Auth
                mAuth = FirebaseAuth.getInstance();

                signupbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(emailinput.getText().toString().trim() != "" && passinput.getText().toString().trim() != ""){
                            usernames.clear();
                            usernameReader();


                        }
                        else{

                            Toast.makeText(NewUserActivity.this, "Fields cannot be empty!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    // This method read username list data in Firebase Realtime Database, add all results to usernames list.
    private void usernameReader() {
        try {
            mDatabase.child("users").child("username_list").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("TAGKAAN", "Error getting data", task.getException());
                    } else {
                        Log.d("TAGKAAN", String.valueOf(task.getResult().getValue()));
                        if(task.getResult().getValue() != null){
                            HashMap<Integer, String> hash = new HashMap<Integer, String>();
                            try{
                                hash  = (HashMap) (task.getResult().getValue());
                                usernames.addAll( hash.values() );
                            }catch (Exception e){
                                usernames.addAll((List) (task.getResult().getValue()));
                            }
                            if (usernameinput.getText().toString().trim() != "" && !usernames.isEmpty()) {
                                Log.d("TAGKAAN","here"+usernames.get(0).toString());
                                checkingIfusernameExist();
                            }
                            else{
                                Toast.makeText(NewUserActivity.this, "We are sorry! An Issue occured, can you try again.", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            checkingIfusernameExist();
                        }


                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    // This method uploads username list in Firebase Realtime Database
    private void usernameSaver(){
        usernames.add(usernameinput.getText().toString());
        mDatabase.child("users").child("username_list").removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAGKAAN", "The operation is complete.LOOK HERE!!!!"+usernames.size());
                            mDatabase.child("users").child("username_list").setValue(usernames)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("TAGKAAN", "The operation is complete.123131313123");

                                            } else {
                                                Log.d("TAGKAAN", task.getException().getMessage());
                                            }
                                        }
                                    });

                        } else {
                            Log.d("TAGKAAN", task.getException().getMessage());
                            Toast.makeText(NewUserActivity.this, "An Issue occured!!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    // This method is checking whether username already registered or not
    private void checkingIfusernameExist(){
        Predicate<String> isExist = number -> number.equals(usernameinput.getText().toString().trim()) ;
        if(usernames.stream().anyMatch(isExist)){
            Log.d("TAGKAAN",""+usernames.stream().anyMatch(isExist));
            Toast.makeText(NewUserActivity.this, "This username already exist, please type another else!",Toast.LENGTH_SHORT).show();
        }else{
            Log.d("TAGKAAN",""+usernames.stream().anyMatch(isExist));
            createAccount();

        }
        }

// This method is creating a new Account
public void createAccount(){
    mAuth.createUserWithEmailAndPassword(emailinput.getText().toString(), passinput.getText().toString())
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAGKAAN", "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        usernameSaver();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(usernameinput.getText().toString()).build();
                        user.updateProfile(profileUpdates);
                        Intent i = new Intent(NewUserActivity.this, MainActivity.class);
                        startActivity(i);
                        //updateUI(user); todo im gonna add!! --- i did
                    } else {
                        // If sign in fails, display a message to the user.
                        if(passinput.getText().toString().trim().length() < 8){
                            Toast.makeText(NewUserActivity.this, "Your password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(NewUserActivity.this, "If you sure about your Ethernet Connection!!! E-mail not valid or already exist!", Toast.LENGTH_LONG).show();
                        }

                        Log.w("TAGKAAN", "createUserWithEmail:failure", task.getException());
                        passinput.setText("");
                        // updateUI(null); todo im gonna add as well!! -- i did
                    }
                }

        });

    }
}