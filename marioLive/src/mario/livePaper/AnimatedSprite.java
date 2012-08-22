package mario.livePaper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class AnimatedSprite {
	private Bitmap mAnimation;
	private int mxPos;
	private int myPos;
	private Rect mSRectangle;
	private int mNoOfFrames;
	private int mCurrentFrame;
	private int mSpriteHeight;
	private int mSpriteWidth;
	private boolean mPlay;
	private boolean mRepeat;
	private long mPrevTime;
	private int mTimePerFrame; // 1000/fps
	private int bkgShift;
	private int fgShift;
	private boolean mSolid;
	private int mJumpingSpeed = -8;
	private float mGravity = 1.9f;
	private int myInit;
	private int myVel = 0;
	private int mxVel = 0;
	private int mID = 0;
	
	enum objects {
		Coin(0), Goomba(1), Wall(2), Mario(3), Bkg(4), CoinBlock(5);
		
		private int code;
		
		private objects(int c) {
			code = c;
		}
		
		public int getCode() {
			return code;
		}
	}
	private objects objType;
	
	public int getID() {
		return mID;
	}

	public void setID(int mID) {
		this.mID = mID;
	}

	public objects getType() {
		return objType;
	}
	
	public Rect getRect() {
		return new Rect(getXPos() + bkgShift, getYPos(), getXPos() + mSpriteWidth + bkgShift, getYPos() + mSpriteHeight);
	}
	
	public int getFgShift() {
		return fgShift;
	}

	public void setFgShift(int fgShift) {
		this.fgShift = fgShift;
	}

	public int getBkgShift() {
		return bkgShift;
	}

	public void setBkgShift(int bkgShift) {
		this.bkgShift = bkgShift;
	}

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
		myInit = newY;
		myPos = newY;
	}
	
	public void pause() {
		mPlay = false;
	}
	
	public void play() {
		mPlay = true;
	}
	
	public AnimatedSprite() {
		mSRectangle = new Rect(0,0,0,0);
		mCurrentFrame = 0;
		mxPos = 80;
		myPos = 200;
		mPlay = true;
		bkgShift = 0;
		fgShift = 0;
		mSolid = false;
	}
	
	public void Initialize(Bitmap theBitmap, int height, int width, int theFrameCount, boolean repeat, int fps, objects objType) {
		mAnimation = theBitmap;
		mSpriteHeight = height;
		mSpriteWidth = width;
		if(objType == objects.Bkg)
		{
			mSpriteWidth += 500;
		}
		mSRectangle.top = 0;
		mSRectangle.bottom = mSpriteHeight;
		mSRectangle.left = 0;
		mSRectangle.right = mSpriteWidth;
		mNoOfFrames = theFrameCount;
		mRepeat = repeat;
		mTimePerFrame = 1000/fps;
		mPrevTime = 0;
		this.objType = objType;
	}
	
	public void Update(long time) {
		if(mPrevTime == 0) { mPrevTime = time; }
		// velocity += acceleration * dt
		// position += velocity * dt
		
		if(myPos < myInit) {
			myVel += mGravity;// * (time - mPrevTime)/1000.0f;
		}
		else if(myPos != myInit) {
			myVel = 0;
			myPos = myInit;
		}
		
		myPos += myVel;//l * (time - mPrevTime)/1000.0f;
		
		
		if(mPlay && !(!mRepeat && mCurrentFrame == mNoOfFrames - 1) && (mTimePerFrame > 0 && time - mPrevTime > mTimePerFrame)) {
			mPrevTime = time;
			mCurrentFrame += 1;
			if(mCurrentFrame >= mNoOfFrames && mRepeat) {
				mCurrentFrame = 0;
			}
			else {
				mCurrentFrame = mNoOfFrames - 1;
			}
		}
		
		mSRectangle.left = mCurrentFrame * mSpriteWidth;
		mSRectangle.right = mSRectangle.left + mSpriteWidth;
	}
	
	public void draw(Canvas canvas) {
		Rect dest = new Rect(getXPos() + bkgShift, getYPos(), getXPos() + mSpriteWidth + bkgShift, getYPos() + mSpriteHeight);
		canvas.drawBitmap(mAnimation, mSRectangle, dest, null);
	}
	
	public void collision(int side) {
		//this.setXPos(this.mxPos + 35);
		if(objType == objects.CoinBlock && side == 1) {
			bounce();
		}
	}

	public boolean isSolid() {
		return mSolid;
	}

	public void setSolid(boolean mSolid) {
		this.mSolid = mSolid;
	}
	
	public void bounce() {
		myVel = mJumpingSpeed;
	}
}
