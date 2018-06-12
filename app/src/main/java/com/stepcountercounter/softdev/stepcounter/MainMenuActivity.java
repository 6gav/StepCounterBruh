package com.stepcountercounter.softdev.stepcounter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener{



    private SensorManager sensorManager;
    int StepCount, DebugTapCount;
    boolean tracking, DebugEnabled;
    TextView StepCounter;
    Button temp;
    int LastStepCount;
    EditText debugNumberTextView;




    public String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TestUserData";


    //Timer
    Handler h = new Handler();
    int delay = 250; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        temp = (Button)findViewById(R.id.DebugAddSteps);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        StepCounter = findViewById(R.id.stepCounterTextView);
        temp.setVisibility(View.INVISIBLE);
        temp.setEnabled(false);
        DebugTapCount = 5;
        DebugEnabled = false;
        debugNumberTextView = findViewById(R.id.stepNumberTextView);

        tracking = false;
        StepCount = 0;
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null){
            sensorManager.registerListener(this, countSensor, sensorManager.SENSOR_DELAY_UI);

        }
        else
        {
            Toast.makeText(this, "No Sensor Available.", Toast.LENGTH_SHORT).show();
        }

        String tempStepsForCount = LoadSteps();
        StepCounter.setText("Steps: " + tempStepsForCount);
        StepCount = Integer.valueOf(tempStepsForCount);
        //StepCount = loadSteps(file);


    }


    public void startTracking(View v){
        tracking = true;
    }

    public void stopTracking(View v){
        tracking = false;
    }

    public void AddSteps(View v){
        if(DebugEnabled){
            int textSteps = Integer.valueOf(debugNumberTextView.getText().toString());
            StepTrack(textSteps);
        }
    }
    public void DebugEnable(){
        temp.setVisibility(View.VISIBLE);
        temp.setEnabled(true);
        temp.setClickable(true);
        debugNumberTextView.setVisibility(View.VISIBLE);

    }
    public void DebugTry(View v){
        if(!DebugEnabled) {
            if (DebugTapCount > 0) {
                DebugTapCount--;

            } else if (DebugTapCount == 0) {
                DebugEnabled = true;
                DebugEnable();
            }
        }
    }
    public void StepTrack(int x){
        StepCount+=x;
        saveSteps(StepCount);
    }


    @Override
    protected void onResume(){

        h.postDelayed(new Runnable() {
            public void run() {
                StepCounter.setText("Steps: " + String.valueOf(StepCount));
                runnable=this;

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();

    }

    @Override
    protected  void onPause(){
        h.removeCallbacks(runnable);
        super.onPause();


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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if((int)event.values[0] != LastStepCount && tracking)
        StepTrack(1);


        LastStepCount = (int)event.values[0];


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {


    }

    public String LoadSteps(){
        String tempSteps = "";
        try{
            InputStream inputStream = getApplicationContext().openFileInput("testData.txt");

            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                tempSteps = receiveString;
            }
        }
        catch(IOException e){


        }

        return tempSteps;
    }

    //Check if storage is available - public
    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public void saveSteps(int Steps) {
        try {
            OutputStreamWriter outputSteps = new OutputStreamWriter(openFileOutput("testData.txt", Context.MODE_PRIVATE));
            outputSteps.write(String.valueOf(Steps));
            outputSteps.close();
        } catch (IOException e) {
            Log.e("Exception", "File Write Failed: " + e.toString());
        }
    }

}