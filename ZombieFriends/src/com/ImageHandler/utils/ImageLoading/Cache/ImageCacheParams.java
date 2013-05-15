package com.ImageHandler.utils.ImageLoading.Cache;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

import com.ImageHandler.utils.Utils;

/**
 * A holder class that contains cache parameters.
 */
public class ImageCacheParams {
	// Default memory cache size in kilobytes
	private static final int			DEFAULT_MEM_CACHE_SIZE				= 1024 * 5;			// 5MB

	// Default disk cache size in bytes
	private static final int			DEFAULT_DISK_CACHE_SIZE				= 1024 * 1024 * 10;	// 10MB
	// Constants to easily toggle various caches
	private static final boolean		DEFAULT_MEM_CACHE_ENABLED			= true;
	private static final boolean		DEFAULT_DISK_CACHE_ENABLED			= true;
	private static final boolean		DEFAULT_CLEAR_DISK_CACHE_ON_START	= false;
	private static final boolean		DEFAULT_INIT_DISK_CACHE_ON_CREATE	= false;
	// Compression settings when writing images to disk cache
	private static final CompressFormat	DEFAULT_COMPRESS_FORMAT				= CompressFormat.PNG;
	private static final int			DEFAULT_COMPRESS_QUALITY			= 70;

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public int				memCacheSize			= ImageCacheParams.DEFAULT_MEM_CACHE_SIZE;
	public int				diskCacheSize			= ImageCacheParams.DEFAULT_DISK_CACHE_SIZE;
	public File				diskCacheDir;
	public CompressFormat	compressFormat			= ImageCacheParams.DEFAULT_COMPRESS_FORMAT;
	public int				compressQuality			= ImageCacheParams.DEFAULT_COMPRESS_QUALITY;
	public boolean			memoryCacheEnabled		= ImageCacheParams.DEFAULT_MEM_CACHE_ENABLED;
	public boolean			diskCacheEnabled		= ImageCacheParams.DEFAULT_DISK_CACHE_ENABLED;

	public boolean			clearDiskCacheOnStart	= ImageCacheParams.DEFAULT_CLEAR_DISK_CACHE_ON_START;

	public boolean			initDiskCacheOnCreate	= ImageCacheParams.DEFAULT_INIT_DISK_CACHE_ON_CREATE;

	public ImageCacheParams(Context context, String uniqueName) {
		diskCacheDir = getDiskCacheDir(context, uniqueName);
	}

	public ImageCacheParams(File diskCacheDir) {
		this.diskCacheDir = diskCacheDir;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !ImageCacheParams.isExternalStorageRemovable() ? ImageCacheParams.getExternalCacheDir(
				context).getPath()
				: context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * Sets the memory cache size based on a percentage of the max available VM
	 * memory. Eg. setting percent to 0.2 would set the memory cache to one
	 * fifth of the available memory. Throws {@link IllegalArgumentException} if
	 * percent is < 0.05 or > .8. memCacheSize is stored in kilobytes instead of
	 * bytes as this will eventually be passed to construct a LruCache which
	 * takes an int in its constructor.
	 * This value should be chosen carefully based on a number of factors Refer
	 * to the corresponding Android Training class for more discussion:
	 * http://developer.android.com/training/displaying-bitmaps/
	 * 
	 * @param percent
	 *            Percent of available app memory to use to size memory cache
	 */
	public void setMemCacheSizePercent(float percent) {
		if (percent < 0.05f || percent > 0.8f) {
			throw new IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.05 and 0.8 (inclusive)");
		}
		memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024);
	}

}