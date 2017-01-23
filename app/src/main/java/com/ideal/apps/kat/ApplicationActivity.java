package com.ideal.apps.kat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ideal.apps.kat.service.LogService;
import com.ideal.apps.kat.util.FileUtils;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplicationActivity extends AppCompatActivity {
    public static String APPLICATION_INFO_EXTRA = "application_info_package";

    @BindView(R.id.application_icon)
    protected ImageView applicationIconView;
    @BindView(R.id.application_name)
    protected TextView applicationNameView;
    @BindView(R.id.application_package)
    protected TextView applicationPackageView;

    @BindView(R.id.application_logs)
    protected GridView applicationLogsView;
    protected LogAdapter logAdapter;

    @BindView(R.id.record_log)
    protected ImageButton recordButton;
    @BindView(R.id.pause_log)
    protected ImageButton pauseButton;
    @BindView(R.id.stop_log)
    protected ImageButton stopButton;

    private LogService service;
    private boolean bound = false;
    private ApplicationInfo application;

    private ServiceConnection serviceCallback = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogService.LogBinder binder = (LogService.LogBinder)iBinder;
            service = binder.getService();
            bound = true;
            updateButtonsState();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
            service = null;
        }
    };

    static class LogAdapter extends ArrayAdapter<File> {
        public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance(DateFormat.LONG);
        private LayoutInflater inflater;

        public LogAdapter(Context context, List<File> objects) {
            super(context, R.layout.log_item, objects);
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            File f = getItem(position);

            if(v == null){
                v = inflater.inflate(R.layout.log_item, parent, false);
            }

            Date creationDate = new Date(f.lastModified());
            TextView dateView = (TextView)v.findViewById(R.id.log_item_date);
            dateView.setText(DATE_FORMAT.format(creationDate));

            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        ButterKnife.bind(this);

        final PackageManager packageManager = getPackageManager();
        Bundle data = getIntent().getExtras();
        String applicationPackage = data.getString(APPLICATION_INFO_EXTRA);

        try {
            application = packageManager.getApplicationInfo(applicationPackage, 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO: display a popup an go back
            this.finish();
        }

        fillApplicationDetails();
        fillLogsGrid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, LogService.class);
        startService(intent);
        bindService(intent, serviceCallback, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound){
            unbindService(serviceCallback);
        }
    }

    protected void updateButtonsState(){
        boolean recording = service.isRecording(application);
        recordButton.setEnabled(!recording);
        recordButton.setVisibility(recording ? View.INVISIBLE : View.VISIBLE);
        stopButton.setEnabled(recording);
        stopButton.setVisibility(recording ? View.VISIBLE : View.INVISIBLE);
    }

    protected void fillApplicationDetails(){
        final PackageManager packageManager = getPackageManager();
        String applicationName = packageManager.getApplicationLabel(application).toString();
        applicationNameView.setText(applicationName);
        applicationPackageView.setText(application.packageName);
        applicationIconView.setImageDrawable(packageManager.getApplicationIcon(application));
    }

    protected void fillLogsGrid(){
        File logDirectory = FileUtils.getApplicationLogDirectory(this, application);

        if(logDirectory.exists()) {
            File[] logs = logDirectory.listFiles();
            List<File> applicationLogs = new ArrayList<>();
            for (File f : logs) {
                if (f.isFile()) {
                    applicationLogs.add(f);
                }
            }
            logAdapter = new LogAdapter(this, applicationLogs);
            applicationLogsView.setAdapter(logAdapter);
            applicationLogsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    File logFile = logAdapter.getItem(i);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(logFile);
                    //intent.setDataAndType(uri, "plain/text");
                    intent.setData(uri);
                    //startActivity(intent);
                }
            });
        }
    }

    @OnClick(R.id.record_log)
    public void recordLog(){
        if(service != null) {
            if(service.startRecording(application)){
                updateButtonsState();
            }
        }
    }

    @OnClick(R.id.pause_log)
    public void pauseLog(){
        // TODO:
    }

    @OnClick(R.id.stop_log)
    public void stopLog(){
        if(service != null){
            service.stopRecording(application);
            updateButtonsState();
            fillLogsGrid();
        }
    }

    @OnClick(R.id.clear_logs)
    public void clearLogs(){
        File logDirectory = FileUtils.getApplicationLogDirectory(this, application);
        File[] files = logDirectory.listFiles();

        if(files != null){
            for(File f: files){
                f.delete();
            }
        }
        fillLogsGrid();
    }
}
