package mario.livePaper;

import android.graphics.BitmapFactory;
import mario.livePaper.AnimatedSprite;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;

public class Mario {
	private int mXPos;
	private int mYPosInit;
	private int mYPos;
	private float mYVel;
	private float mXVel;
	private int mRunningSpeed = 3;
	private int mJumpingSpeed = 15;
	private float mGravity = 1.0f;
	private int xOffset;
	private int numObj = 0;
	private AnimatedSprite collideableObjects[];
	// states = RunLeft, RunRight, Jump, SquatLeft, SquatRight
	enum states {
		RunLeft(0), RunRight(1), JumpLeft(2), SquatLeft(3), SquatRight(4), StandLeft(5), StandRight(6), JumpRight(7);
		
		private int code;
		
		private states(int c) {
			code = c;
		}
		
		public int getCode() {
			return code;
		}
	}
	
	private states mState;
	private Bitmap[] mStateImg;
	private AnimatedSprite[] mAnimations;
	
	public int collideSide(Rect otherRect) { // top = 1, bottom = -1, else = 0
		Rect myRect = mAnimations[mState.getCode()].getRect();
		if(myRect.top+5 >= otherRect.bottom && ((myRect.left < otherRect.left && myRect.right > otherRect.left) || (myRect.left < otherRect.right && myRect.right > otherRect.right)))
		{
			return 1;
		}
		
		if(myRect.bottom-5 <= otherRect.top && ((myRect.left < otherRect.left && myRect.right > otherRect.left) || (myRect.left < otherRect.right && myRect.right > otherRect.right)))
		{
			return -1;
		}
		return 0;
	}
	
	public int collideSide(Rect myRect, Rect otherRect) { // top = 1, bottom = -1, else = 0
		if(myRect.top+5 >= otherRect.bottom && ((myRect.left < otherRect.left && myRect.right > otherRect.left) || (myRect.left < otherRect.right && myRect.right > otherRect.right)))
		{
			return 1;
		}
		
		if(myRect.bottom-5 <= otherRect.top && ((myRect.left < otherRect.left && myRect.right > otherRect.left) || (myRect.left < otherRect.right && myRect.right > otherRect.right)))
		{
			return -1;
		}
		return 0;
	}
	
	public void addCollisionObj(AnimatedSprite obj) {
		collideableObjects[numObj] = obj;
		numObj += 1;
	}
	
