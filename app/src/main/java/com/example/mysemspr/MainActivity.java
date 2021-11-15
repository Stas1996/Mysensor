package com.example.mysemspr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor rotationSensor;
    private float light;
    private float rotationZ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);


    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotationSensor,SensorManager.SENSOR_DELAY_NORMAL);




    }

    private boolean howBatteryCharging(Intent batteryStatus) {
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }

    private boolean isBatteryCharging(Intent batteryStatus) {
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        return isCharging;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == lightSensor){
            light = event.values[0];
        }else if(event.sensor == rotationSensor) {
            rotationZ = event.values[0];
        }
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);

        boolean isCharging = isBatteryCharging(batteryStatus);

// How are we charging?
        boolean acCharging = howBatteryCharging(batteryStatus);
        Log.d("stas", rotationZ + "");
        Log.d("stas",isCharging + " is charging");
        Log.d("stas",acCharging +" how charging");
        Log.d("stas", light + " light");
        if(light > 6000 && isCharging && !acCharging && rotationZ > 0.680 && rotationZ < 0.690){
            Intent intent = new Intent(MainActivity.this,SuccessActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}