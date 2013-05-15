/**
 * 
 */
package com.ImageHandler.utils.ImageLoading.Downloading;


import android.graphics.Bitmap;

public interface IDownloadBitmapListener {
	public void gotBitmapForImage(Bitmap bitmap, ImageToCache cache);
}
