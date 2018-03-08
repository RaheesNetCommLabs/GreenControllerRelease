package com.netcommlabs.greencontroller.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

/**
 * Created by Rahul on 10/29/2016.
 */
public class FileUtils {

    public static String getPathFromURI(Context mContext, Uri contentUri) throws Exception {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = mContext.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public static Bitmap getPhoto(String photoPath) {
        Bitmap bitMap = null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap myBitmap = BitmapFactory.decodeFile(photoPath);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitMap = rotateImage(myBitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitMap = rotateImage(myBitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitMap = rotateImage(myBitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
                bitMap = myBitmap;
                break;
            case ExifInterface.ORIENTATION_UNDEFINED:
                bitMap = myBitmap;
                break;
            default:
                bitMap = myBitmap;
                break;
        }
        return bitMap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public static void pickFromGallery(Activity mContext, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        mContext.startActivityForResult(Intent.createChooser(intent, "Select File"), requestCode);
    }

    public static void captureImage(Activity mContext, File fileName, int requestCode) {
        try {
           Uri tempURI = Uri.fromFile(fileName);
           /* ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            Uri tempURI= mContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);*/
            Intent photoCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempURI);
            mContext.startActivityForResult(photoCaptureIntent, requestCode);





        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static String compressNow(Activity mContext, final String file) {
        File compressedImage = null;
        try {
            FileUtils.deleteCache(mContext);
            compressedImage = new Compressor.Builder(mContext).setMaxWidth(500).setMaxHeight(1000).setCompressFormat(Bitmap.CompressFormat.WEBP).build().compressToFile(new File(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return compressedImage.getAbsolutePath();
    }

    public static Bitmap getBitmapWithCompressedFromPicker(Activity act, String file) {
        String f = null;
        f = compressNow(act, file);
        Log.e("@@@@@@@CompressURI_IS", f.toString());
        return getPhoto(f);
    }

    public static String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder();
        boolean nextTitleCase = true;
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }

            titleCase.append(c);
        }

        return titleCase.toString();
    }
}
