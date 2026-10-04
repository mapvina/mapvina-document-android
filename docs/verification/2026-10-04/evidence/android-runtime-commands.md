# Android runtime environment: 04/10/2026

Pixel 9 API 35, `emulator-5554`, public core `1.0.2`. Sample build commands are in
the parent report. The initial fused GPS cache was outside Vietnam. Set HCMC
explicitly on this **test emulator**, not on production devices:

```bash
adb shell appops set 2000 android:mock_location allow
adb shell cmd location set-location-enabled true
adb shell cmd location providers add-test-provider gps
adb shell cmd location providers set-test-provider-enabled gps true
adb shell cmd location providers set-test-provider-location gps --location 10.8231,106.70098 --accuracy 5
adb shell cmd location providers add-test-provider fused
adb shell cmd location providers set-test-provider-enabled fused true
adb shell cmd location providers set-test-provider-location fused --location 10.8231,106.70098 --accuracy 5
adb shell am force-stop com.mapvina.sample
adb shell am start -n com.mapvina.sample/.MainActivity
adb shell dumpsys location
adb exec-out screencap -p > android-demo-light-portrait.png
adb shell input swipe 770 1350 420 1100 700
adb shell input tap 510 1050
adb shell input tap 510 1050
adb exec-out screencap -p > android-demo-pan-zoom.png
adb shell settings put system accelerometer_rotation 0
adb shell settings put system user_rotation 1
adb exec-out screencap -p > android-demo-landscape.png
adb shell settings put system user_rotation 0
```

Wait for tiles/camera animation before each capture. In portrait tap the map-style
button at `970,1936`: standard → simple → night → satellite/3D → standard.
Night and satellite results are separate from the streets acceptance.
No automated gesture assertion or snapshot/disable-logo test is implied.

When finished, remove test providers and restore normal emulator settings:

```bash
adb shell cmd location providers remove-test-provider gps
adb shell cmd location providers remove-test-provider fused
adb shell appops set 2000 android:mock_location default
adb shell settings put system accelerometer_rotation 1
```
