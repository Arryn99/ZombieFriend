package com.ImageHandler.utils.ImageLoading;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetFileDescriptor.AutoCloseInputStream;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.ImageHandler.utils.ImageLoading.Downloading.ImageWorker;

/**
 * ============================================================================
 * ===================== A simple subclass of {@link ImageWorker} that resizes
 * images from resources given a target width and height. Useful for when the
 * input images might be too large to simply load directly into memory.
 * ==========
 * ====================================================================
 * ======================
 */
public abstract class ImageResizer extends ImageWorker {

	public ImageResizer(Context context) {
		super(context);
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding bitmaps using the decode* methods from {@link BitmapFactory}.
	 * This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSizeExact(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		if (reqHeight < 0) {
			return 1;
		}
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee a final image
			// with both dimensions larger than or equal to the requested height
			// and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding bitmaps using the decode* methods from {@link BitmapFactory}.
	 * This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSizeFast(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		if (reqHeight < 0) {
			return 1;
		}

		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			final Boolean scaleByHeight = Math.abs(options.outHeight
					- reqHeight) >= Math.abs(options.outWidth - reqWidth);

			// We calculate the sampleFactor, 2 will mean a factor of 0.5
			final int sampleSize = scaleByHeight ? heightRatio : widthRatio;

			inSampleSize = (int) Math.pow(2d,
					Math.floor(Math.log(sampleSize) / Math.log(2d)));

			// This offers some additional logic in case the image has a strange
			// aspect ratio. For example, a panorama may have a much larger
			// width than height. In these cases the total pixels might still
			// end up being too large to fit comfortably in memory, so we should
			// be more aggressive with sample down the image (=larger
			// inSampleSize).

			final float totalPixels = width * height;

			// Anything more than 2x the requested pixels we'll sample down
			// further
			final float totalReqPixelsCap = reqWidth * reqHeight * 2;

			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	/**
	 * Decode and sample down a bitmap from a byte array stream to the
	 * requestedInputStream width and height.
	 * 
	 * @param fileDescriptor
	 *            The file descriptor to read from * @param fast If true use
	 *            power of 2 scaling for image else scale exactly
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromByteArray(byte[] byteArray,
			boolean fast, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);// (fileDescriptor,
																				// null,
																				// options);

		if (fast) {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeFast(
					options, reqWidth, reqHeight);
		} else {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeExact(
					options, reqWidth, reqHeight);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length,
				options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param fast
	 *            If true use power of 2 scaling for image else scale exactly
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFile(String filename,
			Boolean fast, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filename, options);

		if (fast) {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeFast(
					options, reqWidth, reqHeight);
		} else {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeExact(
					options, reqWidth, reqHeight);
		}
		;

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filename, options);
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param fast
	 *            If true use power of 2 scaling for image else scale exactly
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, Boolean fast, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		if (fast) {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeFast(
					options, reqWidth, reqHeight);
		} else {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeExact(
					options, reqWidth, reqHeight);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * 
	 * @param filename
	 *            The full path of the file to decode
	 * @param fast
	 *            If true use power of 2 scaling for image else scale exactly
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and he
	 */
	public static Bitmap decodeSampledBitmapFromAutoCloseInputStream(
			AssetFileDescriptor.AutoCloseInputStream stream, Boolean fast,
			int reqWidth, int reqHeight) {
		try {
			final FileDescriptor fd = stream.getFD();
			return ImageResizer.decodeSampledBitmapFromDescriptor(fd, fast,
					reqWidth, reqHeight);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * Decode and sample down a bitmap from a file to the requested width and
	 * height.
	 * @param filename The full path of the file to decode
	 * @param fast If true use power of 2 scaling for image else scale exactly
	 * @param reqWidth The requested width of the resulting bitmap
	 * @param reqHeight The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 * ratio and dimensions that are equal to or greater than the requested
	 * width and he
	 */
	public static Bitmap decodeSampledBitmapFromStream(InputStream stream,
			Boolean fast, int reqWidth, int reqHeight) {
		try {
			if (stream.getClass().equals(AutoCloseInputStream.class)) {
				AutoCloseInputStream s = (AutoCloseInputStream) stream;
				return ImageResizer.decodeSampledBitmapFromDescriptor(
						s.getFD(), fast, reqWidth, reqHeight);
			}

			stream.mark(1024 * 100);
			// First decode with inJustDecodeBounds=true to check dimensions
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, options);
			stream.reset();

			if (fast) {
				// Calculate inSampleSize
				options.inSampleSize = ImageResizer.calculateInSampleSizeFast(
						options, reqWidth, reqHeight);
			} else {
				// Calculate inSampleSize
				options.inSampleSize = ImageResizer.calculateInSampleSizeExact(
						options, reqWidth, reqHeight);
			}

			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			return BitmapFactory.decodeStream(stream, null, options);
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Decode and sample down a bitmap from a file input stream to the requested
	 * width and height.
	 * 
	 * @param fileDescriptor
	 *            The file descriptor to read from * @param fast If true use
	 *            power of 2 scaling for image else scale exactly
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromDescriptor(
			FileDescriptor fileDescriptor, boolean fast, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

		if (fast) {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeFast(
					options, reqWidth, reqHeight);
		} else {
			// Calculate inSampleSize
			options.inSampleSize = ImageResizer.calculateInSampleSizeExact(
					options, reqWidth, reqHeight);
		}

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory
				.decodeFileDescriptor(fileDescriptor, null, options);
	}

	static public Bitmap getRoundedCornerBitmap(Bitmap pBitmap,
			float pRoundRectCorner) {
		final Bitmap output = Bitmap.createBitmap(pBitmap.getWidth(),
				pBitmap.getHeight(), Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, pBitmap.getWidth(),
				pBitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);

		canvas.drawRoundRect(rectF, pRoundRectCorner, pRoundRectCorner, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(pBitmap, rect, rect, paint);

		return output;
	}

	/**
	 * Will resize and crop an image so that it fills the entire imageView.
	 * 
	 * @param context
	 *            a context need to get the resources
	 * @param pBitmap
	 * @param imageViewWidth
	 * @param imageViewHeight
	 * @return
	 */
	static public Bitmap cropBitmapToFit(Bitmap pBitmap, int imageViewWidth, int imageViewHeight) {
			int sourceWidth  = pBitmap.getWidth();
		    int sourceHeight = pBitmap.getHeight();

		    // Compute the scaling factors to fit the new height and width, respectively.
		    // To cover the final image, the final scaling will be the bigger 
		    // of these two.
		    float xScale = (float) imageViewWidth  / sourceWidth;
		    float yScale = (float) imageViewHeight / sourceHeight;
		    float scale  = Math.max(xScale, yScale);

		    // Now get the size of the source bitmap when scaled
		    float scaledWidth = scale * sourceWidth;
		    float scaledHeight = scale * sourceHeight;

		    // Let's find out the upper left coordinates if the scaled bitmap
		    // should be centered in the new size give by the parameters
		    float left = (imageViewWidth - scaledWidth) / 2;
		    float top = (imageViewHeight - scaledHeight) / 2;

		    // The target rectangle for the new, scaled version of the source bitmap will now
		    // be
		    RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

		    // Finally, we create a new bitmap of the specified size and draw our new,
		    // scaled bitmap onto it.
		    Bitmap dest = Bitmap.createBitmap(imageViewWidth, imageViewHeight, pBitmap.getConfig());
		    Canvas canvas = new Canvas(dest);
		    canvas.drawBitmap(pBitmap, null, targetRect, null);

		    return dest;
	}

	static public Bitmap cropBitmapToFitandRoundCorners(Bitmap pBitmap, int imageViewWidth, int imageViewHeight, int pRoundRectCornerRadius) {
		Bitmap bitmap = ImageResizer.cropBitmapToFit(pBitmap, imageViewWidth,imageViewHeight);
		return ImageResizer.getRoundedCornerBitmap(bitmap, pRoundRectCornerRadius);
	}

}
