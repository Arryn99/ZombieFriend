/*
 * Copyright (C) 2012 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ImageHandler.utils.ImageLoading.Downloading;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.ImageHandler.utils.ImageLoading.Cache.CacheAsyncTask;
import com.ImageHandler.utils.ImageLoading.Cache.ImageCache;
import com.ImageHandler.utils.ImageLoading.Cache.ImageCacheParams;

/**
 * This class wraps up completing some arbitrary long running work when loading
 * a bitmap to an ImageView. It handles things like using a memory and disk
 * cache, running the work in a background thread and setting a placeholder
 * image.
 */
public abstract class ImageWorker {
	static int					runningThreads	= 0;

	private ImageCacheParams	mImageCacheParams;
	protected ImageCache		mImageCache;
	private Bitmap				mLoadingBitmap;

	private boolean				mExitTasksEarly	= false;

	private boolean				mPauseWork		= false;

	private final Object		mPauseWorkLock	= new Object();

	protected Resources			mResources;

	protected ImageWorker(Context context) {
		mResources = context.getResources();
	}

	public ImageCache getmImageCache() {
		return mImageCache;
	}

	public ImageCacheParams getmImageCacheParams() {
		return mImageCacheParams;
	}

	public Bitmap getmLoadingBitmap() {
		return mLoadingBitmap;
	}

	public Object getmPauseWorkLock() {
		return mPauseWorkLock;
	}

	public boolean isExitTasksEarly() {
		return mExitTasksEarly;
	}

	public boolean ismPauseWork() {
		return mPauseWork;
	}

	/**
	 * Load an image specified by the data parameter into an ImageView (override {@link ImageWorker#downloadInBackground(Object)} to define the processing
	 * logic). A memory and disk cache will be used if an {@link ImageCache} has
	 * been set using {@link ImageWorker#setImageCache(ImageCache)}. If the
	 * image is found in the memory cache, it is set immediately, otherwise an {@link AsyncTask} will be created to asynchronously load the bitmap.
	 * 
	 * @param data
	 *            The URL of the image to download.
	 * @param imageView
	 *            The ImageView to bind the downloaded image to.
	 */
	public void loadImage(ImageToCache data, ImageView imageView) {
		if (data == null) {
			return;
		}

		Bitmap bitmap = null;

		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(data.getKey());
		}

