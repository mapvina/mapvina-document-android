# Minimal Android MapVina sample

This sample builds against Maven Central without `mavenLocal()`:

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.mapvina.mapvinademotest/.MainActivity
```

`app/build.gradle.kts` uses core `android-sdk:1.0.2`, GeoJSON/Turf `1.0.1`,
and annotation plugin `1.0.0`. Kotlin `2.2.10` is required by the SDK's Kotlin
metadata; imports and the XML `MapView` use `io.github.mapvina.android.*`.
The map uses the public example key for demonstration only; configure a
project-specific key according to your MapVina deployment before production.

On 27/09/2026, this sample built and rendered a streets map with the horizontal
MapVina logo at bottom-start on an Android API 35 emulator. See
[`../SDK_RELEASE_AUDIT_2026-09-27.md`](../SDK_RELEASE_AUDIT_2026-09-27.md)
for other platforms and known blockers.
