package com.example.mysemspr;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MicrophoneDirection;
import android.media.MicrophoneInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private Sensor proximitySensor;
    private float light;
    private float proximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
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
        sensorManager.registerListener(this,proximitySensor,SensorManager.SENSOR_DELAY_NORMAL);
    }

    private boolean howBatteryCharging(Intent batteryStatus) {
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == lightSensor) {
            light = event.values[0];
        }

        if (event.sensor == proximitySensor){
            proximity = event.values[0];
        }

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        boolean acCharging = howBatteryCharging(batteryStatus);

        Log.d("stas", light + " light");
        Log.d("stas",  proximity + " proximity");

        int percentage = batteryPercentage(batteryStatus);
        Log.d("stas",acCharging +" how charging");
        Log.d("stas", "percentage " + percentage);

        if(light > 15000 && acCharging && percentage == 55 && proximity == 5.5){
            Intent intent = new Intent(MainActivity.this,SuccessActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private int batteryPercentage(Intent batteryStatus) {

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        double batteryPct = level / (double) scale;

        return (int) (batteryPct * 100);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}