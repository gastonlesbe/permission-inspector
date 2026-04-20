# Permission Inspector — Android App Project Brief

## Overview

A free Android utility app that reads all installed apps and their declared permissions, displays them in a clean list with risk ratings, and provides deep links directly into Android system settings so the user can manage permissions without hunting through menus.

**Tech stack:** Java · Gradle · AdMob (banner + interstitial)
**IDE:** Android Studio (development + emulator)

---

## Project Setup Instructions

### 1. Prerequisites

Make sure you have the following installed:

- **Android Studio** (Hedgehog or newer) — used for both coding and running the emulator
- **JDK 17** — confirm with `java -version`
- **Android SDK** — installed via Android Studio's SDK Manager (API 33+)
- Add this to your shell profile (`~/.bashrc` or `~/.zshrc`):

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

### 2. Open the project

The project is ready to open directly in Android Studio:

**File → Open** → select the `PermissionInspector` folder.

Android Studio will detect the Gradle project automatically and sync dependencies.

### 3. Add local.properties

Create `local.properties` at the project root if it doesn't exist (not committed to git):

```
sdk.dir=/home/YOUR_USER/Android/Sdk
```

Replace `YOUR_USER` with your actual Linux username.

### 4. Gradle wrapper

The `gradlew` wrapper is already included in the project. Make it executable if needed:

```bash
chmod +x gradlew
```

### 5. Build and run on emulator

- Open **Android Studio** → **Device Manager** → start your AVD emulator (API 33+)
- Run via Android Studio's Run button, or from terminal:

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

- **Project details:**
  - Name: `PermissionInspector`
  - Package name: `com.lesbeguerisgaston.permissioninspector`
  - Language: **Java**
  - Minimum SDK: **API 26 (Android 8.0)**
  - Build configuration: **Groovy DSL (build.gradle)**

---

## Folder Structure

```
PermissionInspector/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── local.properties
├── gradlew
├── gradle/wrapper/
│   ├── gradle-wrapper.jar
│   └── gradle-wrapper.properties
└── app/
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/lesbeguerisgaston/permissioninspector/
            │       ├── MainActivity.java
            │       ├── AppDetailActivity.java
            │       ├── model/
            │       │   ├── AppInfo.java
            │       │   └── PermissionInfo.java
            │       ├── adapter/
            │       │   ├── AppListAdapter.java
            │       │   └── PermissionAdapter.java
            │       └── util/
            │           ├── PermissionHelper.java
            │           └── DeepLinkHelper.java
            └── res/
                ├── layout/
                │   ├── activity_main.xml
                │   ├── activity_app_detail.xml
                │   ├── item_app.xml
                │   └── item_permission.xml
                ├── values/
                │   ├── strings.xml
                │   ├── colors.xml
                │   └── themes.xml
                ├── drawable/
                │   └── bg_quick_btn.xml
                ├── mipmap-hdpi/
                ├── mipmap-mdpi/
                ├── mipmap-xhdpi/
                ├── mipmap-xxhdpi/
                └── mipmap-xxxhdpi/
```

---

## Gradle Files

### `build.gradle` (project level)

```groovy
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.2'
    }
}
```

> **Note:** Do NOT add an `allprojects { repositories {} }` block here — repositories are managed in `settings.gradle` via `dependencyResolutionManagement`.

### `build.gradle` (app level)

```groovy
plugins {
    id 'com.android.application'
}

android {
    namespace 'com.lesbeguerisgaston.permissioninspector'
    compileSdk 34

    defaultConfig {
        applicationId "com.lesbeguerisgaston.permissioninspector"
        minSdk 26
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

    // AdMob (banner + interstitial)
    implementation 'com.google.android.gms:play-services-ads:23.0.0'

    // Appodeal banner — uncomment and add repo in settings.gradle when you have your account key
    // implementation 'com.appodeal.ads:sdk:3.3.1'
}
```

### `settings.gradle`

