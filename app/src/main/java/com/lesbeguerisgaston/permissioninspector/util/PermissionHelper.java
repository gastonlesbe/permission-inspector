package com.lesbeguerisgaston.permissioninspector.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.lesbeguerisgaston.permissioninspector.model.AppInfo;
import com.lesbeguerisgaston.permissioninspector.model.PermissionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionHelper {

    // Maps raw Android permission strings → (display name, description, risk level)
    private static final Map<String, String[]> PERMISSION_MAP = new HashMap<>();

    static {
        // Format: { "Display Name", "Plain-language description", "risk: 0/1/2" }
        PERMISSION_MAP.put("android.permission.CAMERA",
            new String[]{"Camera", "Can take photos and record video at any time while the app is open.", "2"});
        PERMISSION_MAP.put("android.permission.RECORD_AUDIO",
            new String[]{"Microphone", "Can record audio — used for voice messages, calls, or background listening.", "2"});
        PERMISSION_MAP.put("android.permission.ACCESS_FINE_LOCATION",
            new String[]{"Location (precise)", "Knows your exact GPS position. Can track you to within a few meters.", "2"});
        PERMISSION_MAP.put("android.permission.ACCESS_COARSE_LOCATION",
            new String[]{"Location (approximate)", "Knows your general area (city/neighborhood level) via Wi-Fi or cell towers.", "1"});
        PERMISSION_MAP.put("android.permission.ACCESS_BACKGROUND_LOCATION",
            new String[]{"Location (always — background)", "Tracks your location even when the app is not open or in use.", "2"});
        PERMISSION_MAP.put("android.permission.READ_CONTACTS",
            new String[]{"Contacts (read)", "Can read your full contact list including names, phones, and emails.", "1"});
        PERMISSION_MAP.put("android.permission.WRITE_CONTACTS",
            new String[]{"Contacts (write)", "Can add, edit, or delete entries in your contact list.", "1"});
        PERMISSION_MAP.put("android.permission.READ_CALL_LOG",
            new String[]{"Call log (read)", "Can see your full call history — who you called and when.", "2"});
        PERMISSION_MAP.put("android.permission.WRITE_CALL_LOG",
            new String[]{"Call log (write)", "Can add or modify entries in your call history.", "2"});
        PERMISSION_MAP.put("android.permission.CALL_PHONE",
            new String[]{"Make phone calls", "Can dial phone numbers and make calls without asking you first.", "2"});
        PERMISSION_MAP.put("android.permission.READ_SMS",
            new String[]{"Read SMS", "Can read all your text messages.", "2"});
        PERMISSION_MAP.put("android.permission.SEND_SMS",
            new String[]{"Send SMS", "Can send text messages from your number — potentially costing you money.", "2"});
        PERMISSION_MAP.put("android.permission.RECEIVE_SMS",
            new String[]{"Receive SMS", "Can intercept incoming text messages before you see them.", "2"});
        PERMISSION_MAP.put("android.permission.READ_EXTERNAL_STORAGE",
            new String[]{"Storage (read)", "Can read all files, photos, and documents on your device.", "1"});
        PERMISSION_MAP.put("android.permission.WRITE_EXTERNAL_STORAGE",
            new String[]{"Storage (write)", "Can create, edit, or delete files on your device storage.", "1"});
        PERMISSION_MAP.put("android.permission.READ_MEDIA_IMAGES",
            new String[]{"Photos", "Can access all photos stored on your device.", "1"});
        PERMISSION_MAP.put("android.permission.READ_MEDIA_VIDEO",
            new String[]{"Videos", "Can access all video files stored on your device.", "1"});
        PERMISSION_MAP.put("android.permission.READ_MEDIA_AUDIO",
            new String[]{"Audio files", "Can access all music and audio files on your device.", "0"});
        PERMISSION_MAP.put("android.permission.BODY_SENSORS",
            new String[]{"Body sensors", "Can read data from health sensors like heart rate monitors.", "1"});
        PERMISSION_MAP.put("android.permission.ACTIVITY_RECOGNITION",
            new String[]{"Physical activity", "Can detect when you are walking, running, cycling, or in a vehicle.", "1"});
        PERMISSION_MAP.put("android.permission.USE_BIOMETRIC",
            new String[]{"Biometrics", "Can request fingerprint or face authentication.", "1"});
        PERMISSION_MAP.put("android.permission.BLUETOOTH",
            new String[]{"Bluetooth", "Can connect to and communicate with nearby Bluetooth devices.", "1"});
        PERMISSION_MAP.put("android.permission.BLUETOOTH_SCAN",
            new String[]{"Bluetooth scan", "Can scan for nearby Bluetooth devices — can be used to infer location.", "1"});
        PERMISSION_MAP.put("android.permission.BLUETOOTH_CONNECT",
            new String[]{"Bluetooth connect", "Can connect to paired Bluetooth devices.", "0"});
        PERMISSION_MAP.put("android.permission.INTERNET",
            new String[]{"Internet", "Can send and receive data over the internet.", "0"});
        PERMISSION_MAP.put("android.permission.ACCESS_WIFI_STATE",
            new String[]{"Wi-Fi state", "Can see which Wi-Fi networks are nearby — can be used to infer location.", "0"});
        PERMISSION_MAP.put("android.permission.POST_NOTIFICATIONS",
            new String[]{"Notifications", "Can show notifications on your screen and notification bar.", "0"});
        PERMISSION_MAP.put("android.permission.VIBRATE",
            new String[]{"Vibrate", "Can make your phone vibrate — typically for notifications.", "0"});
        PERMISSION_MAP.put("android.permission.RECEIVE_BOOT_COMPLETED",
            new String[]{"Start on boot", "Can launch itself automatically every time you restart your phone.", "1"});
        PERMISSION_MAP.put("android.permission.FOREGROUND_SERVICE",
            new String[]{"Foreground service", "Can run persistently in the background with a visible notification.", "1"});
        PERMISSION_MAP.put("android.permission.USE_FINGERPRINT",
            new String[]{"Fingerprint", "Can request fingerprint authentication.", "1"});
        PERMISSION_MAP.put("android.permission.FLASHLIGHT",
            new String[]{"Flashlight", "Can turn the camera flash LED on and off.", "0"});
        PERMISSION_MAP.put("android.permission.NFC",
            new String[]{"NFC", "Can read and write data via Near Field Communication (tap-to-pay, etc.).", "1"});
        PERMISSION_MAP.put("android.permission.READ_PHONE_STATE",
            new String[]{"Phone state", "Can read your phone number, network info, and unique device identifiers.", "2"});
        PERMISSION_MAP.put("android.permission.PROCESS_OUTGOING_CALLS",
            new String[]{"Outgoing calls", "Can see the number you are dialing before the call connects.", "2"});
        PERMISSION_MAP.put("android.permission.SCHEDULE_EXACT_ALARM",
            new String[]{"Exact alarms", "Can schedule tasks to run at precise times, even in the background.", "0"});
        PERMISSION_MAP.put("android.permission.USE_EXACT_ALARM",
            new String[]{"Exact alarms", "Can set exact alarms — used for reminders and scheduled tasks.", "0"});
        PERMISSION_MAP.put("android.permission.MANAGE_EXTERNAL_STORAGE",
            new String[]{"Full storage access", "Can access ALL files on your device, including system folders.", "2"});
        PERMISSION_MAP.put("android.permission.SYSTEM_ALERT_WINDOW",
            new String[]{"Draw over other apps", "Can show content on top of any other app on your screen.", "2"});
        PERMISSION_MAP.put("android.permission.GET_ACCOUNTS",
            new String[]{"Account list", "Can see all Google and other accounts linked to your device.", "1"});
        PERMISSION_MAP.put("android.permission.WRITE_CALENDAR",
            new String[]{"Calendar (write)", "Can create, edit, or delete calendar events.", "1"});
        PERMISSION_MAP.put("android.permission.READ_CALENDAR",
            new String[]{"Calendar (read)", "Can read all your calendar events and appointments.", "1"});
    }

    public static List<AppInfo> getInstalledApps(Context context) {
        PackageManager pm = context.getPackageManager();
        List<AppInfo> result = new ArrayList<>();

        List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

        for (PackageInfo pkg : packages) {
            // Skip system apps
            if ((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) continue;

            String[] declaredPerms = pkg.requestedPermissions;
            if (declaredPerms == null || declaredPerms.length == 0) continue;

            List<PermissionInfo> permList = new ArrayList<>();
            for (String perm : declaredPerms) {
                if (PERMISSION_MAP.containsKey(perm)) {
                    String[] data = PERMISSION_MAP.get(perm);
                    permList.add(new PermissionInfo(
                        data[0],
                        data[1],
                        Integer.parseInt(data[2]),
                        perm
                    ));
                }
            }

            if (permList.isEmpty()) continue;

            String label = pkg.applicationInfo.loadLabel(pm).toString();
            android.graphics.drawable.Drawable icon = pkg.applicationInfo.loadIcon(pm);

            result.add(new AppInfo(label, pkg.packageName, icon, permList));
        }

        // Sort: highest risk first, then alphabetical
        result.sort((a, b) -> {
            if (b.riskLevel != a.riskLevel) return b.riskLevel - a.riskLevel;
            return a.appName.compareToIgnoreCase(b.appName);
        });

        return result;
    }
}
