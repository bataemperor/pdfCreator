package com.example.bataemperor.pdfcreator;

import android.app.Application;
import android.os.Environment;
import android.support.v7.widget.AppCompatSpinner;

import java.io.File;

/**
 * Created by aleksandar on 5.8.16..
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        File EXTERNAL_CACHE_DIR = new File(Environment.getExternalStorageDirectory(), "CV");
        EXTERNAL_CACHE_DIR.mkdirs();
    }
}
