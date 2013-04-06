package com.ZombieFriends.GameObjects;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.R;
import com.ZombieFriends.Mechanics.Game;


public class Background extends GameObject
{	
	
	public Background(Context context)
	{
		super(context);
		setmBitmapWithSize(context, R.drawable.sky_bgr, Game.ScreenSize); //load a sky image
	}

	@Override
	public void update(float dt)
	{
	}
	
	public	void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);	
	}

}