```groovy
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Uncomment when adding Appodeal back:
        // maven { url 'https://appodeal.jfrog.io/artifactory/appodeal' }
    }
}

rootProject.name = "PermissionInspector"
include ':app'
```

### `gradle.properties`

```properties
android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

> **Important:** This file is required. Without `android.useAndroidX=true` the build will warn about AndroidX conflicts and may crash at runtime.

---

## AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Required to query installed apps on Android 11+ -->
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES"
        tools:ignore="QueryAllPackagesPermission" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.PermissionInspector">

        <!-- AdMob App ID — replace with your real ID before release -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-3940256099942544~3347511713" />

        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".AppDetailActivity"
            android:exported="false"
            android:parentActivityName=".MainActivity" />

    </application>
</manifest>
```

> **Important:** `QUERY_ALL_PACKAGES` is required on Android 11+ to see all installed apps. Google Play will ask you to justify its use during app review — your justification is that this is a privacy inspector tool whose core function depends on reading installed packages.

---

## Java Source Files

### `model/AppInfo.java`

```java
package com.lesbeguerisgaston.permissioninspector.model;

import java.util.List;

public class AppInfo {
    public String appName;
    public String packageName;
    public android.graphics.drawable.Drawable icon;
    public List<PermissionInfo> permissions;
    public int riskLevel;       // 0 = low, 1 = medium, 2 = high

    // App info fields — all free, no extra permissions needed
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
```

### `model/PermissionInfo.java`

```java
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
```

### `util/PermissionHelper.java`

This is the core class that reads installed apps and maps raw Android permission strings to human-readable descriptions and risk levels.

```java
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

            // Install size from APK file on disk
            String installSize = "Unknown";
            try {
                java.io.File apk = new java.io.File(pkg.applicationInfo.sourceDir);
                long bytes = apk.length();
                if (bytes > 1024 * 1024) {
                    installSize = String.format("%.0f MB", bytes / (1024.0 * 1024.0));
                } else {
                    installSize = String.format("%.0f KB", bytes / 1024.0);
                }
            } catch (Exception ignored) {}

            String installDate = formatDate(pkg.firstInstallTime);
            String lastUpdated = formatRelativeTime(pkg.lastUpdateTime);
            String versionName = pkg.versionName != null ? pkg.versionName : "—";
            String category = getCategoryLabel(pkg.applicationInfo.category);

            result.add(new AppInfo(label, pkg.packageName, icon, permList,
                installSize, installDate, lastUpdated, versionName, category));
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

    private static String formatRelativeTime(long millis) {
        long diff = System.currentTimeMillis() - millis;
        long days  = diff / (1000 * 60 * 60 * 24);
        if (days == 0)  return "today";
        if (days == 1)  return "yesterday";
        if (days < 30)  return days + " days ago";
        if (days < 365) return (days / 30) + " mo ago";
        return (days / 365) + " yr ago";
    }

    private static String getCategoryLabel(int category) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return "App";
        switch (category) {
            case android.content.pm.ApplicationInfo.CATEGORY_GAME:          return "Game";
            case android.content.pm.ApplicationInfo.CATEGORY_AUDIO:         return "Music";
            case android.content.pm.ApplicationInfo.CATEGORY_VIDEO:         return "Video";
            case android.content.pm.ApplicationInfo.CATEGORY_IMAGE:         return "Photos";
            case android.content.pm.ApplicationInfo.CATEGORY_SOCIAL:        return "Social";
            case android.content.pm.ApplicationInfo.CATEGORY_NEWS:          return "News";
            case android.content.pm.ApplicationInfo.CATEGORY_MAPS:          return "Navigation";
            case android.content.pm.ApplicationInfo.CATEGORY_PRODUCTIVITY:  return "Productivity";
            default:                                                          return "App";
        }
    }
}
```

### `util/DeepLinkHelper.java`

```java
package com.lesbeguerisgaston.permissioninspector.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class DeepLinkHelper {

    public static void openAppSettings(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", packageName, null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void openMicrophoneSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public static void openCameraSettings(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startActivity(new Intent(Settings.ACTION_PRIVACY_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            context.startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    public static void openLocationSettings(Context context) {
        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}
```

