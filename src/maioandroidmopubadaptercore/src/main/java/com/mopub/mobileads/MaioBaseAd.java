package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.privacy.PersonalInfoManager;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAdsListener;
import jp.maio.sdk.android.MaioAdsListenerInterface;

import static com.mopub.mobileads.MaioUtils.getMoPubErrorCode;
import static com.mopub.mobileads.MaioUtils.trace;

public class MaioBaseAd extends BaseAd {

    private final static String TAG = "MaioBaseAd";
    private MaioCredentials _credentials;
    private MaioAdsListenerInterface _listener;
    private boolean _isAdRequested;
    private final static Object _adRequestLockObject = new Object();

    @Override
    protected void load(@NonNull final Context context, @NonNull final AdData adData) {
        trace();

        if (MaioAdManager.getInstance().isInitialized() == false) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            }
            return;
        }

        if (MaioAdManager.getInstance().canShow(_credentials.getZoneId()) == false) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
            }
            return;
        }

        if (MaioAdManager.getInstance().isInitialized()
                && MaioAdManager.getInstance().canShow(_credentials.getZoneId())) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoaded();
            }
            _isAdRequested = false;
        }
    }
        private void maioInit() {

        _isAdRequested = true;

        _listener = new MaioAdsListener() {

            @Override
            public void onChangedCanShow(String zoneId, boolean newValue) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                synchronized (_adRequestLockObject) {
                    Log.d("[MAIO]", "isAdRequested: " + _isAdRequested);
                    if (_isAdRequested == false) {
                        return;
                    }
                    _isAdRequested = false;
                }

                if (mLoadListener != null) {

                    if (newValue) {
                        mLoadListener.onAdLoaded();
                    } else {
                        mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
                    }
                }
            }

            @Override
            public void onClosedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (mInteractionListener != null) {
                    mInteractionListener.onAdDismissed();
                }

                MaioAdManager.getInstance().removeListener(_listener);
            }

            @Override
            public void onClickedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (mInteractionListener != null) {
                    mInteractionListener.onAdClicked();
                }
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                if (mInteractionListener != null) {
                    mInteractionListener.onAdFailed(errorCode);
                }
            }

            @Override
            public void onStartedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (mInteractionListener != null) {
                    mInteractionListener.onAdShown();
                }
            }
        };
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull final Activity launcherActivity, @NonNull final AdData adData)
            throws Exception {
        MaioUtils.trace();
        if (validate(adData)) return false;

        maioInit();

        MaioAdManager.getInstance().init(launcherActivity, _credentials.getMediaId(), _listener);

        return true;
    }

    private boolean validate(@NonNull AdData adData) {
        // If GDPR is required do not initialize SDK
        PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();

        if (personalInfoManager != null && personalInfoManager.gdprApplies() == Boolean.TRUE) {
            if (mLoadListener != null) {
                mLoadListener.onAdLoadFailed(MoPubErrorCode.NO_FILL);
            }
            return true;
        }

        final Map<String, String> serverExtras = adData.getExtras();
        if (serverExtras.size() == 0) {
            return true;
        }

        try {
            _credentials = MaioCredentials.Create(serverExtras);
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }

    @Override
    protected void show() {
        trace();

        if (MaioAdManager.getInstance().isInitialized() == false) {
            return;
        }

        if (MaioAdManager.getInstance().canShow(_credentials.getZoneId()) == false) {
            MoPubErrorCode errorCode = getMoPubErrorCode(FailNotificationReason.VIDEO);
            if (mInteractionListener != null) {
                mInteractionListener.onAdFailed(errorCode);
            }
        }

        MaioAdManager.getInstance().show(_credentials.getZoneId());
    }

    @Override
    protected void onInvalidate() {
        trace();

        MaioAdManager.getInstance().removeListener(_listener);
    }

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    private boolean isTargetZone(String receivedZoneId) {
        trace();

        if(_credentials == null){
            return true;
        }
        String zoneId = _credentials.getZoneId();
        return zoneId == null || zoneId.equals(receivedZoneId);
    }

    @NonNull
    public String getAdNetworkId() {
        if(_credentials == null){
            return "";
        }
        return  _credentials.getZoneId() == null ? "" : _credentials.getZoneId();
    }
}
