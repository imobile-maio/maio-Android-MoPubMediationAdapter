package jp.maio.maioandroidmopubadapter;

import android.app.Activity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.logging.MoPubLog;
import com.mopub.mobileads.MaioAdapterConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.mobileads.MoPubView;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MainActivity
        extends AppCompatActivity
        implements MoPubInterstitial.InterstitialAdListener, MoPubRewardedVideoListener {

    private final String AD_UNIT_ID = "SET_YOUR_AD_UNIT_ID";
    private final String AD_UNIT_ID_INTER = "SET_YOUR_AD_UNIT_ID";
    private MoPubInterstitial _moPubInterstitial;

    private SdkInitializationListener initSdkListener() {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
           /* MoPub SDK initialized.
           Check if you should show the consent dialog here, and make your ad requests. */
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configurations required to initialize
        Map<String, String> mediatedNetworkConfiguration = new HashMap<>();
        mediatedNetworkConfiguration.put("MEDIA_ID", "DemoPublisherMediaForAndroid");
        mediatedNetworkConfiguration.put("ZONE_ID", "DemoPublisherZoneForAndroid");


        SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(AD_UNIT_ID)
                .withAdditionalNetwork("com.mopub.mobileads.MaioAdapterConfiguration")
                .withMediatedNetworkConfiguration("com.mopub.mobileads.MaioAdapterConfiguration", mediatedNetworkConfiguration)
                .withLogLevel(MoPubLog.LogLevel.DEBUG)
                .withLegitimateInterestAllowed(false)
                .build();

        MoPub.initializeSdk(this, sdkConfiguration, initSdkListener());

        Button loadButton = findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadRewardedVideo();
            }
        });

        Button showButton = findViewById(R.id.showButton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRewardedVideo();
            }
        });

        Button loadInterstitialButton = findViewById(R.id.loadInterButton);
        loadInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadInterstitial();
            }
        });


        Button showInterButton = findViewById(R.id.showInterButton);
        showInterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

    }

    private void loadRewardedVideo() {
        MoPubRewardedVideos.setRewardedVideoListener(this);
        MoPubRewardedVideos.loadRewardedVideo(AD_UNIT_ID);
    }

    private void showRewardedVideo() {
        MoPubRewardedVideos.showRewardedVideo(AD_UNIT_ID);
    }

    private void loadInterstitial() {
        _moPubInterstitial = new MoPubInterstitial(this, AD_UNIT_ID_INTER);
        _moPubInterstitial.setInterstitialAdListener(this);
        _moPubInterstitial.load();
    }

    private void showInterstitial() {
        _moPubInterstitial.show();
    }

    // Interstitial Ad interface methods

    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        Log.d("DEBUG", "01: onInterstitialLoaded");
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        Log.d("DEBUG", "onInterstitialFailed: " + errorCode.toString());
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        Log.d("DEBUG", "02: onInterstitialShown");

    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        Log.d("DEBUG", "03: onInterstitialClicked");
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        Log.d("DEBUG", "04: onInterstitialDismissed");
    }

    // Rewarded Video ad interface methods

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        Log.d("DEBUG", "01: onRewardedVideoLoadSuccess");
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId,
                                           @NonNull MoPubErrorCode errorCode) {
        Log.d("DEBUG", "02: onRewardedVideoLoadFailure");
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {
        Log.d("DEBUG", "03: onRewardedVideoStarted");
    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId,
                                             @NonNull MoPubErrorCode errorCode) {
        Log.d("DEBUG", "04: onRewardedVideoPlaybackError");

    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {
        Log.d("DEBUG", "05: onRewardedVideoClicked");
    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        Log.d("DEBUG", "06: onRewardedVideoClosed");
    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds,
                                         @NonNull MoPubReward reward) {
        Log.d("DEBUG", "07: onRewardedVideoCompleted");
    }
}
