package com.ZombieFriends.Mechanics;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;

import com.ZombieFriends.GameEngine.GameThread.Finger_Action;
import com.ZombieFriends.GameEngine.Tools.Vector;
import com.ZombieFriends.GameObjects.Brick;

public class RoadController extends Mechanics {
	LinkedList<Brick> list = new LinkedList<Brick>();


	public RoadController(Context context) {
		super(context);
		//get the screen size 
		Vector screenSize = Game.ScreenSize;
		//position is where we last drew a brick
		Vector position   = Vector.Zero();

		while(position.getY() < screenSize.getY()){
			int tallestBrick = 0;
			while(position.getX() < screenSize.getX()){

				Brick tempBrick = new Brick(context);
				tempBrick.setPosition(position);

				//find the tallest brick in this row
				if(tempBrick.getSize().getY() > tallestBrick);
				tallestBrick = (int)tempBrick.getSize().getY();

				position = Vector.add(position, new Vector(tempBrick.getSize().getX(), 0));

				//add the brick to the list
				list.add(tempBrick);

			}
			//we are at the end of the screen so drop down to the next row.
			position = new Vector(0, position.getY() + tallestBrick);
		}


	}

	Random randomAngle = new Random();

	@Override
	public void onDraw(Canvas canvas) {
		for (Brick brick : list) {
		//	canvas.save();
		//	canvas.rotate(randomAngle.nextFloat()*5,brick.getPosition().getX() + brick.getSize().getX()/2,brick.getPosition().getY() + brick.getSize().getY()/2);
			brick.onDraw(canvas);
		//	canvas.restore();

		}
	}

	@Override
	public void update(float dt) {


	}

	@Override
	public void onTouchEvent(Finger_Action action, Vector position, long eventTime) {

	}

}
