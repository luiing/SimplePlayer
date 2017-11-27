package com.uis.lib.simpleplayer.bitmap;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.ArrayMap;

/**
 * @author uis on 2017/11/21.
 */

public class PlayerBitmap {

    public static void createBitmapAsync(String url,long time,OnBitmapListener call){
        AsyncTask task = new BitmapTask();
        task.execute(url,time,call);
    }

    public static Bitmap createBitmap(String url, long time){
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new ArrayMap<String, String>());
            }else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime(time);
        }catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    static class BitmapTask extends AsyncTask{

        static class BitmapData{
            Bitmap bitmap;
            OnBitmapListener call;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            if(objects!=null && objects.length==3){
                String url = String.valueOf(objects[0]);
                long time = (long)objects[1];
                BitmapData data = new BitmapData();
                data.call = (OnBitmapListener)objects[2];
                data.bitmap = createBitmap(url,time);
                return data;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if(o!=null && o instanceof BitmapData){
                BitmapData data = (BitmapData)o;
                data.call.onBitmap(data.bitmap);
            }
        }
    };
}
