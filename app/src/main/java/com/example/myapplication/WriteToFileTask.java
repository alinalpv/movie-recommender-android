package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class WriteToFileTask extends AsyncTask<String, Void, String> {
    private final Context context;
    private final String fileName;
    private boolean completed = false;
    public WriteToFileTask(String fileName, Context context) {
        this.context = context;
        this.fileName = fileName;
    }

    protected String doInBackground(String... ratings) {
        File directory = context.getDir("ratingsDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, fileName);
        try {
            Files.write(Paths.get(myPath.getPath()), "".getBytes(), StandardOpenOption.WRITE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String rating: ratings) {
            try {
                Files.write(Paths.get(myPath.getPath()),(rating + '\n').getBytes(), StandardOpenOption.APPEND);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        completed = true;
        return "";
    }
    public boolean isCompleted(){ return completed;}
}