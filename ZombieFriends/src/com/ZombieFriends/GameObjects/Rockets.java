package com.ZombieFriends.GameObjects;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.R;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.Mechanics.Game;

public class Rockets extends GameObject
{

	boolean flicked = false;
	boolean appeared = false;
	
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
		Vector distanceMovedInFrame = Vector.multiply(mSpeed, dt);	//d = s*t
		mPosition = Vector.add(distanceMovedInFrame, mPosition);	//sets mPosition to be the distanceMovedInFrame + previous pos
	}

	public void setmSpeed(Vector mSpeed)
	{
		this.mSpeed = mSpeed;
	}
	
}