### `MainActivity.java`

```java
package com.lesbeguerisgaston.permissioninspector;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.lesbeguerisgaston.permissioninspector.adapter.AppListAdapter;
import com.lesbeguerisgaston.permissioninspector.model.AppInfo;
import com.lesbeguerisgaston.permissioninspector.util.DeepLinkHelper;
import com.lesbeguerisgaston.permissioninspector.util.PermissionHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppListAdapter adapter;
    private InterstitialAd interstitialAd;
    private boolean returnedFromDetail = false;

    // Replace with your real AdMob IDs before release
    private static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";
    private static final String ADMOB_BANNER_ID       = "ca-app-pub-3940256099942544/6300978111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAdMob();
        setupRecyclerView();
        setupSearch();
        loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (returnedFromDetail && interstitialAd != null) {
            returnedFromDetail = false;
            interstitialAd.setFullScreenContentCallback(
                new com.google.android.gms.ads.FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        interstitialAd = null;
                        loadInterstitial();
                    }
                });
            interstitialAd.show(this);
        } else {
            returnedFromDetail = false;
        }
    }

    private void setupAdMob() {
        MobileAds.initialize(this, initializationStatus -> {
            setupBanner();
            loadInterstitial();
        });
    }

    private void setupBanner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ADMOB_BANNER_ID);
        android.widget.FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        bannerContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, ADMOB_INTERSTITIAL_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd ad) {
                    interstitialAd = ad;
                }
                @Override
                public void onAdFailedToLoad(LoadAdError error) {
                    interstitialAd = null;
                }
            });
    }

    private void openDetail(AppInfo app) {
        returnedFromDetail = true;
        Intent intent = new Intent(this, AppDetailActivity.class);
        intent.putExtra("package_name", app.packageName);
        startActivity(intent);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AppListAdapter(this, this::openDetail);
        recyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        EditText searchBox = findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadApps() {
        new AsyncTask<Void, Void, List<AppInfo>>() {
            @Override
            protected List<AppInfo> doInBackground(Void... voids) {
                return PermissionHelper.getInstalledApps(MainActivity.this);
            }
            @Override
            protected void onPostExecute(List<AppInfo> apps) {
                adapter.setData(apps);
            }
        }.execute();
    }

    public void onMicTap(android.view.View v) {
        DeepLinkHelper.openMicrophoneSettings(this);
    }

    public void onCameraTap(android.view.View v) {
        DeepLinkHelper.openCameraSettings(this);
    }

    public void onLocationTap(android.view.View v) {
        DeepLinkHelper.openLocationSettings(this);
    }
}
```

> **Ad trigger note:** The interstitial is shown when the user presses back from the detail screen and returns to the main list. This is a natural transition point per AdMob policy. AdMob enforces a minimum 5-minute cooldown between interstitials automatically.

### `AppDetailActivity.java`

```java
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

        List<AppInfo> allApps = PermissionHelper.getInstalledApps(this);
        AppInfo app = null;
        for (AppInfo a : allApps) {
            if (a.packageName.equals(packageName)) { app = a; break; }
        }
        if (app == null) { finish(); return; }

        ((ImageView) findViewById(R.id.appIcon)).setImageDrawable(app.icon);
        ((TextView) findViewById(R.id.appName)).setText(app.appName);
        ((TextView) findViewById(R.id.permCount)).setText(
            app.permissions.size() + " permissions granted");

        // Bind info card
        ((TextView) findViewById(R.id.valSize)).setText(app.installSize);
        ((TextView) findViewById(R.id.valCategory)).setText(app.category);
        ((TextView) findViewById(R.id.valInstalled)).setText(app.installDate);
        ((TextView) findViewById(R.id.valUpdated)).setText(app.lastUpdated);
        ((TextView) findViewById(R.id.valVersion)).setText("v" + app.versionName);
        ((TextView) findViewById(R.id.valPermCount)).setText(String.valueOf(app.permissions.size()));

        RecyclerView rv = findViewById(R.id.permissionsRecycler);
        rv.setLayoutManager(new LinearLayoutManager(this));

        final AppInfo finalApp = app;
        rv.setAdapter(new PermissionAdapter(app.permissions,
            perm -> DeepLinkHelper.openAppSettings(this, finalApp.packageName)));

        MaterialButton settingsBtn = findViewById(R.id.openSettingsBtn);
        settingsBtn.setOnClickListener(v ->
            DeepLinkHelper.openAppSettings(this, packageName));

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
```

