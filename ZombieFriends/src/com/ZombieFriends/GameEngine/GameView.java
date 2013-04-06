/**
 * 
 */
package com.ZombieFriends.GameEngine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.Mechanics.Game;

/**
 * This is the main surface that handles the on touch events and draws
 * the image to the screen.
 */
public class GameView extends SurfaceView implements SurfaceHolder.Callback
{

	private static final String TAG = GameView.class.getSimpleName();

	private Game thread;
	
	protected RelativeLayout mRoot;
	

	public GameView(Context context, RelativeLayout root)
	{
		super(context);
		mRoot = root;
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		// create the game loop thread
		thread = new Game(getHolder(), this, root);
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// at this point the surface is created and
		// we can safely start the game loop
		thread.setRunning(true);
		thread.start();
		thread.addScreens(mRoot);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.d(TAG, "Surface is being destroyed");
		// tell the thread to shut down and wait for it to finish
		// this is a clean shutdown
		if (thread.isAlive())
		{
			thread.setRunning(false);
		}
		Log.d(TAG, "Thread was shut down cleanly");
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
		{
			//player has just touched the screen
			Vector touchPos = new Vector(event.getRawX(), event.getRawY());
			thread.onTouchEvent(Finger_Action.DOWN, touchPos, event.getEventTime());
			return true;
		}

		case MotionEvent.ACTION_UP:
		{
			//touch started on this game object
			Vector touchPos = new Vector(event.getRawX(), event.getRawY());
			thread.onTouchEvent(Finger_Action.UP, touchPos, event.getEventTime());
			return true;
		}

		case MotionEvent.ACTION_MOVE:
		{
			//player's finger is moving on the screen
			Vector touchPos = new Vector(event.getRawX(), event.getRawY());
			thread.onTouchEvent(Finger_Action.MOVE, touchPos, event.getEventTime());
			return true;
		}
		}
		return false;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawColor(Color.BLACK);
		thread.onDraw(canvas);
	}
}
