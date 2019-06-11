package com.example.seekm.studemts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.seekm.studemts.NetworkChangeReceiver.IS_NETWORK_AVAILABLE;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public ImageView Profile_Image;
    public TextView Profile_Email;
    public TextView Profile_Name;
    ImageView advert,history,nearby,messages,nearby2;
    public String Image_Url;

    SharedPreferences Profile_preferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        //check network connectivity
        if (isConnected()) {
            Snackbar.make(findViewById(R.id.nearbyTutors),"Internet Connected" , Snackbar.LENGTH_SHORT).show();
            //Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            Snackbar.make(findViewById(R.id.drawer_layout),"No Internet Connection" , Snackbar.LENGTH_INDEFINITE).show();
            //Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";
                if (networkStatus=="disconnected"){
                    Snackbar.make(findViewById(R.id.drawer_layout),"No Internet Connection " , Snackbar.LENGTH_INDEFINITE).show();
                }else{
                    Snackbar.make(findViewById(R.id.drawer_layout),"Internet Connected" , Snackbar.LENGTH_LONG).show();
                }
            }
        }, intentFilter);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        advert = (ImageView)findViewById(R.id.advert);
        advert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawer.this,Advert.class);
                startActivity(intent);
            }
        });

        messages = (ImageView)findViewById(R.id.messages);
        messages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawer.this,Messages.class);
                startActivity(intent);
            }
        });

        history= (ImageView)findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawer.this,History.class);
                startActivity(intent);
            }
        });

        nearby= (ImageView)findViewById(R.id.nearbyTutors);
        nearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawer.this,NearbyTutorsMapActivity.class);
                startActivity(intent);
            }
        });

        nearby2= (ImageView)findViewById(R.id.nearbyTutors2);
        nearby2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Drawer.this,TutorsList.class);
                startActivity(intent);
            }
        });





        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);


        navigationView.setNavigationItemSelectedListener(this);

        Profile_Image=header.findViewById(R.id.profilePicture);
        Profile_Email=header.findViewById(R.id.User_email_logged_In);
        Profile_Name=header.findViewById(R.id.User_Name_Logged_In);


        Profile_preferences = getApplicationContext().getSharedPreferences("Profile_Preferecens",0);

        //String Email_user= Profile_preferences.getString("Email",null);
        //String Name_User= Profile_preferences.getString("First_Name",null)+ " " + Profile_preferences.getString("Last_Name",null);



        //Profile_Email.setText("Hello");
        //Profile_Name.setText(Name_User);





        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();
        //final CShowProgress cShowProgress = CShowProgress.getInstance();
        //cShowProgress.showProgress(EditProfile.this);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Students")
                .whereEqualTo("User_uid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    Profile_Name.setText(document.get("FirstName").toString()+ " " + document.get("LastName").toString());
                                    Profile_Email.setText(document.get("EmailAddress").toString());
                                    Image_Url = document.get("ProfileImage_Url").toString();
                                    Glide
                                            .with(Drawer.this)
                                            .load(Image_Url)
                                            .into(Profile_Image);
                                } catch (NullPointerException e) {
                                    //Log.d(TAG, "onComplete: Exception" + e.getMessage());
                                }
                            }
                            //cShowProgress.hideProgress();
                        }
                    }
                });




    }


    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Toast.makeText(this,"id=" + id,Toast.LENGTH_LONG).show();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();



        //Profile_Image.setImageURI();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            startActivity(new Intent (Drawer.this,EditProfile.class));
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(Drawer.this,About.class));


        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(Drawer.this, Feedback.class));
        } else if (id == R.id.nav_share) {
            startActivity(new Intent(Drawer.this, Share.class));


        } else if (id == R.id.nav_logout) {


            FirebaseAuth.getInstance().signOut();

//            ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            startActivity(new Intent(Drawer.this,MobileV.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}