package com.lesbeguerisgaston.permissioninspector.model;

import java.util.List;

public class AppInfo {
    public String appName;
    public String packageName;
    public android.graphics.drawable.Drawable icon;
    public List<PermissionInfo> permissions;
    public int riskLevel; // 0 = low, 1 = medium, 2 = high

    public AppInfo(String appName, String packageName,
                   android.graphics.drawable.Drawable icon,
                   List<PermissionInfo> permissions) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.permissions = permissions;
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
