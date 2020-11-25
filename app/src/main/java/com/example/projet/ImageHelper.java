package com.example.projet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageHelper {
    public static int calculte(BitmapFactory.Options options,int reqHeight,int reqWidth){
        final int height = options.outHeight;
        final int widht = options.outWidth;
        int intsamplesize = 1;
        if (height> reqHeight || widht>reqWidth){
            final int halfh = height / 2;
            final int halfw = widht / 2;

            while ( (halfh/intsamplesize)>=reqHeight && (halfw/intsamplesize)>=reqWidth){
                intsamplesize*=2;
            }
        }
        return intsamplesize;

    }
    public static Bitmap decodefrompath(String path,int reqWidth, int reqHeight ){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        options.inSampleSize = calculte(options,reqHeight,reqWidth);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path,options);

    }
}