	public Mario(int x, int y, Context context) {
		mXPos = x;
		mYPos = y;
		mYPosInit = y;
		mYVel = 0;
		mXVel = 0;
		
		mStateImg = new Bitmap[8];
		mAnimations = new AnimatedSprite[8];
		collideableObjects = new AnimatedSprite[16];
		for(int i = 0;i<8;i++) {
			mAnimations[i] = new AnimatedSprite();
		}
		
		mStateImg[states.RunLeft.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.runleft);
		mStateImg[states.RunRight.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.runright);
		mStateImg[states.JumpLeft.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumpleft);
		mStateImg[states.JumpRight.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.jumpright);
		mStateImg[states.SquatLeft.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.poses);
		mStateImg[states.SquatRight.getCode()] = BitmapFactory.decodeResource(context.getResources(), R.drawable.poses);
		
		mAnimations[states.RunLeft.getCode()].Initialize(mStateImg[states.RunLeft.getCode()], 48, 48, 2, true, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.RunRight.getCode()].Initialize(mStateImg[states.RunRight.getCode()], 48, 48, 2, true, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.JumpRight.getCode()].Initialize(mStateImg[states.JumpRight.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.JumpLeft.getCode()].Initialize(mStateImg[states.JumpLeft.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.SquatLeft.getCode()].Initialize(mStateImg[states.SquatLeft.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.SquatRight.getCode()].Initialize(mStateImg[states.SquatRight.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.StandLeft.getCode()].Initialize(mStateImg[states.RunLeft.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		mAnimations[states.StandRight.getCode()].Initialize(mStateImg[states.RunRight.getCode()], 48, 48, 1, false, 10, AnimatedSprite.objects.Mario);
		
		this.setState(states.StandRight);
	}
	
	public void update(long time) {
		boolean updateX = true, updateY = true;
		mAnimations[mState.getCode()].Update(time);
		
		mYVel += mGravity;
		
		if(mYPos+mYVel > mYPosInit) {
			mYVel = 0;
			setYPos(mYPosInit);
			if(mState == states.JumpLeft) {
				setState(states.RunLeft);
			}
			else if(mState == states.JumpRight){
				setState(states.RunRight);
			}
		}
		
		Rect oldRect = mAnimations[mState.getCode()].getRect();
		Rect newRect = new Rect();
		newRect.left = (int) (oldRect.left + mXVel + 15);
		newRect.right = (int)(oldRect.right + mXVel - 10);
		newRect.top = (int)(oldRect.top + mYVel + 10);
		newRect.bottom = (int)(oldRect.bottom + mYVel - 7);
		
		for(int i=0;(i<numObj);i++) {
			if(Rect.intersects(newRect, collideableObjects[i].getRect())) {
				int side = collideSide(newRect, collideableObjects[i].getRect());
    			
				if(collideableObjects[i].isSolid()) {
					switch(side) {
					case 0:
						mXVel = 0;
						updateX = false;
						break;
						
					case 1: case -1:
						mYVel = 0;
						updateY = false;
						/*if(mXVel > 0) {
							this.setState(states.RunRight);
						}
						else if(mXVel < 0) {
							this.setState(states.RunLeft);
						}*/
						break;
					}
				}
				collideableObjects[i].collision(side);
				switch(collideableObjects[i].getType().getCode()) {
				case 0:
					collideableObjects[i].setXPos(mXPos + 50);
					break;
				default:
					break;
					
				}
			}
		}
		
		if(updateX) { setXPos((int) (mXPos + mXVel)); }
		if(updateY) { setYPos((int) (mYPos + mYVel)); }
		
		if(mXPos - xOffset >= 432) {
			mXPos = 431;
			setState(states.StandRight);
		}
		else if(mXPos - xOffset <= 0) {
			mXPos = 1;
			setState(states.StandLeft);
		}
	}
	
	public void draw(Canvas c) {
		mAnimations[mState.getCode()].draw(c); // pass draw calls onto the correct animation
		//Paint paint = new Paint();
    	//paint.setColor(0xFF00FF00);
		//c.drawText(String.format("%d, %d", mXPos, mYPos), mXPos, mYPos - 32, paint);
	}
	
	public int getXPos() {
		return mXPos;
	}
	
	public void setXPos(int mXPos) {
		this.mXPos = mXPos;
		mAnimations[mState.getCode()].setXPos(mXPos);
	}
	
	public int getYPos() {
		return mYPos;
	}
	
	public void setYPos(int mYPos) {
		this.mYPos = mYPos;
		mAnimations[mState.getCode()].setYPos(mYPos);
	}

	public float getYVel() {
		return mYVel;
	}

	public void setYVel(int mYVel) {
		this.mYVel = mYVel;
	}

	public float getXVel() {
		return mXVel;
	}

	public void setXVel(int mXVel) {
		this.mXVel = mXVel;
	}

	public states getState() {
		return mState;
	}
	
	public void jump() {
		if(mState != states.JumpLeft && mState != states.JumpRight) {
			if(mXVel > 0 || mState == states.StandRight) {
				this.setState(states.JumpRight);
			}
			else {
				this.setState(states.JumpLeft);
			}
		}
	}

	public void setState(states mState) {
		this.mState = mState;
		
		switch(mState) {
		case RunLeft:
			mXVel = -mRunningSpeed;
			break;
		case RunRight:
			mXVel = mRunningSpeed;
			break;
		case JumpLeft:
			mYVel = -mJumpingSpeed;
			break;
		case JumpRight:
			mYVel = -mJumpingSpeed;
			break;
		default:
			mXVel = 0;//mYVel = 0;
			break;
		}
		mAnimations[mState.getCode()].setXPos(mXPos);
		mAnimations[mState.getCode()].setYPos(mYPos);
	}

	public int getxOffset() {
		return xOffset;
	}

	public void setxOffset(int xOffset) {
		this.xOffset = xOffset;
	}
}
