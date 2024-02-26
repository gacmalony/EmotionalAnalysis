package com.example.emotionapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emotionapp.R;
import com.example.emotionapp.model.MyModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    // todo I'm gonna add a click event to the admin console
    // After bringing the analyzed results, I will bind them with room database data
    // For Beginning I would complete 16 different results --> Tomorrow

    Button btnadmin;
    ImageButton btn;

    TextView textView;

    Map<Integer, Double> dataMap;

    String datetime;

    private FirebaseAuth mAuth;






    private static final int REQUEST_IMAGE_CAPTURE = 1;



    String image_url;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_base);
        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.button);
        textView = findViewById(R.id.textView);

        FirebaseApp.initializeApp(this);
        dataMap = new HashMap<>();


        btnadmin = findViewById(R.id.button2);

        btnadmin.setOnClickListener(v -> {

        });
        askPermission();
        btn.setOnClickListener(v -> openFile());



        Toolbar toolbar = findViewById(R.id.thisistool);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);

        }
    }




    @Override
    protected void onRestart() {
        super.onRestart();
        // I would add
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.basemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void openFile() {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
    }



    private void askPermission(){
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");
            FaceDetectionProcess(bitmap);
            Log.w("TAGKAAN","ONACTIVITYRESULT!");
            StorageCall(bitmap);
        }catch (NullPointerException e){
            e.printStackTrace();
        }finally {
            Toast.makeText(getApplicationContext(),"Take a photo of the target face",Toast.LENGTH_SHORT).show();
        }

    }

    private void StorageCall(Bitmap bitmap){

        try {
            Log.w("TAGKAAN"," STORAGE UPLOAD CALL!");
            Date date = new Date();
            SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            datetime = sfd.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }


        FirebaseUser currentUser = mAuth.getCurrentUser();
        try {
            if(!currentUser.isAnonymous()){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference imagesRef = storage.getReference().child("images-user-"+currentUser.getDisplayName()+"-"+currentUser.getUid()+"/"+datetime);
                image_url = "images-user-"+currentUser.getDisplayName()+"-"+currentUser.getUid()+"/"+datetime;
                ImageAsByte(bitmap, imagesRef);
            }else{

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }


    }









    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_signout){
            mAuth.signOut();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }



    private void ImageAsByte(Bitmap bitmap, StorageReference imagesRef){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(exception -> Log.w("TAGKAAN","UNSUCCESSFULLY STORAGE UPLOAD!")).addOnSuccessListener(taskSnapshot -> Log.w("TAGKAAN", "UPLOADED SUCCESSFULLY!"));
    }

    private void FaceDetectionProcess(Bitmap bitmap) {
        textView.setText(R.string.process);
        final StringBuilder builder = new StringBuilder();
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                //.setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).
                enableTracking().build();
        btn.setVisibility(View.GONE);


        FaceDetector detector = FaceDetection.getClient(options);
                detector.process(image)
                        .addOnSuccessListener(
                                faces -> {
                                    if (faces.size() !=0){
                                        if(faces.size() == 1){
                                            builder.append(faces.size()+ " Face Detected\n\n");
                                        }else if(faces.size() >1){
                                            builder.append(faces.size() + " Faces Detected\n\n");
                                        }
                                    }


                                    for (Face face : faces) {
                                        //Rect bounds = face.getBoundingBox();
                                        double rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                        double rotZ = face.getHeadEulerAngleZ();    // Head is tilted sideways rotZ degrees
                                        dataMap.put(4, rotY);
                                        dataMap.put(5, rotZ);
                                        builder.append("2. Head Rotation to Right["+String.format("%.2f",rotY) +"deg. ]\n");
                                        builder.append("2. Head Tilted Sideways["+String.format("%.2f",rotZ) +"deg. ]\n");


                                        /* If landmark detection was enabled (mouth, ears, eyes, cheeks, and
                                         nose available):
                                        FaceLandmark leftEar = face.getLandmark(FaceLandmark.LEFT_EAR);
                                        if (leftEar != null) {
                                            PointF leftEarPos = leftEar.getPosition();
                                        }

                                         If contour detection was enabled:
                                            List<PointF> leftEyeContour
                                                 = face.getContour(FaceContour.LEFT_EYE).getPoints();
                                        List<PointF> leftCheek =
                                                face.getContour(FaceContour.LEFT_CHEEK).getPoints();
                                        List<PointF> rightEyeContour =
                                                face.getContour(FaceContour.RIGHT_EYE).getPoints();
                                        for(PointF p : leftEyeContour){
                                           System.out.println(p);
                                        }
                                        for(PointF p : rightEyeContour ){
                                            System.out.println(p);
                                        }
                                        for(PointF p : leftCheek){
                                            System.out.println(p);
                                        }*/

                                        //if(leftEyeContour.get(0) != null){
                                        //  for(int i = 0; i < leftEyeContour.size(); i++){
                                        //      builder.append("Sp. LeftEyeContour"+leftEyeContour.get(i));
                                        // }
                                        // }


                                        // If classification was enabled:
                                        if (face.getSmilingProbability() != null) {
                                            double smileProb = face.getSmilingProbability();
                                            dataMap.put(1, smileProb);
                                            builder.append("4. Smiling Probability ["+ String.format("%.2f", smileProb)+"]\n");


                                        }
                                        if (face.getRightEyeOpenProbability() != null) {
                                            double rightEyeOpenProb = face.getRightEyeOpenProbability();
                                            dataMap.put(2, rightEyeOpenProb);
                                            builder.append("5. Right Eye Open Probability ["+ String.format("%.2f", rightEyeOpenProb)+"]\n");

                                        }

                                        if (face.getLeftEyeOpenProbability() != null) {
                                            double leftEyeOpenProb = face.getLeftEyeOpenProbability();
                                            dataMap.put(3, leftEyeOpenProb);
                                            builder.append("5. Left Eye Open Probability ["+ String.format("%.2f", leftEyeOpenProb)+"]\n");
                                        }


                                        // If face tracking was enabled:
                                        if (face.getTrackingId() != null) {
                                            int id = face.getTrackingId();

                                        }
                                        builder.append("\n");
                                    }
                                    Log.w("TAGKAAN","BaseActivitySavedPointbeforeShowDetection");
                                    ShowDetectionwithsql(dataMap,String.valueOf(faces.size())+" Face Detected!", true);
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        StringBuilder builder1 = new StringBuilder();
                                        builder1.append("Sorry there is a Mistake here!!");
                                        Log.w("TAGKAAN","BaseActivitySavedPointbeforeShowDetection2");
                                        ShowDetectionwithsql(dataMap,builder1.toString(), false);

                                    }
                                });






    }

    private void ShowDetectionwithsql(final Map list,final String title, final boolean b){
        if(b){
            if(list.size() != 0) {
                textView.setText(title);
                Intent i = new Intent(this, ResultsActivity.class);
                new MyModel(dataMap);
                i.putExtra("image_url", image_url);
                startActivity(i);
                Log.w("TAGKAAN", "Activity send");
        }else{
                textView.setText("SORRY CAN YOU TAKE PHOTO AGAIN?");
            }
            }else{
                textView.setText(title);
        }
    }


    // With an Activity
   /* private void ShowDetectionwithdata(final String title, final Map list, boolean b){
        if(b == true) {
            textView.setText(null);
            Toast.makeText(this, "MainActivitySavedPoint Third", Toast.LENGTH_SHORT).show();

            if (list.size() != 0) {
                Intent i = new Intent(this, Test.class);
                MyModel model = new MyModel(dataMap);
                startActivity(i);
            }
        }
    }
    */

    // With a fragment
   /* private void ShowDetection(final String title, final StringBuilder builder, boolean b) {

        if(b == true){
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            Toast.makeText(this, "MainActivitySavedPoint Third", Toast.LENGTH_SHORT).show();


            if(builder.length() != 0){
                textView.append(builder);
                if(title.substring(0, title.indexOf(" ")).equalsIgnoreCase("OCR")){
                    textView.append("\n(Hold the Text and copy it!)");

                }else{
                    textView.append("(Hold the text and copy it!)");
                }

                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText(title, builder);
                        clipboardManager.setPrimaryClip(clipData);
                        return true;
                    }

            });
        }else {
                textView.append(title.substring(0, title.indexOf(" "))+"Failed to find anything!!!");
            }


    } else if (b == false) {
            textView.setText(null);
            textView.setMovementMethod(new ScrollingMovementMethod());
            textView.append(builder);
        }
        Toast.makeText(this, "MainActivitySavedPoint Fourth", Toast.LENGTH_SHORT).show();

    }*/
}
