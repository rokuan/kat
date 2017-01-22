package com.ideal.apps.kat.service;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.ideal.apps.kat.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class RecordInfo {
    private ApplicationInfo application;
    private long startTime;
    private RecordThread recordThread;
    private Context context;

    class RecordThread extends Thread {
        private File outputFile;

        public RecordThread(File o){
            outputFile = o;
        }

        @Override
        public void run() {
            FileOutputStream os;
            InputStreamReader is;
            PrintWriter writer = null;
            BufferedReader reader = null;
            Process process = null;

            try {
                outputFile.createNewFile();
                os = new FileOutputStream(outputFile);
                writer = new PrintWriter(os, true);

                process = Runtime.getRuntime().exec("logcat -s " + application.packageName);
                is = new InputStreamReader(process.getInputStream());
                reader = new BufferedReader(is);
                String line;

                while((line = reader.readLine()) != null){
                    writer.println(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { writer.close(); } catch (Exception _) {}
                try { reader.close(); } catch (Exception _) {}
                try { process.destroy(); } catch (Exception e) {}
            }
        }
    }

    public RecordInfo(Context c, ApplicationInfo a){
        context = c;
        application = a;
        startTime = System.currentTimeMillis();
    }

    public void start(){
        startTime = System.currentTimeMillis();
        File output = FileUtils.getFilePath(context, application, startTime);
        recordThread = new RecordThread(output);
        recordThread.start();
    }

    public void stop(){
        try {
            recordThread.interrupt();
        } catch (Exception e) {

        }
    }
}
