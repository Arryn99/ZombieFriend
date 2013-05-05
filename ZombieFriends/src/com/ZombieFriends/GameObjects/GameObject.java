package com.ZombieFriends.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.Mechanics.Game;

public abstract class GameObject
{

	Bitmap  mBitmap;		//this represents the sprite
	Vector  mPosition;		//this represents the current position
	Vector  mPreviousPosition = new Vector(0,0);
	boolean mTouchBeganOnGameObject; //set to true if touch starts on game object
	long    mPreviousTouchTime;
	Vector mResetPosition;


	public GameObject(Context context)
	{
		super();
		mPosition = new Vector(0, 0);
	}

	public void setBitmap(Context context, int resourceID)
	{
		mBitmap = BitmapFactory.decodeResource(context.getResources(),resourceID);
	}
	
	public void setBitmap( Bitmap bitmap)
	{
		mBitmap = bitmap;
	}

	public void keepObjectOnScreen()
	{

		float x = mPosition.getX();
		float y = mPosition.getY();

		if (x < 0)
		{
			mPosition.setX(0);
		}
		if (y < 0)
		{
			mPosition.setY(0);
		}

		if (x > (Game.ScreenSize.getX() - getSize().getX()))
		{
			mPosition.setX(Game.ScreenSize.getX() - mBitmap.getWidth());
		}
		if (y > (Game.ScreenSize.getY() - getSize().getY()))
		{
			mPosition.setY(Game.ScreenSize.getY() - mBitmap.getHeight());
		}

	}


	public void setmBitmapWithSize(Context context, int resourceID, Vector size)
	{
		setBitmap(context, resourceID); // load in the bitmap normally 
		Bitmap b = Bitmap.createScaledBitmap(mBitmap, (int)size.getX(), (int)size.getY(), true); //Resize background to fit the screen.
		mBitmap.recycle(); // clean up the original bitmap now that it has been copied
		mBitmap = b; //assign the new scaled bitmap to be drawn
	}

	public Vector getSize()
	{
		Vector size = new Vector(mBitmap.getWidth(), mBitmap.getHeight());
		return size;

	}
	public boolean isOnScreen()
	{
		float x = mPosition.getX();
		float y = mPosition.getY();

		if (x < -getSize().getX())
		{
			return false;
		}
		else if (y < -getSize().getY())
		{
			return false;
		}

		if (x > (Game.ScreenSize.getX() + getSize().getX()))
		{
			return false;
		}
		else if (y > (Game.ScreenSize.getX() + getSize().getY()))
		{
			return false;
		}
		else return true;
	}


	public void onTouchEvent(Finger_Action action, Vector position, long eventTime)
	{
		switch(action)
		{
		case DOWN:
		{
			//player has just touched the screen
			//Log.d("touchEvent", "Player touch " + position.getX() + " " + position.getY());
			if (collisionWithPoint(position))
			{
				mTouchBeganOnGameObject = true;
				onTouch();
				mPreviousPosition = position;
				mPreviousTouchTime = eventTime;

			}
			else mTouchBeganOnGameObject = false;

		}
		break;
		case UP:
		{
			//if player's finger has left screen
			if (mTouchBeganOnGameObject)
			{
				//touch started on this game object
				Log.d("touchEvent", "Player touch up " + position.getX() + " " + position.getY());
				if (collisionWithPoint(position))
				{
					//touch ended on this game object
					onTap();
				}
			}
			else
			{

			}
		}
		break;
		case MOVE:
		{
			//player's finger is moving on the screen
			//Log.d("touchEvent", "Player touch move " + position.getX() + " " + position.getY());

			Vector velocity = Vector.sub(position, mPreviousPosition);
			float diff = (eventTime - mPreviousTouchTime)/1000.0f;
			//Log.d("diff: ", " " + diff);
			velocity = Vector.multiply(velocity, 1/diff);

			if (mTouchBeganOnGameObject)
			{
				onMove(position, velocity);
			}
			if (collisionWithPoint(position))
			{
				if (!mTouchBeganOnGameObject)
				{
					onMoveInto(position, velocity);
				}
			}

			mPreviousPosition = position;
			mPreviousTouchTime = eventTime;

		}
		break;
		}

	}

	protected void onTap()
	{

	}

	public void onTouch()
	{

	}

	public void onMove(Vector touchPos, Vector velocity)
	{

	}

	public void onMoveInto(Vector touchPos, Vector velocity)
	{

	}

	public boolean collisionWithPoint(Vector point)
	{
		if (mBitmap != null)	//check if alive
		{
			if ((mPosition.getX() + mBitmap.getWidth() > point.getX()) && (mPosition.getX() < point.getX()))
			{
				if ((mPosition.getY() + mBitmap.getHeight() > point.getY()) && (mPosition.getY() < point.getY()))
					return true;
				else return false;
			}
			else return false;
		}
		else return false;
	}

	public static boolean checkForCollision(GameObject a, GameObject b)
	{
		Vector topLeftA     = a.mPosition;
		Vector topRightA    = Vector.add(a.mPosition, new Vector(a.getSize().getX(), 0));
		Vector bottomLeftA  = Vector.add(a.mPosition, new Vector(0, a.getSize().getY()));
		Vector bottomRightA = Vector.add(a.mPosition,  a.getSize());

		// are any of the corners inside b
		if(b.collisionWithPoint(topLeftA))
		{
			return true;
		}
		else if(b.collisionWithPoint(topRightA))
		{
			return true;
		}
		else if(b.collisionWithPoint(bottomLeftA))
		{
			return true;
		}
		else if(b.collisionWithPoint(bottomRightA))
		{
			return true;
		}


		Vector topLeftB     = b.mPosition;
		Vector topRightB    = Vector.add(b.mPosition, new Vector(b.getSize().getX(), 0));
		Vector bottomLeftB  = Vector.add(b.mPosition, new Vector(0, b.getSize().getY()));
		Vector bottomRightB = Vector.add(b.mPosition,  b.getSize());
		// are any of the corners inside A

		if(a.collisionWithPoint(topLeftB))
		{
			return true;
		}
		else if(a.collisionWithPoint(topRightB))
		{
			return true;
		}
		else if(a.collisionWithPoint(bottomLeftB))
		{
			return true;
		}
		else if(a.collisionWithPoint(bottomRightB))
		{
			return true;
		}

		return false;
	}
	protected Paint mBitmapPaint = new Paint();
	/**
	 * @param canvas represents the screen being drawn to
	 */
	public void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(mBitmap, mPosition.getX(), mPosition.getY(), mBitmapPaint);
	}


	/**
	 * @param dt represents the time between frames
	 */
	abstract public void update(float dt);

	public void cleanUp()
	{
		mBitmap.recycle();		//frees up memory for bitmaps
		mBitmap = null;			//lets garbage collector know that it can be cleaned up
	}

}