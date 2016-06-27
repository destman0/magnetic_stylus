package com.desperados.mark1.ex4_magneticsensor;

import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
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

    private TouchEventView tev;

    private int buffsize = 100;
    private double[] xarray = new double [buffsize];
    private double[] yarray = new double [buffsize];
    private double[] zarray = new double [buffsize];
    private TextView et_x, et_y, et_z;
    private TextView vt_x, vt_y, vt_z;
    private TextView vt_tl, vt_tr, vt_bl, vt_br;
    private TextView vt_sx, vt_sy;
    private Button btn_tl, btn_tr, btn_bl, btn_br;
    double bx, by, bz;
    double dx, dy , dz;
    double ex = 0, ey = 0, ez = 0;
    double toplx, toply, toprx, topry, botlx, botly, botrx, botry;
    double wheight, wheight1,wheight2, wwidth, wwidth1,wwidth2;
    double dwidth, dheight;
    double hstep, wstep;
    double l = 0.01;
    double dr, r, xa, ya;
    private int iter = 0;
    private boolean bfull = false;
    private boolean cready = false;
    private int cpcount = 0;
    //public float screenX = 200, screenY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeVariables();
        tev.mCurrentShape = TouchEventView.DEBUG;

/*        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ShapeFragment())
                    .commit();
        }*/

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
                        toplx = dx;
                        toply = dy;
                        int rx,ry;
                        rx = (int)dx;
                        ry = (int)dy;
                        vt_tl.setText("Top Left X: "+Double.toString(rx)+"   Y: "+Double.toString(ry));
                        cpcount = cpcount+1;
                    }
                }
        );

        btn_tr.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_tr.setEnabled(false);
                        btn_tr.setBackgroundColor(0xff00ff00);
                        toprx = dx;
                        topry = dy;
                        int rx,ry;
                        rx = (int)dx;
                        ry = (int)dy;
                        vt_tr.setText("Top Right X: "+Double.toString(rx)+"   Y: "+Double.toString(ry));
                        cpcount = cpcount+1;
                    }
                }
        );
        btn_bl.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_bl.setEnabled(false);
                        btn_bl.setBackgroundColor(0xff00ff00);
                        botlx = dx;
                        botly = dy;
                        int rx,ry;
                        rx = (int)dx;
                        ry = (int)dy;
                        vt_bl.setText("Bottom Left X: "+Double.toString(rx)+"   Y: "+Double.toString(ry));
                        cpcount = cpcount+1;
                    }
                }
        );
        btn_br.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        btn_br.setEnabled(false);
                        btn_br.setBackgroundColor(0xff00ff00);
                        botrx = dx;
                        botry = dy;
                        int rx,ry;
                        rx = (int)dx;
                        ry = (int)dy;
                        vt_br.setText("Bottom Right X: "+Double.toString(rx)+"   Y: "+Double.toString(ry));
                        cpcount = cpcount+1;
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

        switch (item.getItemId()){
            case R.id.action_debug:
                tev.mCurrentShape = TouchEventView.DEBUG;
                //tev.debug = 0;
                tev.reset();
                break;
            case R.id.action_smoothline:
                tev.mCurrentShape = TouchEventView.SMOOTHLINE;
                //tev.debug = 0;
                tev.reset();
                break;
            case R.id.action_rectangle:
                tev.mCurrentShape = TouchEventView.RECTANGLE;
                //tev.debug = 0;
                tev.reset();
                break;
            case R.id.action_circle:
                tev.mCurrentShape = TouchEventView.CIRCLE;
                //tev.debug = 0;
                tev.reset();
                break;
            case R.id.action_triangle:
                tev.mCurrentShape = TouchEventView.TRIANGLE;
                //tev.debug = 0;
                tev.reset();
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {



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

        dx = bx - ex;
        dy = by - ey;
        dz = bz - ez;

        geometryCalc();
        //vt_sx.setText(Double.toString(xa));
        //vt_sy.setText(Double.toString(ya));

        et_x.setText("Earth_x: "+Double.toString(ex));
        et_y.setText("Earth_y: "+Double.toString(ey));
        et_z.setText("Earth_z: "+Double.toString(ez));

        vt_x.setText("X: "+Double.toString(dx));
        vt_y.setText("Y: "+Double.toString(dy));
        vt_z.setText("Z: "+Double.toString(dz));


        if(cpcount == 4) {
            Toast.makeText(MainActivity.this, "Workspace calibrated", Toast.LENGTH_SHORT).show();
            cpcount = 0;
            wwidth1 = toplx - toprx;
            wwidth2 = botlx - botrx;
            wheight1 = toply - botly;
            wheight2 = topry - botry;
            wwidth = (wwidth1+wwidth2)/2;
            wheight = (wheight1+wheight2)/2;


            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            dwidth = size.x;
            dheight = size.y;

            hstep = dheight / wheight;
            wstep = dwidth / wwidth;
            cready = true;
        }

        if (cready == true){
            //Toast.makeText(MainActivity.this, "W:" +(toplx - dx)*wstep+" H:"+(toply - dy)*hstep, Toast.LENGTH_SHORT).show();
            tev.screenX = (float)((toplx - dx)*wstep);
            tev.screenY = (float)((toply - dy)*hstep);
            vt_sx.setText(Double.toString((toplx - dx)*wstep));
            vt_sy.setText(Double.toString((toplx - dy)*hstep));
            tev.invalidate();


        }


    }








    private void initializeVariables() {

        tev = (TouchEventView) findViewById(R.id.drawingview);

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

        vt_sx = (TextView) findViewById(R.id.vt_sx);
        vt_sy = (TextView) findViewById(R.id.vt_sy);


        btn_tl = (Button) findViewById(R.id.btn_tl);
        btn_tr = (Button) findViewById(R.id.btn_tr);
        btn_bl = (Button) findViewById(R.id.btn_bl);
        btn_br = (Button) findViewById(R.id.btn_br);

    }


    private void geometryCalc(){
        dr = - Math.sqrt(Math.pow(dx,2)+Math.pow(dy,2));
        r=bisectionMethod();
        xa = r*Math.cos(Math.atan(dy/dx));
        ya = r*Math.sin(Math.atan(dy/dx));
        dx = xa;
        dy = ya;

    }


    private double bisectionMethod(){
        double a = 0.001,b=1,p,fa,fp,tol = 0.00001;
        int n = 500,iter;

        iter = 1;
        fa=equationFunc(a);

        while (iter<=n){
            p = a + (b-a)/2;
            fp = equationFunc(p);

            if ((fp == 0) || ((b-a) < tol))
                return p;
            iter = iter+1;

            if (fp*fa > 0){
                a = p;
                fa = fp;
            }
            else
                b=p;
        }
        return 0.01;
    }

    private double equationFunc(double esr){
        return Math.pow(l/esr,5)+3*Math.pow(l/esr,3)+(3-Math.pow(dr/dz,2))*(l/esr)+2*dr/dz;
    }



}



