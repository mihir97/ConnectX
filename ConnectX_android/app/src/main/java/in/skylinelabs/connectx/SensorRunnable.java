package in.skylinelabs.connectx;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import static java.lang.Math.floor;

/**
 * Created by MIHIR on 14-04-2017.
 */

final class SensorRunnable implements Runnable {

    private Context mContext;
    private SensorManager mSensorManager = null;
    private Sensor mSensor;
    private SensorEventListener mListener;
    private HandlerThread mHandlerThread;
    private EventCallback eventCallback;
    private SensorEvent sensorEvent;


    double X,Y,Z, tempx, tempy, tempz;

    int threshold = 20;

    SensorRunnable(Context context, EventCallback eventCallback) {
        mContext = context;
        this.eventCallback = eventCallback;
    }

    public void calibarate(){
        if(sensorEvent != null){
            tempx = sensorEvent.values[0] ;
            tempy = sensorEvent.values[1] ;
            //tempz = sensorEvent.values[2] ;
        }
    }


    @Override
    public void run() {

        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mHandlerThread = new HandlerThread("AccelerometerLogListener");
        mHandlerThread.start();
        Handler handler = new Handler(mHandlerThread.getLooper());


        mListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {


                sensorEvent = event;

                X = event.values[0];
                Y = event.values[1];
                Z = event.values[2];

                if(Z < 0) {
                    X = 20 - X;
                }

                X = X - tempx;
                Y = Y - tempy;
                X *= 10;
                Y *= 10;

                X = floor(X);
                Y = floor(Y);
                eventCallback.onEvent((long) Y);
//Y is Tilting
//                if (Y > threshold && left_right != Events.TILT_RIGHT) {
//                    //y.setText("Right Tilt");
//                    eventCallback.onEvent(Events.TILT_RIGHT);
//                    Y = 1;
//                    left_right = Events.TILT_RIGHT;
//
//                } else if (Y < -threshold && left_right != Events.TILT_LEFT) {
//                    //y.setText("Left Tilt");
//                    eventCallback.onEvent(Events.TILT_LEFT);
//                    Y = 2;
//                    left_right = Events.TILT_LEFT;
//
//                } else if(Y > -threshold && Y < threshold && left_right != Events.TILT_LEFT_RIGHT_STABLE){
//                    //y.setText("Stable");
//                    eventCallback.onEvent(Events.TILT_LEFT_RIGHT_STABLE);
//                    Y = 0;
//                    left_right = Events.TILT_LEFT_RIGHT_STABLE;
//                }
//
////X is for Up Down
//                if (X > threshold && up_down != Events.TILT_UP) {
//                    //z.setText("Towards You");
//                    eventCallback.onEvent(Events.TILT_UP);
//                    X = 1;
//                    up_down = Events.TILT_UP;
//
//                } else if (X < -threshold  && up_down != Events.TILT_DOWN) {
//                    //z.setText("Far from You");
//                    eventCallback.onEvent(Events.TILT_DOWN);
//                    X = 2;
//                    up_down = Events.TILT_DOWN;
//                } else if(X > -threshold && X < threshold && up_down != Events.TILT_UP_DOWN_STABLE){
//                    //z.setText("Stable");
//                    eventCallback.onEvent(Events.TILT_UP_DOWN_STABLE);
//                    up_down = Events.TILT_UP_DOWN_STABLE;
//                    X = 0;
//                }
////Z is not used
//                Z = 0;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL * 2000000,
                handler
        );
    }


    public void cleanThread(){

        //Unregister the listener
        if(mSensorManager != null) {
            mSensorManager.unregisterListener(mListener);
        }

        if(mHandlerThread.isAlive())
            mHandlerThread.quitSafely();

    }
}
