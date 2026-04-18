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

    // Replace with your real AdMob interstitial unit ID before release
    private static final String ADMOB_INTERSTITIAL_ID = "ca-app-pub-3940256099942544/1033173712";

    // Replace with your real AdMob banner unit ID before release
    private static final String ADMOB_BANNER_ID = "ca-app-pub-3940256099942544/6300978111";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupAdMob();
        setupRecyclerView();
        setupSearch();
        loadApps();
    }

    private void setupBanner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(ADMOB_BANNER_ID);
        android.widget.FrameLayout bannerContainer = findViewById(R.id.bannerContainer);
        bannerContainer.addView(adView);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void setupAdMob() {
        MobileAds.initialize(this, initializationStatus -> {
            setupBanner();
            loadInterstitial();
        });
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

    // Bottom bar button handlers (wired up in activity_main.xml onClick)
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
