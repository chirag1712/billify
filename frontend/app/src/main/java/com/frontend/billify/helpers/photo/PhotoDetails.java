package com.frontend.billify.helpers.photo;

import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;


// Code taken and tweaked from https://stackoverflow.com/questions/36995146/why-images-are-rotated-90-degree-in-imageview
public class PhotoDetails {
    // A Photo Utility class with some helper methods

    public static int getCameraPhotoOrientation(File imageFile) {
        int rotate = 0;
        try {
            ExifInterface exif  = null;
            try {
                exif = new ExifInterface(imageFile.getAbsolutePath());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                default:
                    rotate = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

}
