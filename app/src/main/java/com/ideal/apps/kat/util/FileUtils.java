package com.ideal.apps.kat.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH'h'mm'm'ss's'");
    public static final String LOG_FILE_EXTENSION = ".txt";

    public static File getApplicationLogDirectory(Context context, ApplicationInfo application){
        File outputDir = context.getExternalFilesDir(null);
        return new File(outputDir, application.packageName);
    }

    public static File getFilePath(Context context, ApplicationInfo application, long currentTime){
        File applicationDir = getApplicationLogDirectory(context, application);
        Date startDate = new Date(currentTime);
        return new File(applicationDir, DATE_FORMAT.format(startDate) + LOG_FILE_EXTENSION);
    }
}
