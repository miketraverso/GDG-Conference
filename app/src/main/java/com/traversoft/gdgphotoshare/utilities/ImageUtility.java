package com.traversoft.gdgphotoshare.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Display;
import android.view.WindowManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ImageUtility {

    public static byte[] convertBitmapToByteArray(Bitmap bitmap) {
        String bitmapString = ImageUtility.convertBitmapToString(bitmap);
        if (!bitmapString.contentEquals("")) {
            return convertBitmapStringToByteArray(bitmapString);
        }
        return null;
    }

    public static String convertBitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    public static byte[] convertBitmapStringToByteArray(String bitmapByteString) {
        return Base64.decode(bitmapByteString, Base64.DEFAULT);
    }

    public static Uri savePictureInternally(Context context, Bitmap bitmap) {
        int cropHeight;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            cropHeight = bitmap.getWidth();
        } else {
            cropHeight = bitmap.getHeight();
        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropHeight, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        File mediaStorageDir = context.getFilesDir();
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }

        Uri fileContentUri = Uri.fromFile(mediaFile);

        return fileContentUri;
    }

    public static Uri savePicture(Context context, Bitmap bitmap) {
        int cropHeight;
        if (bitmap.getHeight() > bitmap.getWidth()) {
            cropHeight = bitmap.getWidth();
        } else {
            cropHeight = bitmap.getHeight();
        }

        bitmap = ThumbnailUtils.extractThumbnail(bitmap, cropHeight, cropHeight, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "GDGMeetup");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            FileOutputStream stream = new FileOutputStream(mediaFile);
            stream.write(out.toByteArray());
            stream.close();

        } catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }

        Uri fileContentUri = Uri.fromFile(mediaFile);
        return fileContentUri;
    }

    public static Uri turnBitmapToUri(Context context, Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bmp, "GDGMeetup", null);
        return Uri.parse(path);
    }

    public static Bitmap decodeSampledBitmapFromByte(Context context, byte[] bitmapBytes) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int reqWidth, reqHeight;
        Point point = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(point);
            reqWidth = point.x;
            reqHeight = point.y;
        } else {
            reqWidth = display.getWidth();
            reqHeight = display.getHeight();
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inMutable = true;
        options.inBitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inScaled = true;
        options.inDensity = options.outWidth;
        options.inTargetDensity = reqWidth * options.inSampleSize;
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;

        return BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int initialInSampleSize = computeInitialSampleSize(options, reqWidth, reqHeight);
        int roundedInSampleSize;
        if (initialInSampleSize <= 8) {
            roundedInSampleSize = 1;
            while (roundedInSampleSize < initialInSampleSize) {
                // Shift one bit to left
                roundedInSampleSize <<= 1;
            }
        } else {
            roundedInSampleSize = (initialInSampleSize + 7) / 8 * 8;
        }

        return roundedInSampleSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final double height = options.outHeight;
        final double width = options.outWidth;
        final long maxNumOfPixels = reqWidth * reqHeight;
        final int minSideLength = Math.min(reqHeight, reqWidth);
        int lowerBound = (maxNumOfPixels < 0) ? 1 :
                (int) Math.ceil(Math.sqrt(width * height / maxNumOfPixels));
        int upperBound = (minSideLength < 0) ? 128 :
                (int) Math.min(Math.floor(width / minSideLength),
                        Math.floor(height / minSideLength));

        if (upperBound < lowerBound) {
            // return larger bound when there is no overlapping zone.
            return lowerBound;
        }

        if (maxNumOfPixels < 0 && minSideLength < 0) {
            return 1;
        } else if (minSideLength < 0) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
