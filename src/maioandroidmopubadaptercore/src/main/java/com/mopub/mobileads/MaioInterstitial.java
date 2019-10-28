package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import com.mopub.common.MoPub;
import com.mopub.common.privacy.PersonalInfoManager;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAdsListener;
import jp.maio.sdk.android.MaioAdsListenerInterface;

import static com.mopub.mobileads.MaioUtils.*;

@SuppressWarnings({"PointlessBooleanExpression", "unused"})
public class MaioInterstitial extends CustomEventInterstitial {
    private final static String TAG = "MaioInterstitial";

    private MaioCredentials _credentials;
    private MaioAdsListenerInterface _listener;
    private CustomEventInterstitialListener _mopubListener;

    private boolean _isAdRequested;

    @Override
    protected void loadInterstitial(Context context,
                                    final CustomEventInterstitialListener
                                            customEventInterstitialListener,
                                    Map<String, Object> localExtras,
                                    Map<String, String> serverExtras) {
        trace();

        // If GDPR is required do not initialize SDK
        PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();

        if (personalInfoManager != null && personalInfoManager.gdprApplies() == Boolean.TRUE) {
            customEventInterstitialListener.onInterstitialFailed(
                    MoPubErrorCode.NO_FILL);
            return;
        }

        if (context instanceof Activity == false) {
            customEventInterstitialListener.onInterstitialFailed(MoPubErrorCode.INTERNAL_ERROR);
            return;
        }

        _credentials = MaioCredentials.Create(serverExtras);

        _isAdRequested = true;

        _listener = new MaioAdsListener() {

            @Override
            public void onChangedCanShow(String zoneId, boolean newValue) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (_isAdRequested == false) {
                    return;
                }
                _isAdRequested = false;

                if (customEventInterstitialListener != null) {

                    if (newValue) {
                        customEventInterstitialListener.onInterstitialLoaded();
                    } else {
                        customEventInterstitialListener.onInterstitialFailed(
                                MoPubErrorCode.NO_FILL);
                    }
                }
            }

            @Override
            public void onClosedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialDismissed();
                }

                MaioAdManager.getInstance().removeListener(_listener);
            }

            @Override
            public void onClickedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialClicked();
                }
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }

                if (customEventInterstitialListener != null) {
                    MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                    customEventInterstitialListener.onInterstitialFailed(errorCode);
                }
            }

            @Override
            public void onStartedAd(String zoneId) {
                trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }
                writeDevelopLog(TAG, "event listener is null: "
                        + (customEventInterstitialListener == null));

                if (customEventInterstitialListener != null) {
                    customEventInterstitialListener.onInterstitialShown();
                }
            }
        };
        MaioAdManager.getInstance().init((Activity) context, _credentials.getMediaId(), _listener);

        if (MaioAdManager.getInstance().isInitialized()
                && MaioAdManager.getInstance().canShow(_credentials.getZoneId())) {
            customEventInterstitialListener.onInterstitialLoaded();
            _isAdRequested = false;
        }
    }

    @Override
    protected void showInterstitial() {
        trace();

        if (MaioAdManager.getInstance().isInitialized() == false) {
            return;
        }

        if (MaioAdManager.getInstance().canShow(_credentials.getZoneId()) == false) {
            _mopubListener.onInterstitialFailed(getMoPubErrorCode(FailNotificationReason.VIDEO));
        }

        MaioAdManager.getInstance().show(_credentials.getZoneId());
    }

    @Override
    protected void onInvalidate() {
        trace();

        MaioAdManager.getInstance().removeListener(_listener);
    }

    private boolean isTargetZone(String receivedZoneId) {
        trace();

        if(_credentials == null){
            return true;
        }
        String zoneId = _credentials.getZoneId();
        return zoneId == null || zoneId.equals(receivedZoneId);
    }
}