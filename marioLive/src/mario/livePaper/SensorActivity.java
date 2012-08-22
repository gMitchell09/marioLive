package mario.livePaper;

import android.app.Activity;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.content.Context;
import android.app.Service;

public class SensorActivity implements SensorEventListener {
	
	private Sensor mAccelerometer;
	private float[] mAccel;
    private SensorManager mSensorManager;
    private boolean mUpdated = false;
    
    public float[] getAccel() {
    	mUpdated = false;
    	if(mAccelerometer == null) {
    		float[] retVal = {-1.0f, -1.0f, -1.0f};
    		return retVal;
    	}
		return mAccel;
	}
    
    public boolean isUpdated() {
    	mUpdated = !mUpdated;
    	return !mUpdated;
    }

	public SensorActivity(Context c) {
		mSensorManager = (SensorManager)c.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mAccel = new float[3];
    }
	/*
	protected void onCreate() {
		
	}

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
	*/
	
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    	mAccel[0] = event.values[0];
    	mAccel[1] = event.values[1];
    	mAccel[2] = event.values[2];
    	mUpdated = true;
    }
}