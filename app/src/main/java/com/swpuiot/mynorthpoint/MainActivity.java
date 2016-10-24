package com.swpuiot.mynorthpoint;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
    private SensorManager sensorManager;
    private ImageView compassimg;
    private TextView Degree;
    private TextView derection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        compassimg = (ImageView) findViewById(R.id.image_biaopan);
        Degree = (TextView) findViewById(R.id.text_dushu);
        derection = (TextView) findViewById(R.id.text_fangxiang);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor magneticsenser = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        Sensor accelerometersensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(listener, magneticsenser, SensorManager.SENSOR_DELAY_UI);
        sensorManager.registerListener(listener, accelerometersensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(listener);
        }
    }

    private SensorEventListener listener = new SensorEventListener() {
        float[] accelerometervalues = new float[3];
        float[] magneticvalues = new float[3];
        private float lastRotateDegree;
        int lastDegree;

        @Override
        public void onSensorChanged(SensorEvent event) {
            //判断是加速度传感器还是地磁传感器
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                //赋值使用clone（）
                accelerometervalues = event.values.clone();
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                magneticvalues = event.values.clone();
            }
            float[] R = new float[9];
            float[] values = new float[3];
            SensorManager.getRotationMatrix(R, null, accelerometervalues, magneticvalues);
            SensorManager.getOrientation(R, values);
            float rotateDegree = -(float) ((int) Math.toDegrees(values[0]));
            int degree = 0;
            if (rotateDegree < 0 && rotateDegree > -360) {
                degree = (int) (rotateDegree + 360);

            } else if (rotateDegree == -360) {
                degree = +0;
            } else {
                degree = (int) rotateDegree;
            }
            Log.i("MainActivity", "degree: " + degree);
            if (Math.abs(degree - lastDegree) >= 1) {
                Degree.setText((degree + ".0 度"));
                lastDegree = degree;
            }
            if (degree <= 22.5 || degree >= 337.5) {
                derection.setText("北");
            } else if (degree > 22.5 && degree < 67.5) {
                derection.setText("西北");
            } else if (degree > 67.5 && degree <= 112.5) {
                derection.setText("西");
            } else if (degree > 112.5 && degree <= 157.5) {
                derection.setText("西南");
            } else if (degree > 157.5 && degree <= 202.5) {
                derection.setText("南");
            } else if (degree > 202.5 && degree <= 247.5) {
                derection.setText("东南");
            } else if (degree > 248.5 && degree <= 292.5) {
                derection.setText("东");
            } else if (degree > 292.5 && degree <= 337.5) {
                derection.setText("东北");
            }
            if (Math.abs(rotateDegree - lastRotateDegree) > 2) {
                RotateAnimation animition = new RotateAnimation(lastRotateDegree, rotateDegree,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animition.setFillAfter(true);
                animition.setInterpolator(new DecelerateInterpolator());
                compassimg.startAnimation(animition);
                lastRotateDegree = rotateDegree;
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
}
