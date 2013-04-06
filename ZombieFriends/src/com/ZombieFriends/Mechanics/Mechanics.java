package com.ZombieFriends.Mechanics;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;

public abstract class Mechanics
{
	public Mechanics(Context context)
	{
		super();
	}
	
	abstract public void onDraw(Canvas canvas);
	
	abstract public void update(float dt);

	abstract public void onTouchEvent(Finger_Action action, Vector position, long eventTime);
}
