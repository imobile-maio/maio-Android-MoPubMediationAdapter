![](https://github.com/imobile-maio/maio-iOS-SDK/blob/wiki/doc/images/logo.png)

# maio Android Mopub Adapter

# Get Started

## Adding Maio to your MoPub Dashboard

1. Open your MoPub dashboard, and click on the `Networks` tab.
1. Click on `New network`.
1. On the bottom of the modal, click on `Custom SDK network`.
1. Input the `Network settings` as below, and click `Next`:
    - `Network name`: `maio`
1. Click `Next` for the `Default CPM preferences`.
1. Input the `App & ad unit setup` as below, and click `Save & Close`:
    - Interstitial
        - Custom Event Class
            - `com.mopub.mobileads.MaioInterstitial`
        - Custom Event Class Data
            ```json
            {"mediaId": "YOUR-MEDIA-ID", "zoneId": "YOUR-ZONE-ID"}
            or
            {"mediaId": "YOUR-MEDIA-ID"}
            ```
    - Rewarded Video
        - Custom Event Class
            - `com.mopub.mobileads.MaioRewardedVideo`
        - Custom Event Class Data
            ```json
            {"mediaId": "YOUR-MEDIA-ID", "zoneId": "YOUR-ZONE-ID"}
            or
            {"mediaId": "YOUR-MEDIA-ID"}
            ```

## Implement `maio Android SDK` and `MaioMopubAdapter` to your Project

1. Install `maio Android SDK` to your project.
    - Add the following code to your project's `build.gradle`
    ```gradle
    allprojects {
        repositories {        
            maven{ url "https://imobile-maio.github.io/maven" }
        }
    }
    ```
    - Add the following code to your app's `build.gradle`
    ```gradle
    dependencies {
        implementation 'com.google.android.gms:play-services-ads:+'
        implementation 'com.maio:android-sdk:1.1.0'
    }
    ```
    - Add the following code to `AndroidManifest.xml`
    ```xml
    <activity  
              android:name="jp.maio.sdk.android.AdFullscreenActivity"  
              android:configChanges="orientation|screenLayout|screenSize|smallestScreenSize"  
              android:hardwareAccelerated="true"  
              android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >  
    </activity>
    <activity            
              android:name="jp.maio.sdk.android.HtmlBasedAdActivity"            
              android:configChanges="keyboardHidden|orientation|screenSize|screenLayout"            
              android:theme="@android:style/Theme.NoTitleBar.Fullscreen" >
    </activity>
    ```
1. Go to the [release page](https://github.com/imobile-maio/maio-Android-MoPubMediationAdapter/releases) and download the newest `MaioMoPubAdapter_v*.*.*.jar`.
1. Copy `MaioMoPubAdapter_v*.*.*.jar` to the `libs/` folder in your app.
