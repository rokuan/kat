package com.ideal.apps.kat.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LogService extends Service {
    public class LogBinder extends Binder {
        public LogService getService(){
            return LogService.this;
        }
    }

    private LogBinder binder = new LogBinder();
    private NotificationManager notificationManager;
    private Map<String, RecordInfo> runningRecordings = new ConcurrentHashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public synchronized boolean isRecording(ApplicationInfo application){
        return runningRecordings.containsKey(application.packageName);
    }

    private void addRecordNotification(ApplicationInfo application){

    }

    public synchronized boolean startRecording(ApplicationInfo application){
        try {
            if(!isRecording(application)) {
                String packageName = application.packageName;
                RecordInfo record = new RecordInfo(this, application);
                runningRecordings.put(packageName, record);
                record.start();
                addRecordNotification(application);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public synchronized void stopRecording(ApplicationInfo application){
        if(isRecording(application)){
            String packageName = application.packageName;
            RecordInfo record = runningRecordings.get(packageName);
            runningRecordings.remove(packageName);
            record.stop();
        }
    }
}
