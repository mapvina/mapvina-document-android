# MapVina Android minimal example

Public core `1.0.2`, GeoJSON/Turf `1.0.1`, annotation `1.0.0`; Kotlin `2.2.10`.
Only one native renderer is linked. Maven local is not required.

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew :app:assembleDebug :app:testDebugUnitTest --console=plain
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.mapvina.mapvinademotest/.MainActivity
```

The demo key `public` is for sample use only. Obtain your own service key for
production and do not commit it. Location permission is optional for map rendering.

[Verification 04/10/2026: dependency, build, runtime and logo evidence](../docs/verification/2026-10-04/README.md).
Streets portrait renders at TP.HCM with the transparent horizontal MapVina logo.
This is not snapshot, all-style, navigation or real-device acceptance.
