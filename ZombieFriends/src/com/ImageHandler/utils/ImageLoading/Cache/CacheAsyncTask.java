package com.ImageHandler.utils.ImageLoading.Cache;

import android.content.Context;

import com.ImageHandler.ImageApplication;
import com.ImageHandler.utils.ImageLoading.BackwardsCompatibility.AsyncTaskBase;

// TODO abstract this so that it will work with any app

/**
 * Utility class to do long operations in background thread
 * 
 */
public class CacheAsyncTask extends AsyncTaskBase<Object, Void, Void> {

	public static final int	MESSAGE_CLEAR			= 0;
	public static final int	MESSAGE_INIT_DISK_CACHE	= 1;
	public static final int	MESSAGE_FLUSH			= 2;
	public static final int	MESSAGE_CLOSE			= 3;
	
	Context mContext;
	
	public CacheAsyncTask(Context context ) {
		super();
		mContext = context;
	}

	protected void clearCacheInternal() {
		ImageApplication.getApplication(mContext).getImageCache().clearCache();
	}

	protected void closeCacheInternal() {
		ImageApplication.getApplication(mContext).getImageCache().close();
	}

	@Override
	protected Void doInBackground(Object... params) {
		switch ((Integer) params[0]) {
			case MESSAGE_CLEAR:
				clearCacheInternal();
				break;
			case MESSAGE_INIT_DISK_CACHE:
				initDiskCacheInternal();
				break;
			case MESSAGE_FLUSH:
				flushCacheInternal();
				break;
			case MESSAGE_CLOSE:
				closeCacheInternal();
				break;
		}
		return null;
	}

	protected void flushCacheInternal() {
		ImageApplication.getApplication(mContext).getImageCache().flush();
	}

	protected void initDiskCacheInternal() {
		ImageApplication.getApplication(mContext).getImageCache().initDiskCache();
	}

}