---

## Layout Files

### `res/layout/activity_main.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- Header -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp"
        android:background="?attr/colorSurface">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Permission inspector"
            android:textSize="20sp"
            android:textStyle="bold"
            android:textColor="?attr/colorOnSurface" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Tap any app to see its permissions"
            android:textSize="13sp"
            android:textColor="?attr/colorOnSurfaceVariant" />
    </LinearLayout>

    <!-- Search -->
    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginHorizontal="12dp"
        android:layout_marginTop="8dp"
        app:boxBackgroundMode="outline"
        style="@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/searchBox"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Search apps..."
            android:inputType="text"
            android:imeOptions="actionSearch" />
    </com.google.android.material.textfield.TextInputLayout>

    <!-- AdMob banner -->
    <FrameLayout
        android:id="@+id/bannerContainer"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:layout_marginHorizontal="12dp" />

    <!-- App list -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingBottom="4dp" />

    <!-- Device-wide bottom bar -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="12dp"
        android:background="?attr/colorSurface"
        android:elevation="4dp">

        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="DEVICE-WIDE SETTINGS"
            android:textSize="10sp"
            android:gravity="center"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:layout_marginBottom="8dp" />

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:weightSum="3">

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:orientation="vertical"
                android:gravity="center"
                android:padding="10dp"
                android:background="@drawable/bg_quick_btn"
                android:layout_marginEnd="4dp"
                android:onClick="onMicTap"
                android:clickable="true"
                android:focusable="true">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="🎤" android:textSize="22sp" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="Microphone" android:textSize="11sp"
                    android:textColor="?attr/colorOnSurface" android:textStyle="bold" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="All apps →" android:textSize="10sp"
                    android:textColor="?attr/colorOnSurfaceVariant" />
            </LinearLayout>

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:orientation="vertical"
                android:gravity="center"
                android:padding="10dp"
                android:background="@drawable/bg_quick_btn"
                android:layout_marginHorizontal="4dp"
                android:onClick="onCameraTap"
                android:clickable="true"
                android:focusable="true">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="📷" android:textSize="22sp" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="Camera" android:textSize="11sp"
                    android:textColor="?attr/colorOnSurface" android:textStyle="bold" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="All apps →" android:textSize="10sp"
                    android:textColor="?attr/colorOnSurfaceVariant" />
            </LinearLayout>

            <LinearLayout
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:orientation="vertical"
                android:gravity="center"
                android:padding="10dp"
                android:background="@drawable/bg_quick_btn"
                android:layout_marginStart="4dp"
                android:onClick="onLocationTap"
                android:clickable="true"
                android:focusable="true">
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="📍" android:textSize="22sp" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="Location" android:textSize="11sp"
                    android:textColor="?attr/colorOnSurface" android:textStyle="bold" />
                <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                    android:text="All apps →" android:textSize="10sp"
                    android:textColor="?attr/colorOnSurfaceVariant" />
            </LinearLayout>

        </LinearLayout>
    </LinearLayout>
