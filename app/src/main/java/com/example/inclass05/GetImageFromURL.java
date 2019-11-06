package com.example.inclass05;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.HttpURLConnection;
import java.net.URL;

public class GetImageFromURL extends AsyncTask<String, Void, Bitmap> {

    MainActivity activity;

    Bitmap bitmap = null;
    String URL=null;

    public GetImageFromURL(MainActivity activity) {

        this.activity=activity;

    }

    @Override
    protected Bitmap doInBackground(String... URL) {
        HttpURLConnection connection = null;
        bitmap = null;
        try {

                URL url = new URL(URL[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bitmap = BitmapFactory.decodeStream(connection.getInputStream());
                }

        } catch (Exception e) {
            Log.d("NoImage",e.toString());
            //Handle the exceptions
        } finally {
            //Close open connection
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap != null && activity.iv_Result != null) {

            activity.iv_Result.setImageBitmap(bitmap);


        }
    }



}
