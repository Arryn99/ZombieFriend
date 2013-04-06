package com.ZombieFriends.GameObjects;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.Tools.Vector;

public class Cloud extends GameObject
{
	Vector mSpeed = new Vector(0, 0);
	Vector initialPos = new Vector(0, 0);
	Random mRandom = new Random();
	int mAlpha = 0;

	boolean appeared = false;

	public boolean isAppeared() {
		return appeared;
	}

	public void setAppeared(boolean appeared) {
		this.appeared = appeared;
	}


	public Cloud(Context context, Vector spawnPosition)
	{
		super(context);
		int sizeX       = 100 + mRandom.nextInt(300); // pick a random size for the cloud
		int sizeY       = 200 - mRandom.nextInt(150);
		setmBitmapWithSize(context,  R.drawable.cloud, new Vector(sizeX,sizeY));
		mPosition = spawnPosition;
		mAlpha = 100+ mRandom.nextInt(100);
	}

	@Override
	public void onTouch()
	{
		super.onTouch();
	}

	@Override
	public	void onDraw(Canvas canvas)
	{
		mBitmapPaint.setAlpha(mAlpha);
		super.onDraw(canvas);
	}

	@Override
	public	void update(float dt)
	{
		Vector distanceMovedInFrame = Vector.multiply(mSpeed, dt);	//d = s*t
		mPosition = Vector.add(distanceMovedInFrame, mPosition);	//sets mPosition to be the distanceMovedInFrame + previous pos
	}

	public void setmSpeed(Vector mSpeed)
	{
		this.mSpeed = mSpeed;
	}

}