</LinearLayout>
```

### `res/layout/item_app.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp"
    android:background="?attr/selectableItemBackground"
    android:clickable="true"
    android:focusable="true">

    <!-- Top row: icon · name · badge · chevron -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <ImageView
            android:id="@+id/appIcon"
            android:layout_width="44dp"
            android:layout_height="44dp"
            android:scaleType="fitCenter" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:layout_marginHorizontal="12dp">

            <TextView
                android:id="@+id/appName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="14sp"
                android:textStyle="bold"
                android:textColor="?attr/colorOnSurface" />

            <TextView
                android:id="@+id/appCategory"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="11sp"
                android:textColor="?attr/colorOnSurfaceVariant" />
        </LinearLayout>

        <TextView
            android:id="@+id/riskBadge"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:paddingHorizontal="8dp"
            android:paddingVertical="3dp"
            android:textSize="10sp"
            android:textStyle="bold"
            android:layout_marginEnd="6dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="›"
            android:textSize="18sp"
            android:textColor="?attr/colorOnSurfaceVariant" />
    </LinearLayout>

    <!-- Info strip: size · updated · version -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="5dp"
        android:layout_marginStart="56dp"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/appSize"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="10sp"
            android:textColor="?attr/colorOnSurfaceVariant" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text=" · "
            android:textSize="10sp"
            android:textColor="?attr/colorOnSurfaceVariant" />

        <TextView
            android:id="@+id/appUpdated"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="10sp"
            android:textColor="?attr/colorOnSurfaceVariant" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text=" · "
            android:textSize="10sp"
            android:textColor="?attr/colorOnSurfaceVariant" />

        <TextView
            android:id="@+id/appVersion"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="10sp"
            android:textColor="?attr/colorOnSurfaceVariant" />

    </LinearLayout>

</LinearLayout>
```

### `res/layout/item_permission.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="top">

    <View
        android:id="@+id/riskDot"
        android:layout_width="10dp"
        android:layout_height="10dp"
        android:layout_marginTop="4dp"
        android:layout_marginEnd="12dp" />

    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">

        <TextView
            android:id="@+id/permName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="13sp"
            android:textStyle="bold"
            android:textColor="?attr/colorOnSurface" />

        <TextView
            android:id="@+id/permDesc"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="12sp"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:layout_marginTop="2dp" />

        <TextView
            android:id="@+id/permLink"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Open settings for this permission →"
            android:textSize="11sp"
            android:textColor="#1565C0"
            android:layout_marginTop="4dp"
            android:clickable="true"
            android:focusable="true" />
    </LinearLayout>

</LinearLayout>
```

### `res/layout/activity_app_detail.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <!-- Header row: icon · name · subtitle -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:padding="16dp"
        android:gravity="center_vertical"
        android:background="?attr/colorSurface">

        <ImageView
            android:id="@+id/appIcon"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:scaleType="fitCenter" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:orientation="vertical"
            android:layout_marginStart="14dp">

            <TextView
                android:id="@+id/appName"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="17sp"
                android:textStyle="bold"
                android:textColor="?attr/colorOnSurface" />

            <TextView
                android:id="@+id/permCount"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textSize="12sp"
                android:textColor="?attr/colorOnSurfaceVariant"
                android:layout_marginTop="2dp" />
        </LinearLayout>
    </LinearLayout>

    <!-- Scrollable body -->
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- App info card -->
            <androidx.cardview.widget.CardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_margin="12dp"
                app:cardCornerRadius="10dp"
                app:cardElevation="0dp"
                xmlns:app="http://schemas.android.com/apk/res-auto">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="14dp"
                    android:background="?attr/colorSurfaceVariant">

                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="APP INFO"
                        android:textSize="10sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorOnSurfaceVariant"
                        android:letterSpacing="0.08"
                        android:layout_marginBottom="10dp" />

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:layout_marginBottom="10dp">

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valSize"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Install size" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valCategory"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Category" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:layout_marginBottom="10dp">

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valInstalled"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Installed" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valUpdated"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Last updated" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valVersion"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Version" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>

                        <LinearLayout android:layout_width="0dp" android:layout_height="wrap_content"
                            android:layout_weight="1" android:orientation="vertical">
                            <TextView android:id="@+id/valPermCount"
                                android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:textSize="13sp" android:textStyle="bold"
                                android:textColor="?attr/colorOnSurface" />
                            <TextView android:layout_width="wrap_content" android:layout_height="wrap_content"
                                android:text="Permissions" android:textSize="10sp"
                                android:textColor="?attr/colorOnSurfaceVariant" />
                        </LinearLayout>
                    </LinearLayout>

                </LinearLayout>
            </androidx.cardview.widget.CardView>

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="PERMISSIONS GRANTED"
                android:textSize="10sp"
                android:textStyle="bold"
                android:textColor="?attr/colorOnSurfaceVariant"
                android:letterSpacing="0.08"
                android:layout_marginHorizontal="16dp"
                android:layout_marginBottom="4dp" />

            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/permissionsRecycler"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:nestedScrollingEnabled="false" />

        </LinearLayout>
    </ScrollView>

    <!-- Footer: open settings button -->
    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="12dp"
        android:background="?attr/colorSurface">

        <com.google.android.material.button.MaterialButton
            android:id="@+id/openSettingsBtn"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Open app settings"
            style="@style/Widget.MaterialComponents.Button.OutlinedButton" />
    </LinearLayout>

