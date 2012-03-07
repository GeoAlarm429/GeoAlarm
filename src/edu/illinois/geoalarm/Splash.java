package edu.illinois.geoalarm;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
/**
 * 
 * @author SriVarshaGorge
 *
 */
public class Splash extends Activity {
	public static boolean flag = true;
	protected boolean active = true;
	protected int splashTime = 5000; 
	
	/** Called when the activity is first created. */

	
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash);
            
            final Context context = this;
           
            Thread splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        while(active && (waited < splashTime)) {
                            sleep(100);
                            if(active) {
                                waited += 100;
                            }
                        }
                    } 
                    catch(InterruptedException e) {  
                    } 
                    
                    finally {
                        finish();
                        flag = false;
                        startActivity(new Intent(context, GeoAlarm.class));
                        stop();
                    }
                }
            };
            splashTread.start();
        
    }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                active = false;
            }
            return true;
        }
    
}
