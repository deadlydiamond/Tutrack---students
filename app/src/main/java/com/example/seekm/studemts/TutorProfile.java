package com.example.seekm.studemts;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorProfile extends AppCompatActivity {

    TextView fullName, email, gender, qualification, dob, board;
    ProgressBar mBar;
    String fname, lname;
    Button request, cancel;
    String myUID, URL;
    ImageView dp;

    private String TAG = "TUTORS_PROFILE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        dp = (ImageView) findViewById(R.id.profilePicture);
        fullName = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.emaill);
        gender= (TextView) findViewById(R.id.genderr);
        qualification = (TextView) findViewById(R.id.qualificationn);
        dob = (TextView) findViewById(R.id.dobb);
        board = (TextView) findViewById(R.id.boardd);


        cancel = (Button)findViewById(R.id.btn_cancel);
        request = (Button)findViewById(R.id.btn_request);
        URL = null;

        final CShowProgress cShowProgress = CShowProgress.getInstance();
        cShowProgress.showProgress(TutorProfile.this);

        Intent intent = getIntent();
        myUID = intent.getStringExtra("uid");
        Toast.makeText(TutorProfile.this, myUID,Toast.LENGTH_LONG).show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tutors")
                .whereEqualTo("User_uid", myUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {

                                    fname = document.get("FirstName").toString();
                                    lname = document.get("LastName").toString();
                                    fullName.setText(fname + " " + lname);
                                    qualification.setText(document.get("LatestQualification").toString());
                                    board.setText(document.get("EducationBoard").toString());
                                    email.setText(document.get("EmailAddress").toString());
                                    gender.setText(document.get("Gender").toString());
                                    dob.setText(document.get("DateOfBirth").toString());
                                    URL = document.get("ProfileImage_Url").toString();
                                    Glide
                                            .with(TutorProfile.this)
                                            .load(URL)
                                            .into(dp);
                                    cShowProgress.hideProgress();

                                } catch (NullPointerException e) {
                                    Log.d(TAG, "onComplete: Exception" + e.getMessage());
                                }
                            }

                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorProfile.this,NearbyTutorsMapActivity.class);
                startActivity(intent);
            }
        });


    }
}
