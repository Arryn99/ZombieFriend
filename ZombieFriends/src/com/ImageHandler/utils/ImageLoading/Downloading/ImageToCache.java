package com.ImageHandler.utils.ImageLoading.Downloading;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * an abstract class that is used to hold data about an image being loaded
 * also contains utility functions to alter bitmap before it is saved into the caches and before it is loaded into the image. These both run on background
 * threads and
 * can be used for tasks such as adding round corners to an image and putting it in the cache. Or adding a color tint to a bitmap but not saving it to the cache
 * 
 */
public abstract class ImageToCache {
	/**
	 * returns the key used to cache this image
	 * This key will MUST conform to filename standards. I.e must not have spaces of wierd characters.
	 * 
	 * @return the Key you want to use to cache this image
	 */
	public abstract String getKey();

	int	mUID	= 0;

	public int getUID() {
		if (mUID == 0) {
			char[] charArray = getKey().toCharArray();
			for (char c : charArray) {
				int a = c;
				mUID += a;
			}
			mUID += charArray.length;
		}
		return mUID;
	}

	/**
	 * sets the placeholder image used when this image is loading
	 * 
	 * @return the placeHolder graphic
	 */
	public Bitmap getPlaceHolderBitmap() {
		return null;
	}

	public abstract void downloadAsync(ImageView pView);

	/**
	 * if you want to do some extra processing of the image i.e cropping
	 * changing color in a background thread before it is inserted into the
	 * cache the Bitmap can be null!!
	 * 
	 * @return the processed Bitmap
	 */
	public Bitmap preCacheBitmap(Bitmap pBitmapToCache) {
		return pBitmapToCache;
	}

	/**
	 * if you want to do some extra processing of the image i.e cropping
	 * changing color in a background thread before it is loaded into the image
	 * view
	 * 
	 * @return the processed Bitmap
	 */
	public Bitmap preImageLoad(Bitmap pBitmapToApply) {
		return pBitmapToApply;
	}
}
