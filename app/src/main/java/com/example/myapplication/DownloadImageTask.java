package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

class DownloadImageTask extends AsyncTask<Pair<Long, String>, Integer, Bitmap> {
    private final Context context;
    private final String url = "http://img.omdbapi.com/?apikey=ff64ebbe&i=tt";
    private static Boolean isTaskCompleted = false;

    public DownloadImageTask(Context context) {
        this.context = context;
        isTaskCompleted = false;
    }

    protected Bitmap doInBackground(Pair<Long, String>... urls) {
        Bitmap mIcon = null;
        int ithPoster=1;
        for(Pair<Long, String> pair: urls) {
            Long movieId = pair.first;
            String imdbId = pair.second;
            try {
                InputStream in = new java.net.URL(url + imdbId).openStream();
                mIcon = BitmapFactory.decodeStream(in);
                saveToStorage(mIcon, Long.toString(movieId));

                publishProgress(Math.round(ithPoster *100 / urls.length));
                ithPoster++;
            } catch (Exception e) {
                //Log.e("Error", e.printStackTrace());
                e.printStackTrace();
            }
        }
        isTaskCompleted = true;
        return mIcon;
    }

    public static boolean isTaskCompleted() {
        return isTaskCompleted;
    }

    private void saveToStorage(Bitmap bmp, String id) {
        File directory = context.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, id);

        try (FileOutputStream fos = new FileOutputStream(myPath)) {
           bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}