		if (bitmap != null) {
			// Bitmap found in memory cache
			imageView.setImageBitmap(bitmap);
		} else if (ImageWorker.cancelPotentialWork(data, imageView)) {
			// {
			// cancelWork(imageView);
			final BitmapWorkerTask task = new BitmapWorkerTask(imageView, this);
			final AsyncDrawable asyncDrawable = new AsyncDrawable(mResources, data.getPlaceHolderBitmap(), task);
			imageView.setImageDrawable(asyncDrawable);

			// NOTE: This uses a custom version of AsyncTask that has been
			// pulled from the
			// framework and slightly modified. Refer to the docs at the top of
			// the class
			// for more info on what was changed.
			try {
				task.executeOnExecutorBase(data);
			} catch (Exception e) {
				Log.w("Tru", "ImageWorker.loadImage thread pool is full");
			}

		}
	}

	/**
	 * Lets you download a bitmap without an image view returing the bitmap from
	 * the memory cache or starting a new thread an returning it to the passed
	 * in listener. if no listener no thread is created
	 * 
	 * @param data
	 *            Image to download;
	 * @param pListener
	 *            Lister to be notified of bitmap download. can be null if null
	 *            no thread is started to download image
	 * @return
	 */
	public Bitmap loadImage(ImageToCache data, IDownloadBitmapListener pListener) {
		if (data == null) {
			return null;
		}

		Bitmap bitmap = null;

		if (mImageCache != null) {
			bitmap = mImageCache.getBitmapFromMemCache(data.getKey());
		}

		if (bitmap != null) {
			pListener.gotBitmapForImage(bitmap, data);
			return bitmap;
		} else {
			if (pListener != null) {
				final DownloadBitmapAsync task = new DownloadBitmapAsync(this, pListener);
				try {
					task.executeOnExecutorBase(data);
				} catch (Exception e) {
					Log.w("Tru", "ImageWorker.loadImage thread pool is full");
				}

			}
		}
		return bitmap;
	}

	/**
	 * Subclasses should override this to define any processing or work that
	 * must happen to produce the final bitmap. This will be executed in a
	 * background thread and be long running. For example, you could resize a
	 * large bitmap here, or pull down an image from the network.
	 * 
	 * @param data
	 *            The data to identify which image to process, as provided by {@link ImageWorker#loadImage(Object, ImageView)}
	 * @return The processed bitmap
	 */
	protected abstract Bitmap downloadInBackground(ImageToCache data);

	public void setExitTasksEarly(boolean exitTasksEarly) {
		mExitTasksEarly = exitTasksEarly;
	}

	/**
	 * Sets the {@link ImageCache} object to use with this ImageWorker. Usually
	 * you will not need to call this directly, instead use {@link ImageWorker#addImageCache} which will create and add the {@link ImageCache} object in a
	 * background thread (to ensure no disk
	 * access on the main/UI thread).
	 * 
	 * @param imageCache
	 */
	public void setImageCache(ImageCache imageCache, Context context) {
		mImageCache = imageCache;
		try {
			new CacheAsyncTask(context).executeOnExecutorBase(CacheAsyncTask.MESSAGE_INIT_DISK_CACHE);
		} catch (Exception e) {
			Log.w("Tru", "ImageWorker.addImageCache thread pool is full");
		}
	}

	/**
	 * Set placeholder bitmap that shows when the the background thread is
	 * running.
	 * 
	 * @param bitmap
	 */
	public void setLoadingImage(Bitmap bitmap) {
		mLoadingBitmap = bitmap;
	}

	/**
	 * Set placeholder bitmap that shows when the the background thread is
	 * running.
	 * 
	 * @param resId
	 */
	public void setLoadingImage(int resId) {
		mLoadingBitmap = BitmapFactory.decodeResource(mResources, resId);
	}

	public void setPauseWork(boolean pauseWork) {
		synchronized (mPauseWorkLock) {
			mPauseWork = pauseWork;
			if (!mPauseWork) {
				mPauseWorkLock.notifyAll();
			}
		}
	}

	/**
	 * Called when the processing is complete and the final bitmap should be set
	 * on the ImageView.
	 * 
	 * @param imageView
	 * @param bitmap
	 */
	protected void setImageBitmap(ImageView imageView, Bitmap bitmap) {
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			imageView.setImageBitmap(getmLoadingBitmap());
		}
	}

	// private static final String TAG = "ImageWorker";

	/**
	 * Returns true if the current work has been canceled or if there was no
	 * work in progress on this image view. Returns false if the work in
	 * progress deals with the same data. The work is not stopped in that case.
	 */
	public static boolean cancelPotentialWork(ImageToCache data, ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = ImageWorker.getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) {
			final ImageToCache bitmapData = bitmapWorkerTask.data;
			if (bitmapData == null || !(bitmapData.getKey().equals(data.getKey()))) {
				bitmapWorkerTask.cancel(true);
			} else {
				// The same work is already in progress.
				return false;
			}
		}
		return true;
	}

	/**
	 * Cancels any pending work attached to the provided ImageView.
	 * 
	 * @param imageView
	 */
	public static void cancelWork(ImageView imageView) {
		final BitmapWorkerTask bitmapWorkerTask = ImageWorker.getBitmapWorkerTask(imageView);
		if (bitmapWorkerTask != null) {
			bitmapWorkerTask.cancel(true);
			// if (BuildConfig.DEBUG) {
			// final Object bitmapData = bitmapWorkerTask.data;
			// }
		}
	}

	/**
	 * @param imageView
	 *            Any imageView
	 * @return Retrieve the currently active work task (if any) associated with
	 *         this imageView. null if there is no such task.
	 */
	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) {
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				return asyncDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

}
