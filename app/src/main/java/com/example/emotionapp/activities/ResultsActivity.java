package com.example.emotionapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.emotionapp.R;
import com.example.emotionapp.fragments.FragmentResult;
import com.example.emotionapp.model.MyModel;
import com.example.emotionapp.roomdb.Quotes;
import com.example.emotionapp.fragments.PreviousResultsFragment;
import com.example.emotionapp.viewmodel.MyViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 255;


    public ResultsActivity() {
        super(R.layout.activity_results2);
    }

    private DatabaseReference mDatabase;

    MyViewModel myViewModel;
    private FirebaseAuth mAuth;

    ArrayList myOutput;
    String result;

    int item1, item2;

    FragmentManager fm;
    private boolean isFragmentDisplayed = false;


    private int point = 0;


    //For Save the results, user results will store in the local database, i used Room Database.
    private Quotes quotes;
    String image_url;
    Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results2);
        psychologyAnalysisMatcher();
        myOutput = new ArrayList<String>();
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().isAnonymous()){

        }
        Log.w("TAGKAAN", "ONCREATE CALLED");
        bundle = getIntent().getExtras();
        myViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);

        }

        WarningDialog.show(this, "We will not store your image because you are anonymous", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ResultsActivity.this, "Please register for all features", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //
    public void imageUrltaker() {

        if (bundle != null) {
            String messageFromActivity = bundle.getString("image_url");
            if (messageFromActivity != null && messageFromActivity.length() >= 1) {
                image_url = messageFromActivity;
                Toast.makeText(ResultsActivity.this, image_url, Toast.LENGTH_SHORT).show();
                quotes = new Quotes(result, image_url);
                addNewQuote();
                Bundle bundle = new Bundle();
                bundle.putString("data", image_url);
                loadFragment(new PreviousResultsFragment(), bundle);

            } else {
                Toast.makeText(ResultsActivity.this, "Image url couldnt be taken properly!!!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public class WarningDialog {
        public static void show(Context context, String message, final DialogInterface.OnClickListener listener) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            if(listener != null)
                                listener.onClick(dialog, id);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MenuItem menuItem2 = findViewById(R.id.action_signout);

        if (item.getItemId() == R.id.action_add) {
            Intent intent = new Intent(this, BaseActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.action_signout) {
            mAuth.signOut();
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.action_previous) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void addNewQuote() {
        if (quotes.getQuote() == null || quotes.getImage_url() == null) {
            Toast.makeText(ResultsActivity.this, "ONE ERROR OCCURRED SORRY!", Toast.LENGTH_LONG).show();
        } else {
            myViewModel.addNewQuotes(quotes);
        }
    }

    public void sender() {
        Intent i = new Intent(ResultsActivity.this, BaseActivity.class);
        startActivity(i);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.w("TAGKAAN", "ONSTART CALLED");
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance("https://emotions-e40fb-default-rtdb.europe-west1.firebasedatabase.app").getReference();
        writeFileText(currentUser);

    }


    public void writeFileText(FirebaseUser currentUser) {
        Log.w("TAGKAAN", "NOW TRYING TO WRITE");
        mDatabase.child("users").child(currentUser.getDisplayName().toString() + "-" + currentUser.getUid().toString()).setValue(point)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAGKAAN", "The operation is complete.");

                        } else {
                            Log.d("TAGKAAN", task.getException().getMessage());
                        }
                    }
                });
        Log.w("TAGKAAN", "WRITED AND NOW SENDING TO READFILE");
        readFileText(currentUser);

    }

    public void readFileText(FirebaseUser currentUser) {

        mDatabase.child("users").child(currentUser.getDisplayName().toString() + "-" + currentUser.getUid().toString()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("TAGKAAN", "Error getting data", task.getException());
                } else {
                    Log.d("TAGKAAN", String.valueOf(task.getResult().getValue()));
                }
            }
        });
        result = readfiletxt();
        Bundle bundle = new Bundle();
        bundle.putString("data", result);
        loadFragment(new FragmentResult(), bundle);

    }


    private String readfiletxt() {
        InputStream inputStream = null;
        try {
            inputStream = getResources().openRawResource(R.raw.psychlogydatas);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                myOutput.add(line);
            }

        } catch (IOException e) {
            // Exception
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                    return myOutput.get(point).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
            }
        }
        return myOutput.get(point).toString();
    }


    private void psychologyAnalysisMatcher() {
        double righteye = Double.parseDouble(MyModel.getRightEye());
        double lefteye = Double.parseDouble(MyModel.getLeftEye());
        double smile = Double.parseDouble(MyModel.getSmile());
        double sideways = Double.parseDouble(MyModel.getSideWays());
        double headrotate = Double.parseDouble(MyModel.getHeadRotate());
        Log.w("TAGKAAN", "" + righteye + "  ///   " + lefteye + "  ///   " + sideways + "  ///   " + smile + "  ///   " + headrotate);

        if (0.2 > smile) {
            if (righteye > 0.99 && lefteye > 0.99) {
                    if (sideways < 2 && sideways > -2) {
                        point = 1;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 2;
                    }else if(sideways >= 4){
                        point = 3;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 4;
                    }else if(sideways <= -4){
                        point = 5;
                    }


            } else if (righteye > 0.1 && lefteye > 0.1) {

                    if (sideways < 2 && sideways > -2) {
                        point = 6;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 7;
                    }else if(sideways >= 4){
                        point = 8;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 9;
                    }else if(sideways <= -4){
                        point = 10;
                    }

            } else if (righteye > 0.015 && lefteye > 0.015) {

                    if (sideways < 2 && sideways > -2) {
                        point = 12;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 12;
                    }else if(sideways >= 4){
                        point = 13;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 14;
                    }else if(sideways <= -4){
                        point = 15;
                    }


            } else {

                    if (sideways < 2 && sideways > -2) {
                        point = 16;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 17;
                    }else if(sideways >= 4){
                        point = 18;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 19;
                    }else if(sideways <= -4){
                        point = 20;
                    }
                }


        } else if (smile >= 0.2 && 0.6 > smile) {
            if (righteye > 0.98 && lefteye > 0.98) {

                    if (sideways < 2 && sideways > -2) {
                        point = 21;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 22;
                    }else if(sideways >= 4){
                        point = 23;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 24;
                    }else if(sideways <= -4){
                        point = 25;
                    }
            } else if (righteye > 0.1 && lefteye > 0.1) {

                    if (sideways < 2 && sideways > -2) {
                        point = 26;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 27;
                    }else if(sideways >= 4){
                        point = 28;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 29;
                    }else if(sideways <= -4){
                        point = 30;
                    }

            } else if (righteye > 0.015 && lefteye > 0.015) {

                    if (sideways < 2 && sideways > -2) {
                        point = 31;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 32;
                    }else if(sideways >= 4){
                        point = 33;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 34;
                    }else if(sideways <= -4){
                        point = 35;
                    }

            } else {

                    if (sideways < 2 && sideways > -2) {
                        point = 36;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 37;
                    }else if(sideways >= 4){
                        point = 38;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 39;
                    }else if(sideways <= -4){
                        point = 40;
                    }


            }
        } else {
            if (righteye > 0.98 && lefteye > 0.98) {

                    if (sideways < 2 && sideways > -2) {
                        point = 41;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 42;
                    }else if(sideways >= 4){
                        point = 43;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 44;
                    }else if(sideways <= -4){
                        point = 45;
                    }



            } else if (righteye > 0.1 && lefteye > 0.1) {

                    if (sideways < 2 && sideways > -2) {
                        point = 46;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 47;
                    }else if(sideways >= 4){
                        point = 48;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 49;
                    }else if(sideways <= -4){
                        point = 50;
                    }


            } else if (righteye > 0.015 && lefteye > 0.015) {

                    if (sideways < 2 && sideways > -2) {
                        point = 51;
                    }else if(sideways < 4 && sideways >= 2){
                        point = 52;
                    }else if(sideways >= 4){
                        point = 53;
                    }else if(sideways > -4 && sideways <= -2){
                        point = 54;
                    }else if(sideways <= -4){
                        point = 55;
                    }
                }else{
                if (sideways < 2 && sideways > -2) {
                    point = 56;
                }else if(sideways < 4 && sideways >= 2){
                    point = 57;
                }else if(sideways >= 4){
                    point = 58;
                }else if(sideways > -4 && sideways <= -2){
                    point = 59;
                }else if(sideways <= -4){
                    point = 60;
                }
            }
        }
        Log.w("TAGKAAN",""+point);
    }



    /*private void fragmentInitializer(String result) {
        Bundle bundle1 = new Bundle();
        bundle1.putString("data",result);
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_toresult, FragmentResult.class, bundle1)
                .commit();
    }

    private void secondFragmentInitializer(){
        Bundle bundle2 = new Bundle();
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view_topreviousresults, PreviousResultsFragment.class, bundle2)
                .commit();
    }*/

        public void loadFragment (Fragment fragment, Bundle bundle){
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.FrameLayout, fragment.getClass(), bundle);
            ft.commit();

        }
    }


