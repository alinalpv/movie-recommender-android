package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.os.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CopyRatingsTask extends AsyncTask<String, Void, String> {
    private final File ratingsFile;
    private final File ratingsUpdateFile;
    private Context context;
    private static boolean isCompleted = false;

    public CopyRatingsTask(File ratingsFile, File ratingsUpdateFile, Context context) {
        this.ratingsFile = ratingsFile;
        this.ratingsUpdateFile = ratingsUpdateFile;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream in = context.getResources().openRawResource(R.raw.ratings);
        try(FileOutputStream out = new FileOutputStream(ratingsFile)){
            FileUtils.copy(in, out);
            Recommender.init(ratingsFile);
            Files.createFile(Paths.get(ratingsUpdateFile.getPath()));
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        isCompleted = true;
        return "";
    }
    public static boolean isCompleted(){return isCompleted;}
}