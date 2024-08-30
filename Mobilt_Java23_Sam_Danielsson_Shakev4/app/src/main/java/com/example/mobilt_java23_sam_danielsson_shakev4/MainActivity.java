package com.example.mobilt_java23_sam_danielsson_shakev4;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.chip.Chip;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private Chip withoutGravity;
    private TextView viewX,viewY,viewZ;
    private Sensor accelerometerSensor;
    private  ToggleButton toggleWholeNumbers;
    private Switch roundUpNumbers;
    private SensorManager sm;
    private float [] gravity;
    private float [] linear_acceleration;
    private SeekBar barX,barY,barZ;
    private float toastThreshold;

    private float deltaX;
    private float deltaY;
    private float deltaZ;

    private float lastX;
    private float lastY;
    private float lastZ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //creating sensor manager
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Accelerometer Sensor
        accelerometerSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        toastThreshold = accelerometerSensor.getMaximumRange() / 4;
        Log.i("Sam", "toastTheshold: " + toastThreshold);

        // Register sensor Listener
        sm.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);

        barX = (SeekBar) findViewById(R.id.barX);
        barY = (SeekBar) findViewById(R.id.barY);
        barZ = (SeekBar) findViewById(R.id.barZ);

        toggleWholeNumbers = (ToggleButton) findViewById(R.id.toggleButton);
        roundUpNumbers = (Switch) findViewById(R.id.roundUpNumbers);
        withoutGravity = (Chip) findViewById(R.id.withoutGravity);

        //Assign TextView
        viewX = (TextView) findViewById(R.id.viewX);
        viewY = (TextView) findViewById(R.id.viewY);
        viewZ = (TextView) findViewById(R.id.viewZ);

        deltaX = 0;
        deltaY = 0;
        deltaZ = 0;

        lastX = 0;
        lastY = 0;
        lastZ = 0;

        gravity = new float[3];
        linear_acceleration = new float[3];

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event){
        float  x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        if (withoutGravity.isChecked()){
            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * x;
            gravity[1] = alpha * gravity[1] + (1 - alpha) * y;
            gravity[2] = alpha * gravity[2] + (1 - alpha) * z;

            // Remove the gravity contribution with the high-paszs filter.
            x = x - gravity[0];
            y = y - gravity[1];
            z = z - gravity[2];
            //if (x<2 && y<2 && z<2){
             //   x = 0.0f;
            //    y = 0.0f;
             //   z = 0.0f;
            //}
        }

        if (toggleWholeNumbers.isChecked()){
            
            if (roundUpNumbers.isChecked()){
                x = (int) Math.ceil(x);
                y = (int) Math.ceil(y);
                z = (int) Math.ceil(z);
            }
            else {
                x = (int) x;
                y = (int) y;
                z = (int) z;
            }
        }
        else {
            if (roundUpNumbers.isChecked()){
                x = (float) Math.ceil(x);
                y = (float) Math.ceil(y);
                z = (float) Math.ceil(z);
            }
        }



        event.sensor.getName();

        viewX.setText("X: " +  x + "m/s");
        viewY.setText("Y: " + y  + "m/s");
        viewZ.setText("Z: " + z  + "m/s");

        deltaX = Math.abs(lastX - x);
        deltaY = Math.abs(lastY - y);
        deltaZ = Math.abs(lastZ - z);

        barX.setProgress((int) x * 10);
        barY.setProgress((int) y * 10);
        barZ.setProgress((int) z * 10);

        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];

       if (deltaX > toastThreshold || deltaY > toastThreshold || deltaZ > toastThreshold){
            Toast.makeText(this, "STOP SHAKING ME!!!", Toast.LENGTH_SHORT).show();
           Log.i("Sam", "onSensorChanged: X:" + x);
            Log.i("Sam", "onSensorChanged: Y:" + y);
           Log.i("Sam", "onSensorChanged: z:" + z);

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
        Log.i("Sam", "onPause:as ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm.registerListener(this,accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        Log.i("Sam", "onResume: ");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}