package com.example.emotionapp.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emotionapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    EditText emailinput, passwordinput;

    Button loginbutton, createnewuserbutton, anonymousloginbutton;



    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializes
        mAuth = FirebaseAuth.getInstance();
        emailinput = findViewById(R.id.emailEditText);
        passwordinput = findViewById(R.id.passwordEditText);
        loginbutton = findViewById(R.id.loginbutton);
        createnewuserbutton = findViewById(R.id.createnewuserbutton);
        anonymousloginbutton = findViewById(R.id.anonymousLoginButton);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null && currentUser.getDisplayName() != null){
            Intent i = new Intent(MainActivity.this, BaseActivity.class);
            startActivity(i);
            //reload();
            // TODO: 21.02.2024 reload method!! -- I did
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginbutton.setOnClickListener(v -> {
            if(!emailinput.getText().toString().trim().isEmpty() && !passwordinput.getText().toString().trim().isEmpty()){
                signIN();
            }
            else{
                Toast.makeText(MainActivity.this, "E-mail and password fields can't be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        createnewuserbutton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "You won't regret!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(MainActivity.this, NewUserActivity.class);
            startActivity(i);
        });
        anonymousloginbutton.setOnClickListener(v -> signInAnonymously());
    }

    public void signInAnonymously(){
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAGKAAN", "signInAnonymously:success");
                        Intent i = new Intent(MainActivity.this, BaseActivity.class);
                        startActivity(i);
                        //updateUI(user); todo Im gonna take care later  -- i did
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAGKAAN", "signInAnonymously:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        //updateUI(null); todo Im gonna take care later ---- i did
                    }
                });

    }

    public void signIN(){
        mAuth.signInWithEmailAndPassword(emailinput.getText().toString().trim(), passwordinput.getText().toString().trim())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAGKAAN", "signInWithEmail:success");
                        emailinput.setText("");
                        passwordinput.setText("");
                        Intent i = new Intent(MainActivity.this, BaseActivity.class);
                        startActivity(i);
                        //updateUI(user); todo im gonna here as well!! --- i did
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAGKAAN", "signInWithEmail:failure", task.getException());
                        Toast.makeText(MainActivity.this, "Authentication failed. Please Try Again!",
                                Toast.LENGTH_SHORT).show();
                        emailinput.setText("");
                        passwordinput.setText("");
                        //updateUI(null); todo im gonna here -- i did
                    }
                });
    }
}



