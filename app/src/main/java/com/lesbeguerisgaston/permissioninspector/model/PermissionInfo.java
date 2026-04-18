package com.lesbeguerisgaston.permissioninspector.model;

public class PermissionInfo {
    public String name;        // Display name e.g. "Camera"
    public String description; // Plain-language explanation
    public int riskLevel;      // 0 = safe, 1 = medium, 2 = high
    public String settingsKey; // Used to build the deep link intent

    public PermissionInfo(String name, String description,
                          int riskLevel, String settingsKey) {
        this.name = name;
        this.description = description;
        this.riskLevel = riskLevel;
        this.settingsKey = settingsKey;
    }
}
