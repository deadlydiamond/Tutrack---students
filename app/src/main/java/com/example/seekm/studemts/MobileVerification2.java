package com.example.seekm.studemts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.seekm.studemts.MobileVerification3;

import static com.example.seekm.studemts.NetworkChangeReceiver.IS_NETWORK_AVAILABLE;

public class MobileVerification2 extends AppCompatActivity implements View.OnClickListener {
    public EditText phone_number;
    public ImageView backbutton;
    public ImageButton floatbutton;
    public String checker;
    public boolean internetCheck = false;
    public TextView forgot_btn_mob_2;
    public TextView no_account_btn_2;
    //public ProgressBar progressBar_2;
    CShowProgress cShowProgress = CShowProgress.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification2);
        //changing status bar color dynamically
        Window window = getWindow();
        //making button float
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        if (Build.VERSION.SDK_INT >= 21)
            window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
        //locking orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (isConnected()) {
            internetCheck = true;
            //Snackbar.make(findViewById(R.id.nearbyTutors),"Internet Connected" , Snackbar.LENGTH_INDEFINITE).show();
            //Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
        } else {
            internetCheck = false;
            Snackbar.make(findViewById(R.id.MobileVerification2),"No Internet Connection" , Snackbar.LENGTH_INDEFINITE).show();
            //Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";
                if (networkStatus=="disconnected"){
                    internetCheck = false;
                    Snackbar.make(findViewById(R.id.MobileVerification2),"No Internet Connection " , Snackbar.LENGTH_INDEFINITE).show();
                }else{
                    internetCheck = true;
                    Snackbar.make(findViewById(R.id.MobileVerification2),"Internet Connected" , Snackbar.LENGTH_LONG).show();
                }
            }
        }, intentFilter);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        phone_number = findViewById(R.id.phone_number2);
        floatbutton = findViewById(R.id.floatingActionButton);
        floatbutton.setOnClickListener(this);
        backbutton = findViewById(R.id.back_button_mob_2);
        backbutton.setOnClickListener(this);
        forgot_btn_mob_2 = findViewById(R.id.forgot_btn_mb_5);
        forgot_btn_mob_2.setOnClickListener(this);
        no_account_btn_2 = findViewById(R.id.no_account_btn_2);
        no_account_btn_2.setOnClickListener(this);
        //progressBar_2 = findViewById(R.id.progressBar_2);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                //progressBar_2.setVisibility(View.VISIBLE);
                cShowProgress.showProgress(MobileVerification2.this);
                validation_Number();

                break;

            case R.id.back_button_mob_2:
                finish();
                startActivity(new Intent(MobileVerification2.this, MobileV.class));

                break;
            case R.id.forgot_btn_mb_5:

                startActivity(new Intent(MobileVerification2.this, UserForgotPassword.class));
                break;
            case R.id.no_account_btn_2:
                finishAffinity();
                startActivity(new Intent(MobileVerification2.this, MobileV.class));

                break;
        }
    }

    private void validation_Number() {

        checker = phone_number.getText().toString();

        if (checker.isEmpty()) {
            //progressBar_2.setVisibility(View.GONE);
            cShowProgress.hideProgress();
            phone_number.setError("Contact Number is required.");
            phone_number.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(checker) || checker.length() < 11 || checker.length() > 11) {
            cShowProgress.hideProgress();
            //progressBar_2.setVisibility(View.GONE);

            phone_number.setError("Invalid phone number.");
            phone_number.requestFocus();

            return;
        }

        if (internetCheck==false){
            cShowProgress.hideProgress();

            phone_number.setError("Check your internet connection");
        }
        else {
            verification_Number();
        }
    }


    private void verification_Number() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cShowProgress.hideProgress();
                //progressBar_2.setVisibility(View.GONE);
                Intent intent = new Intent(MobileVerification2.this, MobileVerification3.class).putExtra("mobile_number", checker);

                startActivity(intent);
            }
        }, 2000);
    }
}
