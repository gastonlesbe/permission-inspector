package com.lesbeguerisgaston.permissioninspector.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class DeepLinkHelper {

    // Opens the specific app's permission settings page
    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Opens device-wide microphone privacy settings (Android 12+)
    public static void openMicrophoneSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // Opens device-wide camera privacy settings (Android 12+)
    public static void openCameraSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    // Opens location settings
    public static void openLocationSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
