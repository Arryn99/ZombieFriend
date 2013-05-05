package com.ZombieFriends.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.ZombieFriends.R;
import com.ZombieFriends.ZombieApplication;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.Mechanics.Game;
import com.rockspin.utils.ImageLoading.Downloading.IDownloadBitmapListener;
import com.rockspin.utils.ImageLoading.Downloading.ImageToCache;

public class Rockets extends GameObject implements IDownloadBitmapListener
{

	boolean flicked = false;
	boolean appeared = false;
	boolean loaded   = false;

	public boolean isAppeared() {
		return appeared;
	}

	public void setAppeared(boolean appeared) {
		this.appeared = appeared;
	}

	@Override
	public void onMove(Vector touchPos, Vector velocity)
	{
		super.onMove(touchPos, velocity);
		if (!flicked)
		{
			mSpeed = velocity;
			if(mSpeed.sqrMag() < 10){
				mSpeed = new Vector(10, 10);
			}
			flicked = true;
			Game.incrementScore();
		}
	}

	@Override
	protected void onTap()
	{
		super.onTap();
		flicked = false;
	}

	//@Override
	//public void onMoveInto(Vector touchPos, Vector velocity)
	//{
	//super.onMoveInto(touchPos, velocity);
	//mSpeed = velocity;
	//}

	Vector mSpeed = new Vector(0, 0);
	Vector initialPos = new Vector(0, 0);

	public Rockets(Context context, Vector spawnPosition)
	{
		super(context);
		setBitmap(context, R.drawable.rocket);
		mPosition = spawnPosition;

		ZombieApplication.getApplication(context).getImageManager().getRandomFriendImage(context, this);

	}

	@Override
	public void onTouch()
	{
		super.onTouch();
	}

	@Override
	public	void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	@Override
	public	void update(float dt)
	{
		if(loaded){
			Vector distanceMovedInFrame = Vector.multiply(mSpeed, dt);	//d = s*t
			mPosition = Vector.add(distanceMovedInFrame, mPosition);	//sets mPosition to be the distanceMovedInFrame + previous pos
		}
	}

	public void setmSpeed(Vector mSpeed)
	{
		this.mSpeed = mSpeed;
	}

	@Override
	public void gotBitmapForImage(Bitmap bitmap, ImageToCache cache) {
		this.setBitmap(bitmap);
		loaded = true;
	}

}
