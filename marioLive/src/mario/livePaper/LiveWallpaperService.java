package mario.livePaper;

import android.content.SharedPreferences;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
 
/**
 * Android Live Wallpaper Archetype
 * @author antoine vianey
 * under GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class LiveWallpaperService extends WallpaperService {
 
    @Override
    public Engine onCreateEngine() {
        return new SampleEngine();
    }
 
    @Override
    public void onCreate() {
        super.onCreate();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
 
    public class SampleEngine extends Engine {
 
        private LiveWallpaperPainting painting;
 
        SampleEngine() {
            SurfaceHolder holder = getSurfaceHolder();
            painting = new LiveWallpaperPainting(holder, 
                getApplicationContext());
        }
 
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            // register listeners and callbacks here
            setTouchEventsEnabled(true);
        }
 
        @Override
        public void onDestroy() {
            super.onDestroy();
            // remove listeners and callbacks here
            painting.stopPainting();
        }
 
        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                // register listeners and callbacks here
                painting.resumePainting();
            } else {
                // remove listeners and callbacks here
                painting.pausePainting();
            }
        }
 
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, 
                int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);
            painting.setSurfaceSize(width, height);
        }
 
        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            // start painting
            painting.start();
        }
 
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            boolean retry = true;
            painting.stopPainting();
            while (retry) {
                try {
                    painting.join();
                    retry = false;
                } catch (InterruptedException e) {}
            }
        }
 
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, 
                float xStep, float yStep, int xPixels, int yPixels) {
        	super.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
        	painting.onOffsetsChanged(xOffset, yOffset, xStep, yStep, xPixels, yPixels);
        }
 
        @Override
        public void onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            painting.doTouchEvent(event);
        }
        
        /*public void onSharedPreferenceChanged(SharedPreferences prefs,
                String key)
        {
            mShape = prefs.getString("livewallpaper_testpattern", "smpte");
            mMotion = prefs.getBoolean("livewallpaper_movement", true);
            readColors();
        }*/
 
    }
 
}