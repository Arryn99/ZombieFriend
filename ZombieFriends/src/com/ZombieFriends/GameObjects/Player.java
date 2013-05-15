package com.ZombieFriends.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.ImageHandler.utils.ImageLoading.Downloading.IDownloadBitmapListener;
import com.ImageHandler.utils.ImageLoading.Downloading.ImageToCache;
import com.ZombieFriends.R;
import com.ZombieFriends.ZombieApplication;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.Mechanics.Game;

public class Player extends GameObject implements IDownloadBitmapListener
{
	int health = 5;	//determine the health of the player
	int mDirection = 1;	//determines player's direction (-1 is up, 1 is down)
	
	public Player(Context context)
	{
		super(context);

		setBitmap(context, R.drawable.plane);
		mPosition.setX(Game.ScreenSize.getX()-getSize().getX() - 5);		//position player to far right of screen

		ZombieApplication.getApplication(context).getImageManager().getProfileImage(context, this);

}

	@Override
	public	void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
	}

	
	public void decrementHealth()
	{
		health--;	//decrease health by 1
		if (health == 0)
		{
			Game.endGame();	//end the game when health reaches 0
		}
	}

	@Override
	public void update(float dt)
	{
		Vector speed = new Vector(0, 50);
		speed = Vector.multiply(speed, dt*mDirection);
		mPosition = Vector.add(mPosition, speed);
		
		
		if (mPosition.getY() < 0)
		{
			mDirection *= -1;
		}

		else if (mPosition.getY() > (Game.ScreenSize.getY() - getSize().getY()))
		{
			mDirection *= -1;
		}
		
		keepObjectOnScreen();
	}

	@Override
	public void gotBitmapForImage(Bitmap bitmap, ImageToCache cache) {
		mPosition = new Vector (Game.ScreenSize.getX() - bitmap.getWidth(), Game.ScreenSize.getY() - bitmap.getHeight());
		this.mBitmap = bitmap;
	}

}
