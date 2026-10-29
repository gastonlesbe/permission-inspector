package com.griscal.permissioninspector;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.griscal.permissioninspector.BuildConfig;
import com.griscal.permissioninspector.adapter.AppListAdapter;
import com.griscal.permissioninspector.adapter.AppListAdapter.SortOrder;
import com.griscal.permissioninspector.model.AppInfo;
import com.griscal.permissioninspector.util.DeepLinkHelper;
import com.griscal.permissioninspector.util.InsetsHelper;
import com.griscal.permissioninspector.util.PermissionHelper;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppListAdapter adapter;
    private InterstitialAd interstitialAd;
    private boolean returnedFromDetail = false;

    private static final String APPODEAL_KEY = "e36b36a575dc5421b5564e46cda12ef1426477c4319d8c7b";
    // AdMob interstitial ID: res/values/ads.xml (production), overridden with Google's
    // sample ID in src/debug/res/values/ads.xml for debug builds.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InsetsHelper.applyTopInset(toolbar);
        InsetsHelper.applyBottomInset(findViewById(R.id.bottomPanel));

        setupAppodealBanner();
        setupAdMobInterstitial();
        setupRecyclerView();
        setupSearch();
        setupSort();
        loadApps();
    }

    private void setupAppodealBanner() {
        if (BuildConfig.DEBUG) return;
        Appodeal.setTesting(false);
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {
                runOnUiThread(() -> Appodeal.show(MainActivity.this, Appodeal.BANNER));
            }
            @Override public void onBannerFailedToLoad() {}
            @Override public void onBannerShown() {}
            @Override public void onBannerShowFailed() {}
            @Override public void onBannerClicked() {}
            @Override public void onBannerExpired() {}
        });
        Appodeal.initialize(this, APPODEAL_KEY, Appodeal.BANNER);
        Appodeal.cache(this, Appodeal.BANNER);
    }

    private void setupAdMobInterstitial() {
        MobileAds.initialize(this, status -> loadInterstitial());
    }

    private void loadInterstitial() {
        InterstitialAd.load(this, getString(R.string.admob_interstitial_id), new AdRequest.Builder().build(),
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

    private void setupSort() {
        ChipGroup chipGroup = findViewById(R.id.sortChipGroup);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipPermissions) adapter.sort(SortOrder.PERMISSIONS);
            else if (id == R.id.chipName)    adapter.sort(SortOrder.NAME);
            else if (id == R.id.chipSize)    adapter.sort(SortOrder.SIZE);
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

    public void onLocationTap(android.view.View v) {
        DeepLinkHelper.openLocationSettings(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem versionItem = menu.findItem(R.id.version);
        if (versionItem != null) {
            try {
                versionItem.setTitle(getString(R.string.version_format, getPackageManager().getPackageInfo(getPackageName(), 0).versionName));
            } catch (PackageManager.NameNotFoundException e) {
                versionItem.setTitle(R.string.version_fallback);
            }
            versionItem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem themeItem = menu.findItem(R.id.theme_toggle);
        if (themeItem != null) {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isDark = prefs.getBoolean("dark_mode", false);
            themeItem.setTitle(isDark ? getString(R.string.menu_theme_light) : getString(R.string.menu_theme_dark));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.theme_toggle) {
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            boolean isDark = prefs.getBoolean("dark_mode", false);
            prefs.edit().putBoolean("dark_mode", !isDark).apply();
            AppCompatDelegate.setDefaultNightMode(
                    !isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
            return true;
        } else if (id == R.id.contact) {
            sendEmail();
            return true;
        } else if (id == R.id.app_carepal_pets) {
            openPlayStore("com.gastonlesbegueris.caretemplate.pets");
            return true;
        } else if (id == R.id.app_carepal_cars) {
            openPlayStore("com.gastonlesbegueris.caretemplate.cars");
            return true;
        } else if (id == R.id.app_carepal_family) {
            openPlayStore("com.gastonlesbegueris.caretemplate.family");
            return true;
        } else if (id == R.id.app_carepal_house) {
            openPlayStore("com.gastonlesbegueris.caretemplate.house");
            return true;
        } else if (id == R.id.app_griscal) {
            openPlayStore("app.griscal.notif");
            return true;
        } else if (id == R.id.app_gpsalert) {
            openPlayStore("com.gaston.lesbegueris.notepases");
            return true;
        } else if (id == R.id.app_milupa) {
            openPlayStore("lesbegueris.gaston.com.milupa");
            return true;
        } else if (id == R.id.app_milinterna) {
            openPlayStore("lesbegueris.gaston.com.milinterna");
            return true;
        } else if (id == R.id.rate) {
            rateMe();
            return true;
        } else if (id == R.id.share) {
            shareApp();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPlayStore(String packageName) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
        }
    }

    private void shareApp() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.griscal.permissioninspector");
        startActivity(Intent.createChooser(intent, getString(R.string.shareapp)));
    }

    private void rateMe() {
        openPlayStore("com.griscal.permissioninspector");
    }

    private void sendEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lesberweb@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        startActivity(Intent.createChooser(intent, ""));
    }
}
