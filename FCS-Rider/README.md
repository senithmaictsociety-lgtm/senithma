# FCS Rider — Android app

This is a **real Android application** (own package id `lk.futureshipping.rider`), not a browser bookmark.
It opens full-screen and loads your live panel over HTTPS with login and the same API features.

## 1. Set your domain

Edit `app/src/main/res/values/strings.xml`:

```xml
<string name="server_base">https://polarr.tech/FCS/fcs-tracking/public</string>
<string name="start_path">rider-login.html</string>
```

Use **HTTPS**. Example:
`https://polarr.tech/FCS/fcs-tracking/public`

## 2. Build APK (on your PC)

1. Install [Android Studio](https://developer.android.com/studio)
2. **Open** this folder (`FCS-Rider`) in Android Studio
3. Wait for Gradle sync
4. Menu: **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. APK path: `app/build/outputs/apk/debug/app-debug.apk`

For release (stronger): **Build → Generate Signed Bundle / APK** (create a keystore).

## 3. Host APK on your domain

Upload e.g. `fcs-rider.apk` to your site and link:

```html
<a href="fcs-rider.apk" download>Download Rider Android App</a>
```

Users on Android open the link → install (may need “Install unknown apps”).

## 4. Security notes

- App only allows navigation on **your host** (external sites blocked)
- HTTPS only (`usesCleartextTraffic=false`)
- Login still uses your server tokens / roles
- GPS permission requested for delivery tracking
- Do not enable cleartext HTTP in production
