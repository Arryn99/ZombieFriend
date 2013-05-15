package com.ImageHandler.utils.ImageLoading.Downloading;

import android.graphics.Bitmap;

/*
 * The actual AsyncTask that will asynchronously process the image. doesnt set it just returns a bitmap
 */
class DownloadBitmapAsync extends BitmapWorkerTask {

	private IDownloadBitmapListener	mListener;

	/**
	 * @param pImageView
	 * @param pImageWorker
	 */
	public DownloadBitmapAsync(ImageWorker pImageWorker, IDownloadBitmapListener listener) {
		super(null, pImageWorker);
		mListener = listener;
	}

	@Override
	protected void onCancelled(Bitmap bitmap) {
		super.onCancelled(bitmap);
		synchronized (mImageWorker.getmPauseWorkLock()) {
			mImageWorker.getmPauseWorkLock().notifyAll();
		}
	}

	/*
	 * nesscesary do that do in backgroudn doesnt exit because they're is no attached image View
	 * (non-Javadoc)
	 */
	@Override
	protected boolean shouldContinue() {
		// dont check that the attached image view is null
		return (mImageWorker.getmImageCache() != null && !isCancelled() && !mImageWorker.isExitTasksEarly());
	};

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

		if (mListener != null) {
			mListener.gotBitmapForImage(bitmap, data);
		}
	}

}