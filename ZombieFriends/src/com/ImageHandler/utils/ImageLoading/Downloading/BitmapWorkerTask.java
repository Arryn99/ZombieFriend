package com.ImageHandler.utils.ImageLoading.Downloading;


import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.ImageHandler.utils.ImageLoading.BackwardsCompatibility.AsyncTaskBase;

/*
 * The actual AsyncTask that will asynchronously process the image.
 */
class BitmapWorkerTask extends AsyncTaskBase<ImageToCache, Void, Bitmap> {
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

	ImageToCache							data;
	private final WeakReference<ImageView>	imageViewReference;

	ImageWorker								mImageWorker;

	public BitmapWorkerTask(ImageView imageView, ImageWorker imageWorker) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		mImageWorker = imageWorker;
	}

	/**
	 * Background processing.
	 */
	@Override
	protected Bitmap doInBackground(ImageToCache... params) {

		data = params[0];
		final String dataString = data.getKey();
		Bitmap bitmap = null;

		// Wait here if work is paused and the task is not cancelled
		synchronized (mImageWorker.getmPauseWorkLock()) {
			while (mImageWorker.ismPauseWork() && !isCancelled()) {
				try {
					mImageWorker.getmPauseWorkLock().wait();
				} catch (final InterruptedException e) {}
			}
		}

		// If the image cache is available and this task has not been cancelled
		// by another
		// thread and the ImageView that was originally bound to this task is
		// still bound back
		// to this task and our "exit early" flag is not set then try and fetch
		// the bitmap from
		// the cache
		if (shouldContinue()) {
			bitmap = mImageWorker.getmImageCache().getBitmapFromDiskCache(dataString);
		}

		// If the bitmap was not found in the cache and this task has not been
		// cancelled by
		// another thread and the ImageView that was originally bound to this
		// task is still
		// bound back to this task and our "exit early" flag is not set, then
		// call the main
		// process method (as implemented by a subclass)
		if (bitmap == null && shouldContinue()) {
			bitmap = mImageWorker.downloadInBackground(data);
		}

		// allow some final processing of the image to take place
		bitmap = data.preCacheBitmap(bitmap);

		// If the bitmap was processed and the image cache is available, then
		// add the processed
		// bitmap to the cache for future use. Note we don't check if the task
		// was cancelled
		// here, if it was, and the thread is still running, we may as well add
		// the processed
		// bitmap to our cache as it might be used again in the future
		if (bitmap != null && mImageWorker.getmImageCache() != null) {
			mImageWorker.getmImageCache().addBitmapToCache(dataString, bitmap);
		}

		// allow some final processing of the image to take place
		if (bitmap != null) {

			bitmap = data.preImageLoad(bitmap);
		}

		return bitmap;
	}

	/**
	 * Returns the ImageView associated with this task as long as the
	 * ImageView's task still points to this task as well. Returns null
	 * otherwise.
	 */
	private ImageView getAttachedImageView() {
		final ImageView imageView = imageViewReference.get();
		final BitmapWorkerTask bitmapWorkerTask = BitmapWorkerTask.getBitmapWorkerTask(imageView);

		if (this == bitmapWorkerTask) {
			return imageView;
		}

		return null;
	}

	/**
	 * can be over ridden by sub classes to allow the image loader to exit under specific conditions
	 * 
	 * @return true if the threas should continue work
	 */
	protected boolean shouldContinue() {
		return (mImageWorker.getmImageCache() != null && !isCancelled() && getAttachedImageView() != null && !mImageWorker.isExitTasksEarly());
	}

	@Override
	protected void onCancelled(Bitmap bitmap) {
		super.onCancelled(bitmap);
		synchronized (mImageWorker.getmPauseWorkLock()) {
			mImageWorker.getmPauseWorkLock().notifyAll();
		}
	}

	/**
	 * Once the image is processed, associates it to the imageView
	 */
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		// if cancel was called on this task or the "exit early" flag is set
		// then we're done
		if (isCancelled() || mImageWorker.isExitTasksEarly()) {
			bitmap = null;
		}

		final ImageView imageView = getAttachedImageView();
		if (bitmap != null && imageView != null) {
			mImageWorker.setImageBitmap(imageView, bitmap);
		}
	}

}