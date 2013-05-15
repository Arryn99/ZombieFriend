package com.ImageHandler;
import android.app.Application;
import android.content.Context;

import com.ImageHandler.utils.ImageLoading.Cache.ImageCache;

public class ImageApplication extends Application{
	/**
	 * a disc and image cache
	 */
	ImageCache mImageCache;

	 public void onCreate(){
		createImageCache();
	}

	/**
	 * sets up the image cache for use in our application
	 */
	private void createImageCache() {
		if (mImageCache == null) {
			mImageCache = new ImageCache(getApplicationContext());
		}
	}
	
	public static ImageApplication getApplication(Context context){
		ImageApplication application= (ImageApplication) context.getApplicationContext();
		return application;
	}

	public ImageCache getImageCache() {
		// TODO Auto-generated method stub
		return mImageCache;
	}
}