</LinearLayout>
```

---

## Drawable

### `res/drawable/bg_quick_btn.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <corners android:radius="12dp" />
    <stroke android:width="1dp" android:color="?attr/colorOutline" />
    <solid android:color="?attr/colorSurfaceVariant" />
</shape>
```

---

## Values

### `res/values/strings.xml`

```xml
<resources>
    <string name="app_name">Permission Inspector</string>
    <string name="search_hint">Search apps…</string>
    <string name="open_settings">Open app settings</string>
    <string name="risk_high">High risk</string>
    <string name="risk_medium">Medium</string>
    <string name="risk_low">Low risk</string>
    <string name="device_wide">Device-wide settings</string>
</resources>
```

### `res/values/colors.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="risk_high">#E53935</color>
    <color name="risk_high_bg">#FFEBEE</color>
    <color name="risk_medium">#F57C00</color>
    <color name="risk_medium_bg">#FFF3E0</color>
    <color name="risk_low">#388E3C</color>
    <color name="risk_low_bg">#E8F5E9</color>
</resources>
```

### `res/values/themes.xml`

```xml
<resources>
    <style name="Theme.PermissionInspector" parent="Theme.Material3.DayNight.NoActionBar">
        <item name="colorPrimary">@color/risk_low</item>
    </style>
</resources>
```

---

## Ad IDs Reference

| Ad type | Test ID (use during development) | Replace before release |
|---|---|---|
| AdMob App ID | `ca-app-pub-3940256099942544~3347511713` | Your real AdMob app ID |
| AdMob Interstitial | `ca-app-pub-3940256099942544/1033173712` | Your real interstitial unit ID |
| AdMob Banner | `ca-app-pub-3940256099942544/6300978111` | Your real banner unit ID |

> Always use Google's test IDs during development. Using real IDs on an emulator risks your AdMob account being flagged for invalid traffic.

---

## Running on Emulator

1. Open **Android Studio** → **Device Manager** → create a device with **API 33+** and start it
2. Run via Android Studio's Run button, or from terminal:
```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```
3. The emulator won't show real ads — AdMob test ads will appear instead (this is correct behavior)
4. `QUERY_ALL_PACKAGES` works on the emulator but the app list will be short since few apps are installed there — test on a physical device for the full experience
5. To see logs while the app runs:
```bash
adb logcat -s PermissionInspector
```

---

## Next Steps

- [ ] Create AdMob account and get real unit IDs (interstitial + banner)
- [ ] Add Appodeal banner — create account, get app key, uncomment dependency in `app/build.gradle` and repo in `settings.gradle`
- [x] App icon designed and placed in mipmap folders
- [ ] Add "Remove ads" in-app purchase via Google Play Billing
- [ ] Implement permission description database in a separate JSON file for easier updates
- [ ] Add sorting options (by name / by risk / by permission count)
- [ ] Add filter by permission type (show all apps with Camera access, etc.)
