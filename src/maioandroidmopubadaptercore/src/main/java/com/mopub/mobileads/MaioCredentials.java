package com.mopub.mobileads;


import androidx.annotation.NonNull;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("PointlessBooleanExpression")
class MaioCredentials {
    private String _mediaId;
    String getMediaId() {
        return _mediaId;
    }

    private String _zoneId;
    String getZoneId() {
        return _zoneId;
    }

    private MaioCredentials(@NonNull String mediaId, String zoneId) {
        _mediaId = mediaId;
        _zoneId = zoneId;
    }

    private static final String MEDIA_ID = "mediaId";
    private static final String ZONE_ID = "zoneId";

    static MaioCredentials Create(Map<String, String> serverExtras) throws IllegalArgumentException {
        String mediaId = serverExtras.get(MEDIA_ID);
        if (mediaId == null) {
            throw new IllegalArgumentException("could not obtain media ID from server extras");
        }
        String zoneId = serverExtras.get(ZONE_ID);

        return new MaioCredentials(mediaId, zoneId);
    }

}