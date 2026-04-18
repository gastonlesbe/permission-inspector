package com.lesbeguerisgaston.permissioninspector;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.lesbeguerisgaston.permissioninspector.adapter.PermissionAdapter;
import com.lesbeguerisgaston.permissioninspector.model.AppInfo;
import com.lesbeguerisgaston.permissioninspector.util.DeepLinkHelper;
import com.lesbeguerisgaston.permissioninspector.util.PermissionHelper;

import java.util.List;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);

        String packageName = getIntent().getStringExtra("package_name");
        if (packageName == null) { finish(); return; }

        // Load app info
        List<AppInfo> allApps = PermissionHelper.getInstalledApps(this);
        AppInfo app = null;
        for (AppInfo a : allApps) {
            if (a.packageName.equals(packageName)) { app = a; break; }
        }
        if (app == null) { finish(); return; }

        // Bind header
        ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(app.icon);
        ((TextView) findViewById(R.id.appName)).setText(app.appName);
        ((TextView) findViewById(R.id.permCount)).setText(
            app.permissions.size() + " permissions granted");

        // Bind permission list
        RecyclerView rv = findViewById(R.id.permissionsRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        final AppInfo finalApp = app;
        rv.setAdapter(new PermissionAdapter(app.permissions,
            perm -> DeepLinkHelper.openAppSettings(this, finalApp.packageName)));

        // Open settings button
        MaterialButton settingsBtn = findViewById(R.id.openSettingsBtn);
        settingsBtn.setOnClickListener(v ->
            DeepLinkHelper.openAppSettings(this, packageName));

        // Back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(app.appName);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
