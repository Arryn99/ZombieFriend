package com.ZombieFriends.GameObjects;

import java.util.Random;

import com.ZombieFriends.R;

import android.content.Context;

public class Brick extends GameObject {
	Random rr = new Random();
	//for now only one image but we can add more later
	int brickResources[] = {R.drawable.brick, R.drawable.brick0, R.drawable.brick1,R.drawable.brick3, R.drawable.brick4,
			R.drawable.brick5};
//public 	float angle = 0;
	Random randomAngle = new Random();
	
	public Brick(Context context) {
		super(context);
		
		// pick a random image 
		
		int index = rr.nextInt(brickResources.length);
		
		//set the image for this game object
		setBitmap( context, brickResources[index] );

		//angle = randomAngle.nextFloat()*5;
	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub

	}

}
