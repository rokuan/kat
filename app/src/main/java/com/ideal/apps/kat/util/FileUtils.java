package com.ideal.apps.kat.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
    public static final String KAT_DIRECTORY = "KatLogs";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();

    public static File getApplicationLogDirectory(Context context, ApplicationInfo application){
        File outputDir = context.getExternalFilesDir(null);
        return new File(new File(outputDir, KAT_DIRECTORY), application.packageName);
    }

    public static File getFilePath(Context context, ApplicationInfo application, long currentTime){
        File applicationDir = getApplicationLogDirectory(context, application);
        Date startDate = new Date(currentTime);
        return new File(applicationDir, DATE_FORMAT.format(startDate));
    }
}
