package com.griscal.permissioninspector;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.griscal.permissioninspector.adapter.PermissionAdapter;
import com.griscal.permissioninspector.model.AppInfo;
import com.griscal.permissioninspector.util.DeepLinkHelper;
import com.griscal.permissioninspector.util.InsetsHelper;
import com.griscal.permissioninspector.util.PermissionHelper;

import java.util.List;

public class AppDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detail);
        InsetsHelper.applyTopInset(findViewById(R.id.detailHeader));
        InsetsHelper.applyBottomInset(findViewById(R.id.detailScroll));

        String packageName = getIntent().getStringExtra("package_name");
        if (packageName == null) { finish(); return; }

        List<AppInfo> allApps = PermissionHelper.getInstalledApps(this);
        AppInfo app = null;
        for (AppInfo a : allApps) {
            if (a.packageName.equals(packageName)) { app = a; break; }
        }
        if (app == null) { finish(); return; }

        // Header
        ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(app.icon);
        ((TextView) findViewById(R.id.appName)).setText(app.appName);
        ((TextView) findViewById(R.id.permCount)).setText(
            getString(R.string.permissions_granted, app.permissions.size()));

        // Info card
        ((TextView) findViewById(R.id.valSize)).setText(app.installSize);
        ((TextView) findViewById(R.id.valCategory)).setText(app.category);
        ((TextView) findViewById(R.id.valInstalled)).setText(app.installDate);
        ((TextView) findViewById(R.id.valUpdated)).setText(app.lastUpdated);
        ((TextView) findViewById(R.id.valVersion)).setText(app.versionName);
        ((TextView) findViewById(R.id.valPermCount)).setText(
            String.valueOf(app.permissions.size()));

        // Permissions list
        RecyclerView rv = findViewById(R.id.permissionsRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        final AppInfo finalApp = app;
        rv.setAdapter(new PermissionAdapter(app.permissions,
            perm -> DeepLinkHelper.openAppSettings(this, finalApp.packageName)));

        // Settings button
        MaterialButton settingsBtn = findViewById(R.id.openSettingsBtn);
        settingsBtn.setOnClickListener(v ->
            DeepLinkHelper.openAppSettings(this, packageName));

        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }
}
