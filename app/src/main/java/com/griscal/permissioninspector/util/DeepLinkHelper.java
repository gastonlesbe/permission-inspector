package com.griscal.permissioninspector.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class DeepLinkHelper {

    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    // Android 12+ has a hardware kill switch for the microphone on the Privacy page.
    // On older Android there is no OS-level toggle — show a message instead.
    public static void openMicrophoneSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Toast.makeText(context, "Buscá \"Acceso al micrófono\" y desactivalo.", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            Toast.makeText(context,
                "El control global del micrófono requiere Android 12 o superior.",
                Toast.LENGTH_LONG).show();
        }
    }

    // Turns off location (GPS / network) for the whole device.
    public static void openLocationSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
