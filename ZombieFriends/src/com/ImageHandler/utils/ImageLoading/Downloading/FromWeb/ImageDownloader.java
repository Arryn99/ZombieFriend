package com.ImageHandler.utils.ImageLoading.Downloading.FromWeb;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ImageHandler.ImageApplication;
import com.ImageHandler.utils.ImageLoading.Downloading.ImageToCache;
import com.ImageHandler.utils.ImageLoading.Downloading.ImageWorker;


/**
 * 
 * class that download an image form the internet in a background thread
 *
 */
public class ImageDownloader extends ImageWorker {

	protected ImageDownloader(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setImageCache(ImageApplication.getApplication(context).getImageCache(), context);
	}

	@Override
	protected Bitmap downloadInBackground(ImageToCache data) {
		Bitmap bitmap = null;
		ImageToDownload imageToDownload = (ImageToDownload)data;
		try {
			   bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageToDownload.getUrl()).getContent());
			} catch (MalformedURLException e) {
			  e.printStackTrace();
			} catch (IOException e) {
			  e.printStackTrace();
			}
		return bitmap;
	}
}
