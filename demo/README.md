# **MapVina — Tích hợp SDK Android (Demo)**

# Tích hợp MapVinaSample vào Android

> ✅ Hướng dẫn này đã được đồng bộ với dự án `demo/` và kiểm chứng bằng build + emulator.
> Xem mục "Kiểm chứng Build & Runtime" trong `../README.md` để biết chi tiết và các lưu ý
> về đóng gói SDK (bắt buộc `mavenLocal()` + dependency `android-sdk-geojson`).

## 1. Cấu hình Gradle

### Root `build.gradle`
Thêm các dependencies và repository cần thiết:

```gradle
buildscript {
    ext.kotlin_version = "1.9.10"
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.7.0'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath 'com.google.gms:google-services:4.4.0'
        classpath "com.google.android.libraries.mapsplatform.secrets-gradle-plugin:secrets-gradle-plugin:2.0.1"
    }
}

allprojects {
    repositories {
        mavenLocal()   // ⚠️ Bắt buộc — xem lưu ý ở dưới
        google()
        jcenter()
        mavenCentral()
    }
}
```

### `gradle-wrapper.properties`
Sử dụng Gradle 8.9:

```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
```

### Module `build.gradle`
Thêm dependencies cho MapVina SDK (khớp với `demo/app/build.gradle` thực tế):

```gradle
dependencies {
    implementation 'io.github.mapvina:android-sdk:1.0.1'
    implementation 'io.github.mapvina:android-sdk-geojson:1.0.0' // bắt buộc, nếu thiếu sẽ lỗi biên dịch
    implementation('io.github.mapvina:android-plugin-annotation-v9:1.0.0') {
        exclude group: 'io.github.mapvina', module: 'android-sdk-opengl' // tránh trùng lớp
    }
}
```

> ⚠️ **Lưu ý đóng gói SDK:** artifact `android-sdk-geojson:1.0.0` publish công khai đóng gói
> sai namespace (`com.mapvina.geojson.*`), trong khi `android-sdk:1.0.1` cần
> `io.github.mapvina.geojson.*`. Bản đúng chỉ có trong Maven local, nên cần `mavenLocal()`.
> `android-sdk-turf` không cần khai báo tường minh (chỉ dùng gián tiếp).

## 2. Triển khai `MapView` trong XML
Thêm `MapView` vào file layout XML:

```xml
<io.github.mapvina.android.maps.MapView
    android:id="@+id/mapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:mapvina_cameraZoom="12"
    app:mapvina_enableTilePrefetch="true"
    app:mapvina_enableZMediaOverlay="true"
    app:mapvina_renderTextureMode="true"
    app:mapvina_renderTextureTranslucentSurface="true"
    app:mapvina_uiAttribution="true"
    app:mapvina_uiCompass="true"
    app:mapvina_uiDoubleTapGestures="true"
    app:mapvina_uiLogo="true"
    app:mapvina_uiRotateGestures="true"
    app:mapvina_uiScrollGestures="true"
    app:mapvina_uiTiltGestures="true"
    app:mapvina_uiZoomGestures="true" />
```

## 3. Khởi tạo MapVinaSample trong Activity/Fragment

### Import thư viện cần thiết:

```kotlin
import io.github.mapvina.android.MapVina
import io.github.mapvina.android.WellKnownTileServer
import io.github.mapvina.android.maps.MapView
import io.github.mapvina.android.maps.MapVinaMap
import io.github.mapvina.android.maps.Style
import io.github.mapvina.android.camera.CameraPosition
import io.github.mapvina.android.geometry.LatLng
```

### Khai báo biến:

```kotlin
private lateinit var mapvinaMap: MapVinaMap
// streets dùng ?key=public (đúng như demo); night/simple dùng ?key=public_key
private var styleUrl = "https://maps.mapvina.com/styles/v2/streets.json?key=public"
```

### Khởi tạo MapVina trong `onCreateView()`:

```kotlin
MapVina.getInstance(requireActivity(), "public", WellKnownTileServer.MapVina)
```

### Thiết lập bản đồ trong `onViewCreated()`:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.mapView.onCreate(savedInstanceState)
    binding.mapView.getMapAsync { map ->
        this.mapvinaMap = map

        map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
            enableLocationComponent(style)
        }

        val latlng = LatLng(10.728073, 106.624054)
        map.cameraPosition = CameraPosition.Builder().target(latlng).zoom(12.0).build()
    }
}
```

> ℹ️ Bản `demo/` hiện tại **không** dùng `NavigationMapRoute` trong luồng cơ bản (mã điều
> hướng turn-by-turn đã được gỡ khỏi source hiện hành). Nếu cần chỉ đường, hãy thêm module
> navigation của MapVina rồi khởi tạo, ví dụ:
> `NavigationMapRoute(null, binding.mapView, mapvinaMap)`.

## 4. Quản lý vòng đời của `MapView`
Để tránh lỗi bộ nhớ, cần gọi các phương thức vòng đời tương ứng:

```kotlin
override fun onStart() {
    super.onStart()
    mapView.onStart()
}

override fun onResume() {
    super.onResume()
    mapView.onResume()
}

override fun onPause() {
    super.onPause()
    mapView.onPause()
}

override fun onStop() {
    super.onStop()
    mapView.onStop()
}

override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
}

override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
}

override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
}
```

## 5. Hình ảnh Sample

<p align="center">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_1.JPEG" alt="Android" width="18%">   
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_2.JPEG" alt="Android" width="18%">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_3.JPEG" alt="Android" width="18%">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_4.JPEG" alt="Android" width="18%">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_5.JPEG" alt="Android" width="18%">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_6.JPEG" alt="Android" width="18%">
  <img src="https://git.advn.vn/sangnguyen/mapvina-document/-/raw/master/images/android_7.JPEG" alt="Android" width="18%">
</p>


## 6. Link Github Core
```kotlin

[⭐️ MapVina Java - Chứa các thư viện hỗ trợ Map](https://github.com/mapvina/mapvina-java)

[⭐️ MapVina Native - Chứa các thư viện core deploy chính của Map Chọn Android](https://github.com/mapvina/mapvina-native)

[⭐️ MapVina Navigation - Chứa các thư viện Navigation, Directions của Map](https://github.com/mapvina/mapvina-navigation-android)

```

## 7. Kết luận
```kotlin
Với hướng dẫn trên, bạn đã có thể tích hợp MapVinaSample vào ứng dụng Android, thiết lập bản đồ với giao diện tuỳ chỉnh, và quản lý vòng đời của `MapView` đúng cách. Bạn có thể mở rộng tính năng như hiển thị marker, vẽ tuyến đường, và sử dụng navigation bằng cách tích hợp thêm các API của MapVina.
```