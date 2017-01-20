package com.ideal.apps.kat;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ApplicationActivity extends AppCompatActivity {
    public static String APPLICATION_INFO_EXTRA = "application_info_package";

    @BindView(R.id.application_name)
    protected TextView applicationNameView;
    @BindView(R.id.application_package)
    protected TextView applicationPackageView;

    @BindView(R.id.record_log)
    protected Button recordButton;
    @BindView(R.id.pause_log)
    protected Button pauseButton;
    @BindView(R.id.stop_log)
    protected Button stopButton;

    private ApplicationInfo application;

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
            fillApplicationDetails();
        } catch (PackageManager.NameNotFoundException e) {
            // TODO: display a popup an go back
            this.finish();
        }
    }

    protected void updateButtonsState(){
        // TODO:
    }

    protected void fillApplicationDetails(){
        final PackageManager packageManager = getPackageManager();
        String applicationName = packageManager.getApplicationLabel(application).toString();
        applicationNameView.setText(applicationName);
        applicationPackageView.setText(application.packageName);
    }

    @OnClick(R.id.record_log)
    public void recordLog(){
        // TODO:
    }

    @OnClick(R.id.pause_log)
    public void pauseLog(){
        // TODO:
    }

    @OnClick(R.id.stop_log)
    public void stopLog(){
        // TODO:
    }
}
