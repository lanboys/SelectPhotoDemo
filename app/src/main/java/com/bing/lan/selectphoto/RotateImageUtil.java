package com.bing.lan.selectphoto;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

/**
 * Created by lean on 16/11/26.
 */
public class RotateImageUtil {

    /**
     *  获取位图的方向	0	90	180	  270
     *  @param path 图片的本地的文件地址
     */
    public static int getBitmapDegress(String path) {
        int degress = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orietnaton = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orietnaton) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degress = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degress = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degress = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return degress;
    }

    /**
     * 
     * 	当程序员发现如果返回的图片出现了旋转 可以通过该方法进行复位
     */
    public static Bitmap rotateBitmapByDegress(Bitmap bmp, int degress) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degress);
        Bitmap result = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return result;
    }

}
