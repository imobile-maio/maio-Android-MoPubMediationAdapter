package com.mopub.mobileads;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mopub.common.BaseAdapterConfiguration;
import com.mopub.common.OnNetworkInitializationFinishedListener;
import com.mopub.common.Preconditions;
import com.mopub.common.logging.MoPubLog;

import java.util.Map;

import jp.maio.sdk.android.MaioAds;
import jp.maio.sdk.android.MaioAdsListener;

import static com.mopub.common.logging.MoPubLog.SdkLogEvent.CUSTOM;
import static com.mopub.common.logging.MoPubLog.SdkLogEvent.CUSTOM_WITH_THROWABLE;

public class MaioAdapterConfiguration extends BaseAdapterConfiguration {

    // Adapter's keys
    private static final String ADAPTER_VERSION = "1.1.9.2";
    private static final String ADAPTER_NAME = MaioAdapterConfiguration.class.getSimpleName();
    private static final String MOPUB_NETWORK_NAME = "maio";

    @NonNull
    @Override
    public String getAdapterVersion() {
        return ADAPTER_VERSION;
    }

    @Nullable
    @Override
    public String getBiddingToken(@NonNull Context context) {
        return null;
    }

    @NonNull
    @Override
    public String getMoPubNetworkName() {
        return MOPUB_NETWORK_NAME;
    }

    @NonNull
    @Override
    public String getNetworkSdkVersion() {
        return MaioAds.getSdkVersion();
    }

    @Override
    public void initializeNetwork(@NonNull Context context, @Nullable Map<String, String>
            configuration, @NonNull OnNetworkInitializationFinishedListener listener) {

        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(listener);

        boolean networkInitializationSucceeded = false;

        synchronized (MaioAdapterConfiguration.class) {
            try {
                if (configuration != null && context instanceof Activity) {

                    MaioCredentials credentials = MaioCredentials.Create(configuration);
                    MaioAdManager.getInstance().init((Activity) context, credentials.getMediaId(), new MaioAdsListener());
                    networkInitializationSucceeded = true;

                } else if (!(context instanceof Activity)) {
                    MoPubLog.log(CUSTOM, ADAPTER_NAME, "maio's initialization via " +
                            ADAPTER_NAME + " not started. An Activity Context is needed.");
                }
            } catch (Exception e) {
                MoPubLog.log(CUSTOM_WITH_THROWABLE, "Initializing maio has encountered " +
                        "an exception.", e);
            }

            if (networkInitializationSucceeded) {
                listener.onNetworkInitializationFinished(MaioAdapterConfiguration.class,
                        MoPubErrorCode.ADAPTER_INITIALIZATION_SUCCESS);
            } else {
                listener.onNetworkInitializationFinished(MaioAdapterConfiguration.class,
                        MoPubErrorCode.ADAPTER_CONFIGURATION_ERROR);
            }
        }

    }
}
