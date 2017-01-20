package com.ideal.apps.kat;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.apps_grid)
    protected ListView apps;

    @BindView(R.id.search_app)
    protected SearchView searchView;

    protected ApplicationAdapter applicationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PackageManager packageManager = getPackageManager();
        List<ApplicationInfo> packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> applications = new ArrayList<>();

        for(ApplicationInfo a: packages){
            if((a.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                applications.add(a);
            }
        }

        applicationAdapter = new ApplicationAdapter(this, applications);
        apps.setAdapter(applicationAdapter);
        apps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                openApplicationState(applicationAdapter.getItem(i));
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applicationAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public void openApplicationState(ApplicationInfo application){

    }

    class ApplicationAdapter extends ArrayAdapter<ApplicationInfo> {
        private List<ApplicationInfo> applications;

        public ApplicationAdapter(Context context, List<ApplicationInfo> objects) {
            super(context, R.layout.app_item, objects);
            applications = new ArrayList<>(objects);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            final PackageManager packageManager = getContext().getPackageManager();

            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    String query = constraint.toString().toLowerCase();
                    FilterResults result = new FilterResults();

                    if (query.toString().length() > 0) {
                        List<ApplicationInfo> found = new ArrayList<ApplicationInfo>();

                        for (ApplicationInfo application: applications) {
                            if (packageManager.getApplicationLabel(application).toString().toLowerCase().contains(constraint)){
                                found.add(application);
                            }
                        }

                        result.values = found;
                        result.count = found.size();
                    } else {
                        result.values = applications;
                        result.count = applications.size();
                    }

                    return result;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    clear();
                    for (ApplicationInfo application: (List<ApplicationInfo>) results.values) {
                        add(application);
                    }
                    notifyDataSetChanged();
                }
            };
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ApplicationInfo application = getItem(position);
            PackageManager packageManager = getContext().getPackageManager();

            if(v == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.app_item, parent, false);
            }

            ImageView appIcon = (ImageView)v.findViewById(R.id.app_item_icon);
            try {
                Drawable icon = packageManager.getApplicationIcon(application.packageName);
                appIcon.setImageDrawable(icon);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            TextView appName = (TextView)v.findViewById(R.id.app_item_name);
            String applicationName = packageManager.getApplicationLabel(application).toString();
            appName.setText(applicationName);

            TextView appPackage = (TextView)v.findViewById(R.id.app_item_package);
            appPackage.setText(application.packageName);

            return v;
        }
    }
}
