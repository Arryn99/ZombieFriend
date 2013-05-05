package com.ZombieFriends;

import android.content.Context;

import com.ZombieFriends.ServerRequests.ImageDownloading.ImageManager;
import com.rockspin.ImageApplication;

public class ZombieApplication extends ImageApplication{

	/**
	 * manages all of the image retrieval within our application
	 */
	ImageManager mImageManager = new ImageManager();
	
	@Override
	public void onCreate()
	{
		// TODO Auto-generated method stub
		super.onCreate();
		mImageManager = new ImageManager();
	}
	
	static public ZombieApplication getApplication(Context context ){
		return (ZombieApplication)context.getApplicationContext();
	}
	
	public ImageManager getImageManager()
	{
		return mImageManager;
	}
}
