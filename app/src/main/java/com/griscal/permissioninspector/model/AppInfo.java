package com.griscal.permissioninspector.model;

import java.util.List;

public class AppInfo {
    public String appName;
    public String packageName;
    public android.graphics.drawable.Drawable icon;
    public List<PermissionInfo> permissions;
    public int riskLevel;       // 0 = low, 1 = medium, 2 = high

    public String installSize;  // e.g. "82 MB"
    public String installDate;  // e.g. "Jan 2022"
    public String lastUpdated;  // e.g. "2 days ago"
    public String versionName;  // e.g. "310.0.0"
    public String category;     // e.g. "Social"

    public AppInfo(String appName, String packageName,
                   android.graphics.drawable.Drawable icon,
                   List<PermissionInfo> permissions,
                   String installSize, String installDate,
                   String lastUpdated, String versionName,
                   String category) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.permissions = permissions;
        this.installSize = installSize;
        this.installDate = installDate;
        this.lastUpdated = lastUpdated;
        this.versionName = versionName;
        this.category = category;
        this.riskLevel = calculateRisk(permissions);
    }

    private int calculateRisk(List<PermissionInfo> perms) {
        for (PermissionInfo p : perms) {
            if (p.riskLevel == 2) return 2;
        }
        for (PermissionInfo p : perms) {
            if (p.riskLevel == 1) return 1;
        }
        return 0;
    }
}
