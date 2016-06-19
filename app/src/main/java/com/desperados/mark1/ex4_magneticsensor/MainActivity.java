package com.desperados.mark1.ex4_magneticsensor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import 	java.lang.Math;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private int buffsize = 1000;
    private double[] xarray = new double [buffsize];
    private double[] yarray = new double [buffsize];
    private double[] zarray = new double [buffsize];
    private TextView et_x, et_y, et_z;
    private TextView vt_x, vt_y, vt_z;
    private TextView vt_tl, vt_tr, vt_bl, vt_br;
    private Button btn_tl, btn_tr, btn_bl, btn_br;
    double bx, by, bz;
    double ex = 0, ey = 0, ez = 0;
    private int iter = 0;
    private boolean bfull = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ShapeFragment())
                    .commit();
        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

        } else {
            Toast.makeText(getApplicationContext(), "No accelerometer found", Toast.LENGTH_SHORT).show();
        }

        btn_tl.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        //Log.i("I am pressed", "");
                        btn_tl.setEnabled(false);
                        btn_tl.setBackgroundColor(0xff00ff00);
                        vt_tl.setText("Top Left: X"+Double.toString(bx-ex)+"Y"+Double.toString(by-ey));
                    }
                }
        );

        btn_tr.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_tr.setEnabled(false);
                        btn_tr.setBackgroundColor(0xff00ff00);
                        vt_tr.setText("Top Right: X"+Double.toString(bx-ex)+"Y"+Double.toString(by-ey));
                    }
                }
        );
        btn_bl.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_bl.setEnabled(false);
                        btn_bl.setBackgroundColor(0xff00ff00);
                        vt_bl.setText("Bottom Left: X"+Double.toString(bx-ex)+"Y"+Double.toString(by-ey));
                    }
                }
        );
        btn_br.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_br.setEnabled(false);
                        btn_br.setBackgroundColor(0xff00ff00);
                        vt_br.setText("Bottom Right: X"+Double.toString(bx-ex)+"Y"+Double.toString(by-ey));
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the main; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {


        double l = 0.02;
        double br, r, xa, ya;
        double sumex = 0, sumey = 0, sumez = 0;
        //Log.i("EX", ""+ex);
        //Log.i("EY", ""+ey);
        //Log.i("EZ", ""+ez);

        //vt_x.setText(Float.toString(event.values[0]));
        //vt_y.setText(Float.toString(event.values[1]));
        //vt_z.setText(Float.toString(event.values[2]));

        if(bfull==false) {

            if (xarray[buffsize-1] == 0) {
                xarray[iter] = event.values[0];
                yarray[iter] = event.values[1];
                zarray[iter] = event.values[2];
                iter = iter + 1;
            } else {
                for (int i = 0; i < buffsize; i++) {
                    sumex = sumex + xarray[i];
                    sumey = sumey + yarray[i];
                    sumez = sumez + zarray[i];
                }
                ex = sumex / buffsize;
                ey = sumey / buffsize;
                ez = sumez / buffsize;
                bfull = true;
            }
        }

        bx = event.values[0];
        by = event.values[1];
        bz = event.values[2];
        //br = - Math.sqrt(Math.pow(bx,2)+Math.pow(by,2));
        r = Math.sqrt(Math.pow(bx,2)+Math.pow(by,2)+Math.pow(bz,2));
        xa = r*Math.cos(Math.atan(by/bx));
        ya = r*Math.sin(Math.atan(by/bx));


        et_x.setText("Earth_x: "+Double.toString(ex));
        et_y.setText("Earth_y: "+Double.toString(ey));
        et_z.setText("Earth_z: "+Double.toString(ez));

        vt_x.setText("X: "+Double.toString(bx-ex));
        vt_y.setText("Y: "+Double.toString(by-ey));
        vt_z.setText("Z: "+Double.toString(bz-ez));


    }





    private void initializeVariables() {
        et_x = (TextView) findViewById(R.id.et_x);
        et_y = (TextView) findViewById(R.id.et_y);
        et_z = (TextView) findViewById(R.id.et_z);


        vt_x = (TextView) findViewById(R.id.vt_x);
        vt_y = (TextView) findViewById(R.id.vt_y);
        vt_z = (TextView) findViewById(R.id.vt_z);

        vt_tl = (TextView) findViewById(R.id.vt_tl);
        vt_tr = (TextView) findViewById(R.id.vt_tr);
        vt_bl = (TextView) findViewById(R.id.vt_bl);
        vt_br = (TextView) findViewById(R.id.vt_br);


        btn_tl = (Button) findViewById(R.id.btn_tl);
        btn_tr = (Button) findViewById(R.id.btn_tr);
        btn_bl = (Button) findViewById(R.id.btn_bl);
        btn_br = (Button) findViewById(R.id.btn_br);

    }

}



