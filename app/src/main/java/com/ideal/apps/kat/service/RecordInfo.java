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
        private volatile boolean running = true;

        public RecordThread(File o){
            outputFile = o;
        }

        public void startRecord(){
            running = true;
            start();
        }

        @Override
        public void run() {
            FileOutputStream os;
            InputStreamReader is;
            PrintWriter writer = null;
            BufferedReader reader = null;
            Process process = null;

            outputFile.getParentFile().mkdirs();

            try {
                outputFile.createNewFile();

                os = new FileOutputStream(outputFile);
                writer = new PrintWriter(os, true);

                Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat");
                is = new InputStreamReader(process.getInputStream());
                reader = new BufferedReader(is);

                while(running){
                    String line = reader.readLine();
                    if(line != null){
                        writer.println(line);
                    }
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Recording has stopped");
                try { writer.close(); } catch (Exception _) {}
                try { reader.close(); } catch (Exception _) {}
                try { process.destroy(); } catch (Exception e) {}
            }
        }

        public void stopRecord(){
            running = false;
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
        recordThread.startRecord();
    }

    public void stop(){
        try {
            recordThread.stopRecord();
        } catch (Exception e) {

        }
    }
}
