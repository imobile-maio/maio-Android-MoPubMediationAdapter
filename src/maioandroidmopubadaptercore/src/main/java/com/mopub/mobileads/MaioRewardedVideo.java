package com.mopub.mobileads;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mopub.common.LifecycleListener;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.privacy.PersonalInfoManager;

import java.util.Map;

import jp.maio.sdk.android.FailNotificationReason;
import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;
import jp.maio.sdk.android.MaioAdsListenerInterface;

import static com.mopub.mobileads.MaioUtils.getMoPubErrorCode;

@SuppressWarnings({"PointlessBooleanExpression", "unused"})
public class MaioRewardedVideo extends CustomEventRewardedVideo {

    private MaioCredentials _credentials;
    private final static Object _adRequestLockObject = new Object();
    private static boolean _isAdRequested = false;

    @Nullable
    @Override
    protected LifecycleListener getLifecycleListener() {
        return null;
    }

    @Override
    protected boolean checkAndInitializeSdk(@NonNull Activity launcherActivity,
                                            @NonNull Map<String, Object> localExtras,
                                            @NonNull Map<String, String> serverExtras)
            throws Exception {
        MaioUtils.trace();


        // If GDPR is required do not initialize SDK
        PersonalInfoManager personalInfoManager = MoPub.getPersonalInformationManager();

        if (personalInfoManager != null && personalInfoManager.gdprApplies() == Boolean.TRUE) {
            MoPubRewardedVideoManager.onRewardedVideoLoadFailure(MaioRewardedVideo.class, "maio",
                    getMoPubErrorCode(FailNotificationReason.AD_STOCK_OUT));
            return false;
        }

        if (serverExtras.size() == 0) {
            return false;
        }

        try {
            _credentials = MaioCredentials.Create(serverExtras);
        } catch (IllegalArgumentException e) {
            return false;
        }

        MaioAdsListenerInterface listener = new MaioAdsListener() {

            @Override
            public void onChangedCanShow(String zoneId, boolean canShow) {
                MaioUtils.trace();

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

                if (canShow == false) {
                    MoPubRewardedVideoManager.onRewardedVideoLoadFailure(MaioRewardedVideo.class,
                            _credentials.getZoneId(),
                            getMoPubErrorCode(FailNotificationReason.AD_STOCK_OUT));
                } else {
                    MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(MaioRewardedVideo.class,
                            _credentials.getZoneId());
                }
            }

            @Override
            public void onClickedAd(String zoneId) {
                MaioUtils.trace();

                if (isTargetZone(zoneId) == false) {
                    return;
                }
                MoPubRewardedVideoManager.onRewardedVideoClicked(MaioRewardedVideo.class,
                        _credentials.getZoneId());
            }

            @Override
            public void onClosedAd(String zoneId) {
                MaioUtils.trace();

                if (isTargetZone(zoneId) == false) return;

                MoPubRewardedVideoManager.onRewardedVideoClosed(MaioRewardedVideo.class,
                        _credentials.getZoneId());
                // Invalidate could not be trusted
                MaioAdManager.getInstance().removeListener(this);
            }

            @Override
            public void onFinishedAd(int playtime,
                                     boolean skipped,
                                     int duration,
                                     String zoneId) {
                MaioUtils.trace();

                if (isTargetZone(zoneId) == false) return;

                MoPubReward reward = skipped
                        ? MoPubReward.failure()
                        : MoPubReward.success("", 0);
                MoPubRewardedVideoManager.onRewardedVideoCompleted(MaioRewardedVideo.class,
                        _credentials.getZoneId(),
                        reward);
            }

            @Override
            public void onFailed(FailNotificationReason failNotificationReason, String zoneId) {
                MaioUtils.trace();

                if (isTargetZone(zoneId) == false) return;

                MoPubErrorCode errorCode = getMoPubErrorCode(failNotificationReason);
                MoPubRewardedVideoManager.onRewardedVideoLoadFailure(MaioRewardedVideo.class,
                        _credentials.getZoneId(),
                        errorCode);
            }

            @Override
            public void onStartedAd(String zoneId) {
                MaioUtils.trace();

                if (isTargetZone(zoneId) == false) return;

                MoPubRewardedVideoManager.onRewardedVideoStarted(MaioRewardedVideo.class,
                        _credentials.getZoneId());
            }
        };
        MaioAdManager.getInstance().init(launcherActivity, _credentials.getMediaId(), listener);

        return true;
    }

    private boolean isTargetZone(String receivedZoneId) {
        MaioUtils.trace();

        String zoneId = _credentials.getZoneId();
        return zoneId == null || zoneId.equals(receivedZoneId);
    }

    @Override
    protected void loadWithSdkInitialized(@NonNull Activity activity,
                                          @NonNull Map<String, Object> localExtras,
                                          @NonNull Map<String, String> serverExtras) {
        MaioUtils.trace();

        synchronized (_adRequestLockObject) {
            _isAdRequested = true;
        }

        if (MaioAdManager.getInstance().isInitialized() == false) {
            return;
        }

        if (MaioAds.canShow(_credentials.getZoneId()) == false) {
            return;
        }

        if (MaioAds.canShow(_credentials.getZoneId())) {
            MoPubRewardedVideoManager.onRewardedVideoLoadSuccess(MaioRewardedVideo.class,
                    _credentials.getZoneId());

            synchronized (_adRequestLockObject) {
                _isAdRequested = false;
            }
        }
    }

    @NonNull
    @Override
    protected String getAdNetworkId() {
        MaioUtils.trace();

        return _credentials == null ? "MAIO No Zone" : _credentials.getZoneId();
    }

    @Override
    protected void onInvalidate() {
        MaioUtils.trace();
    }

    @Override
    protected boolean hasVideoAvailable() {
        MaioUtils.trace();

        //noinspection SimplifiableIfStatement
        if (MaioAdManager.getInstance().isInitialized() == false) {
            return false;
        }

        return MaioAds.canShow(_credentials.getZoneId());
    }

    @Override
    protected void showVideo() {
        MaioUtils.trace();

        if (MaioAdManager.getInstance().isInitialized() == false) {
            return;
        }

        MaioAds.show(_credentials.getZoneId());
    }

}
