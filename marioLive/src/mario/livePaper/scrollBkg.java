package mario.livePaper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class scrollBkg {
	private Bitmap mAnimation;
	private Rect mCurrentWindow;
	private Rect mSRectangle;
	private int mNoOfFrames;
	private int mTotalWidth;
	private int mCurrentFrame;
	private int mSpriteHeight;
	private int mSpriteWidth;
	private boolean mPlay;
	private int mTransitionTime = 500; // transition time in ms
	private long mPrevTime = 0;
	private int mbTransition;
	private int mxPos;
	private int myPos;
	
	public int getXPos() {
		return mxPos;
	}
	
	public int getYPos() {
		return myPos;
	}
	
	public void setXPos(int newX) {
		mxPos = newX;
	}
	
	public void setYPos(int newY) {
		myPos = newY;
	}
	
	public void pause() {
		mPlay = false;
	}
	
	public void play() {
		mPlay = true;
	}
	
	public scrollBkg() {
		mSRectangle = new Rect(0,0,0,0);
		mCurrentFrame = 0;
		mxPos = 0;
		myPos = 0;
		mPlay = true;
		mbTransition = 0;
	}
	
	public void Initialize(Bitmap theBitmap, int height, int width, int totalWidth, int x, int y) {
		mAnimation = theBitmap;
		mSpriteHeight = height;
		mSpriteWidth = width;
		mSRectangle.top = 0;
		mSRectangle.bottom = mSpriteHeight;
		mSRectangle.left = 0;
		mSRectangle.right = mSpriteWidth;
		mTotalWidth = totalWidth;
		mNoOfFrames = totalWidth/width;
		mxPos = x;
		myPos = y;
	}
	
	public void Update(long currentTime) {
		// distance to travel = width;
		// time to travel = transitionTime
		// distance per ms = width/transitionTime
		if(mPrevTime == 0) {
			mPrevTime = currentTime;
		}
		else if(mbTransition != 0) {
			mSRectangle.left += (int)(mbTransition * ((float)mSpriteWidth/mTransitionTime)*(currentTime - mPrevTime));
			mSRectangle.right = mSRectangle.left + mSpriteWidth;
		}
		mPrevTime = currentTime;
	}
	
	public void draw(Canvas canvas) {
		Rect dest = new Rect(getXPos(), getYPos(), getXPos() + mSpriteWidth, getYPos() + mSpriteHeight);
		canvas.drawBitmap(mAnimation, mSRectangle, dest, null);
	}
}
