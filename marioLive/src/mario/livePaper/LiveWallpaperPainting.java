package mario.livePaper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import mario.livePaper.AnimatedSprite;
import mario.livePaper.Mario;
import android.graphics.Color;
import android.net.Uri;
import mario.livePaper.SensorActivity;
import android.widget.Toast;


/**
 * Android Live Wallpaper painting thread Archetype
 * @author antoine vianey
 * GPL v3 : http://www.gnu.org/licenses/gpl-3.0.html
 */
public class LiveWallpaperPainting extends Thread {
	
	public static final Uri MMS_SMS_CONTENT_URI = Uri.parse("content://mms-sms/");
    public static final Uri THREAD_ID_CONTENT_URI =
                    Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "threadID");
    public static final Uri CONVERSATION_CONTENT_URI =
                    Uri.withAppendedPath(MMS_SMS_CONTENT_URI, "conversations");
    
    public static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
    public static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(SMS_CONTENT_URI, "inbox");
    
    public static final Uri MMS_CONTENT_URI = Uri.parse("content://mms");
    public static final Uri MMS_INBOX_CONTENT_URI = Uri.withAppendedPath(MMS_CONTENT_URI, "inbox");
    
    public static final String SMS_ID = "_id";
    public static final String SMS_TO_URI = "smsto:/";
    public static final String SMS_MIME_TYPE = "vnd.android-dir/mms-sms";
    public static final int READ_THREAD = 1;
    public static final int MESSAGE_TYPE_SMS = 1;
    public static final int MESSAGE_TYPE_MMS = 2;
    
    private static final String TIME_FORMAT_12_HOUR = "h:mm a";
    private static final String TIME_FORMAT_24_HOUR = "H:mm";
 
    /** Reference to the View and the context */
    private SurfaceHolder surfaceHolder;
    private Context context;
 
    /** State */
    private boolean wait;
    private boolean run;
 
    /** Dimensions */
    private int width;
    private int height;
 
    /** Time tracking */
    private long previousTime;
    private long currentTime;
    
    private int _xPixelOffset;
    
    private int mFPS = 100; // == 1000/fps = 10 fps
    
    private Mario mario;
    //private AnimatedSprite spriteList[];
    private AnimatedSprite bkg;
    private AnimatedSprite sprCoin;
    private AnimatedSprite sprPipe;
    private AnimatedSprite sprBlock;
    private AnimatedSprite pipePlant;
    private int pipePlantInitY;
    private SensorActivity mSensor;
    private double mTheta;
    private float a,b,c;
    //private int numSprites;
 
    public LiveWallpaperPainting(SurfaceHolder surfaceHolder, 
            Context context) {
        // keep a reference of the context and the surface
        // the context is needed if you want to inflate
        // some resources from your livewallpaper .apk
        this.surfaceHolder = surfaceHolder;
        this.context = context;
        // don't animate until surface is created and displayed
        this.wait = true;
        mario = new Mario(64, 578, context);
        //spriteList = new AnimatedSprite[16];
        //numSprites = 0;
        
        sprCoin = new AnimatedSprite();
        sprPipe = new AnimatedSprite();
        sprBlock = new AnimatedSprite();
        pipePlant = new AnimatedSprite();
        bkg = new AnimatedSprite();
        
        sprCoin.setID(1);
        sprBlock.setID(2);
        
        sprPipe.Initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe), 72, 48, 1, false, -1.0f, AnimatedSprite.objects.Pipe);
        sprCoin.Initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.coin), 24, 20, 4, true, 10.0f, AnimatedSprite.objects.Coin);
        sprBlock.Initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.animblock), 24, 24, 4, true, 10.0f, AnimatedSprite.objects.CoinBlock);
        pipePlant.Initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.pipeplant), 48, 40, 4, true, 10.0f, AnimatedSprite.objects.Plant);
        bkg.Initialize(BitmapFactory.decodeResource(context.getResources(), R.drawable.wallpaper_bkg), 1515, 642, 1, false, -1.0f, AnimatedSprite.objects.Bkg);
        
        sprPipe.setSolid(true);
        sprBlock.setSolid(true);
        
        //mario.addCollisionObj(sprPipe);
        mario.addCollisionObj(sprCoin);
        mario.addCollisionObj(sprBlock);
        mario.addCollisionObj(sprPipe);
        
        mSensor = new SensorActivity(context);
    }
 
    /**
     * Pauses the live wallpaper animation
     */
    public void pausePainting() {
        this.wait = true;
        synchronized(this) {
            this.notify();
        }
    }
 
    /**
     * Resume the live wallpaper animation
     */
    public void resumePainting() {
        this.wait = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    /**
     * Stop the live wallpaper animation
     */
    public void stopPainting() {
        this.run = false;
        synchronized(this) {
            this.notify();
        }
    }
 
    @Override
    public void run() {
        this.run = true;
        Canvas c = null;
        while (run) {
            try {
                c = this.surfaceHolder.lockCanvas(null);
                synchronized (this.surfaceHolder) {
                    currentTime = System.currentTimeMillis();
                    if(currentTime > previousTime + mFPS || true) {
                    	previousTime = currentTime;
                    	updatePhysics();
                        mario.update(currentTime);
                        sprCoin.Update(currentTime);
                        sprBlock.Update(currentTime);
                        pipePlant.Update(currentTime);
                        if(pipePlant.getYPos() <= this.pipePlantInitY) {
                        	pipePlant.setYVel(1);
                        }
                        if(pipePlant.getYPos() >= this.pipePlantInitY + 70) {
                        	pipePlant.setYVel(-1);
                        }
                        doDraw(c);
            		}
                }
            } finally {
                if (c != null) {
                    this.surfaceHolder.unlockCanvasAndPost(c);
                }
            }
            // pause if no need to animate
            synchronized (this) {
                if (wait) {
                    try {
                        wait();
                    } catch (Exception e) {}
                }
            }
        }
    }
 
    /**
     * Invoke when the surface dimension change
     */
    public void setSurfaceSize(int width, int height) {
        this.width = width;
        this.height = height;
        
        bkg.setXPos(0);
        bkg.setYPos(height - 642 - 154);
        sprCoin.setYPos(500);
        sprCoin.setXPos(100);
        sprBlock.setYPos(500);
        sprBlock.setXPos(600);
        sprPipe.setXPos(324);
        sprPipe.setYPos(548);
        pipePlant.setXPos(328);
        pipePlant.setYPos(504);
        pipePlantInitY = pipePlant.getYPos();
        
        synchronized(this) {
            this.notify();
        }
    }
    
    public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
    	
    	bkg.setBkgShift(xPixelOffset);
    	mario.setxOffset(0 /*_xPixelOffset - xPixelOffset*/);
    	sprCoin.setBkgShift(xPixelOffset);
    	sprPipe.setBkgShift(xPixelOffset);
    	sprBlock.setBkgShift(xPixelOffset);
    	pipePlant.setBkgShift(xPixelOffset);
    	
    	_xPixelOffset = xPixelOffset;
    	
    	/*for(int i=0;i<numSprites;i++) {
    		spriteList[i].setFgShift(xPixelOffset);
    	}*/
    }
 
    /**
     * Invoke while the screen is touched
     */
    public void doTouchEvent(MotionEvent event) {
        // handle the event here
        // if there is something to animate
        // then wake up
    	if(event.getAction() == 0) {
	        this.wait = false;
	        mario.jump();
	        synchronized(this) {
	        	notify();
	        }
    	}
    }
 
    /**
     * Do the actual drawing stuff
     */
    private void doDraw(Canvas canvas) 
    {
    	canvas.drawColor(Color.BLACK);
    	bkg.draw(canvas);
    	sprCoin.draw(canvas);
    	pipePlant.draw(canvas);
    	sprPipe.draw(canvas);
    	sprBlock.draw(canvas);
    	mario.draw(canvas);
    }
 
    /**
     * Update the animation, sprites or whatever.
     * If there is nothing to animate set the wait
     * attribute of the thread to true
     */
    private void updatePhysics() {
        // if nothing was updated :
        // this.wait = true;
    	//this.wait = !mario.wasUpdated();
    	//if(mSensor.isUpdated()) {
		float[] sensorData = mSensor.getAccel();
		mTheta = Math.toDegrees(Math.atan2(sensorData[1], sensorData[0]));
		
		if(mTheta < 89) {
			if(mario.getState() != Mario.states.JumpLeft && mario.getState() != Mario.states.JumpRight) {
				mario.setState(Mario.states.RunLeft);
			}
		}
		else if(mTheta > 91) {
			if(mario.getState() != Mario.states.JumpLeft && mario.getState() != Mario.states.JumpRight) {
				mario.setState(Mario.states.RunRight);
			}
		}
		else {
			if(mario.getState() == Mario.states.RunLeft) {
				mario.setState(Mario.states.StandLeft);
			}
			else if(mario.getState() == Mario.states.RunRight) {
				mario.setState(Mario.states.StandRight);
			}
		}
    }
}