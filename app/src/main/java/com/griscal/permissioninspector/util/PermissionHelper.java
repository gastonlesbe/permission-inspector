package com.griscal.permissioninspector.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.griscal.permissioninspector.R;
import com.griscal.permissioninspector.model.AppInfo;
import com.griscal.permissioninspector.model.PermissionInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionHelper {

    // Maps raw Android permission strings → (display name res, description res, risk level)
    private static final Map<String, int[]> PERMISSION_MAP = new HashMap<>();

    private static void put(String permission, int nameRes, int descRes, int risk) {
        PERMISSION_MAP.put(permission, new int[]{nameRes, descRes, risk});
    }

    static {
        // Format: permission, display name, plain-language description, risk 0/1/2
        put("android.permission.CAMERA", R.string.perm_camera, R.string.perm_camera_desc, 2);
        put("android.permission.RECORD_AUDIO", R.string.perm_record_audio, R.string.perm_record_audio_desc, 2);
        put("android.permission.ACCESS_FINE_LOCATION", R.string.perm_access_fine_location, R.string.perm_access_fine_location_desc, 2);
        put("android.permission.ACCESS_COARSE_LOCATION", R.string.perm_access_coarse_location, R.string.perm_access_coarse_location_desc, 1);
        put("android.permission.ACCESS_BACKGROUND_LOCATION", R.string.perm_access_background_location, R.string.perm_access_background_location_desc, 2);
        put("android.permission.READ_CONTACTS", R.string.perm_read_contacts, R.string.perm_read_contacts_desc, 1);
        put("android.permission.WRITE_CONTACTS", R.string.perm_write_contacts, R.string.perm_write_contacts_desc, 1);
        put("android.permission.READ_CALL_LOG", R.string.perm_read_call_log, R.string.perm_read_call_log_desc, 2);
        put("android.permission.WRITE_CALL_LOG", R.string.perm_write_call_log, R.string.perm_write_call_log_desc, 2);
        put("android.permission.CALL_PHONE", R.string.perm_call_phone, R.string.perm_call_phone_desc, 2);
        put("android.permission.READ_SMS", R.string.perm_read_sms, R.string.perm_read_sms_desc, 2);
        put("android.permission.SEND_SMS", R.string.perm_send_sms, R.string.perm_send_sms_desc, 2);
        put("android.permission.RECEIVE_SMS", R.string.perm_receive_sms, R.string.perm_receive_sms_desc, 2);
        put("android.permission.READ_EXTERNAL_STORAGE", R.string.perm_read_external_storage, R.string.perm_read_external_storage_desc, 1);
        put("android.permission.WRITE_EXTERNAL_STORAGE", R.string.perm_write_external_storage, R.string.perm_write_external_storage_desc, 1);
        put("android.permission.READ_MEDIA_IMAGES", R.string.perm_read_media_images, R.string.perm_read_media_images_desc, 1);
        put("android.permission.READ_MEDIA_VIDEO", R.string.perm_read_media_video, R.string.perm_read_media_video_desc, 1);
        put("android.permission.READ_MEDIA_AUDIO", R.string.perm_read_media_audio, R.string.perm_read_media_audio_desc, 0);
        put("android.permission.BODY_SENSORS", R.string.perm_body_sensors, R.string.perm_body_sensors_desc, 1);
        put("android.permission.ACTIVITY_RECOGNITION", R.string.perm_activity_recognition, R.string.perm_activity_recognition_desc, 1);
        put("android.permission.USE_BIOMETRIC", R.string.perm_use_biometric, R.string.perm_use_biometric_desc, 1);
        put("android.permission.BLUETOOTH", R.string.perm_bluetooth, R.string.perm_bluetooth_desc, 1);
        put("android.permission.BLUETOOTH_SCAN", R.string.perm_bluetooth_scan, R.string.perm_bluetooth_scan_desc, 1);
        put("android.permission.BLUETOOTH_CONNECT", R.string.perm_bluetooth_connect, R.string.perm_bluetooth_connect_desc, 0);
        put("android.permission.INTERNET", R.string.perm_internet, R.string.perm_internet_desc, 0);
        put("android.permission.ACCESS_WIFI_STATE", R.string.perm_access_wifi_state, R.string.perm_access_wifi_state_desc, 0);
        put("android.permission.POST_NOTIFICATIONS", R.string.perm_post_notifications, R.string.perm_post_notifications_desc, 0);
        put("android.permission.VIBRATE", R.string.perm_vibrate, R.string.perm_vibrate_desc, 0);
        put("android.permission.RECEIVE_BOOT_COMPLETED", R.string.perm_receive_boot_completed, R.string.perm_receive_boot_completed_desc, 1);
        put("android.permission.FOREGROUND_SERVICE", R.string.perm_foreground_service, R.string.perm_foreground_service_desc, 1);
        put("android.permission.USE_FINGERPRINT", R.string.perm_use_fingerprint, R.string.perm_use_fingerprint_desc, 1);
        put("android.permission.FLASHLIGHT", R.string.perm_flashlight, R.string.perm_flashlight_desc, 0);
        put("android.permission.NFC", R.string.perm_nfc, R.string.perm_nfc_desc, 1);
        put("android.permission.READ_PHONE_STATE", R.string.perm_read_phone_state, R.string.perm_read_phone_state_desc, 2);
        put("android.permission.PROCESS_OUTGOING_CALLS", R.string.perm_process_outgoing_calls, R.string.perm_process_outgoing_calls_desc, 2);
        put("android.permission.SCHEDULE_EXACT_ALARM", R.string.perm_schedule_exact_alarm, R.string.perm_schedule_exact_alarm_desc, 0);
        put("android.permission.USE_EXACT_ALARM", R.string.perm_use_exact_alarm, R.string.perm_use_exact_alarm_desc, 0);
        put("android.permission.MANAGE_EXTERNAL_STORAGE", R.string.perm_manage_external_storage, R.string.perm_manage_external_storage_desc, 2);
        put("android.permission.SYSTEM_ALERT_WINDOW", R.string.perm_system_alert_window, R.string.perm_system_alert_window_desc, 2);
        put("android.permission.GET_ACCOUNTS", R.string.perm_get_accounts, R.string.perm_get_accounts_desc, 1);
        put("android.permission.WRITE_CALENDAR", R.string.perm_write_calendar, R.string.perm_write_calendar_desc, 1);
        put("android.permission.READ_CALENDAR", R.string.perm_read_calendar, R.string.perm_read_calendar_desc, 1);
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
                    int[] data = PERMISSION_MAP.get(perm);
                    permList.add(new PermissionInfo(
                        context.getString(data[0]),
                        context.getString(data[1]),
                        data[2],
                        perm
                    ));
                }
            }

            if (permList.isEmpty()) continue;

            String label = pkg.applicationInfo.loadLabel(pm).toString();
            android.graphics.drawable.Drawable icon = pkg.applicationInfo.loadIcon(pm);

            String installSize = context.getString(R.string.size_unknown);
            long installSizeBytes = 0;
            try {
                java.io.File apk = new java.io.File(pkg.applicationInfo.sourceDir);
                long bytes = apk.length();
                installSizeBytes = bytes;
                if (bytes > 1024 * 1024) {
                    installSize = context.getString(R.string.size_mb, bytes / (1024.0 * 1024.0));
                } else {
                    installSize = context.getString(R.string.size_kb, bytes / 1024.0);
                }
            } catch (Exception ignored) {}

            String installDate = formatDate(pkg.firstInstallTime);
            String lastUpdated = formatRelativeTime(context, pkg.lastUpdateTime);
            String versionName = pkg.versionName != null ? pkg.versionName : context.getString(R.string.version_unknown);
            String category = getCategoryLabel(context, pkg.applicationInfo.category);

            result.add(new AppInfo(label, pkg.packageName, icon, permList,
                installSize, installSizeBytes, installDate, lastUpdated, versionName, category));
        }

        result.sort((a, b) -> {
            if (b.riskLevel != a.riskLevel) return b.riskLevel - a.riskLevel;
            return a.appName.compareToIgnoreCase(b.appName);
        });

        return result;
    }

    private static String formatDate(long millis) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(millis));
    }

    private static String formatRelativeTime(Context context, long millis) {
        long diff = System.currentTimeMillis() - millis;
        long days  = diff / (1000 * 60 * 60 * 24);
        if (days == 0)  return context.getString(R.string.time_today);
        if (days == 1)  return context.getString(R.string.time_yesterday);
        if (days < 30)  return context.getString(R.string.time_days_ago, days);
        if (days < 365) return context.getString(R.string.time_months_ago, days / 30);
        return context.getString(R.string.time_years_ago, days / 365);
    }

    private static String getCategoryLabel(Context context, int category) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return context.getString(R.string.category_app);
        switch (category) {
            case android.content.pm.ApplicationInfo.CATEGORY_GAME:          return context.getString(R.string.category_game);
            case android.content.pm.ApplicationInfo.CATEGORY_AUDIO:         return context.getString(R.string.category_music);
            case android.content.pm.ApplicationInfo.CATEGORY_VIDEO:         return context.getString(R.string.category_video);
            case android.content.pm.ApplicationInfo.CATEGORY_IMAGE:         return context.getString(R.string.category_photos);
            case android.content.pm.ApplicationInfo.CATEGORY_SOCIAL:        return context.getString(R.string.category_social);
            case android.content.pm.ApplicationInfo.CATEGORY_NEWS:          return context.getString(R.string.category_news);
            case android.content.pm.ApplicationInfo.CATEGORY_MAPS:          return context.getString(R.string.category_navigation);
            case android.content.pm.ApplicationInfo.CATEGORY_PRODUCTIVITY:  return context.getString(R.string.category_productivity);
            default:                                                          return context.getString(R.string.category_app);
        }
    }
}
