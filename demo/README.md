# **TRACK ASIA TÍCH HỢP**

# Tích hợp MapVinaSample vào Android

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
        classpath 'com.android.tools.build:gradle:8.4.2'
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version'
    }
}
```

### `gradle-wrapper.properties`
Đảm bảo sử dụng Gradle phiên bản 8:

```
distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
```

### Module `build.gradle`
Thêm dependencies cho MapVina SDK và các dịch vụ liên quan:

```gradle
dependencies {
    implementation('io.github.map-vina:android-sdk:2.0.2')
    implementation('io.github.map-vina:geojson:2.0.1')
    implementation('io.github.map-vina:turf:2.0.1')
    implementation('io.github.map-vina:android-plugin-annotation-v9:2.0.1')
}
```

## 2. Triển khai `MapView` trong XML
Thêm `MapView` vào file layout XML:

```xml
<com.mapvina.android.maps.MapView
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
import com.mapvina.android.maps.MapView
import com.mapvina.android.maps.MapVinaMap
import com.mapvina.android.maps.CameraPosition
import com.mapvina.android.maps.Style
import com.mapvina.android.geometry.LatLng
import com.mapvina.android.MapVina
import com.mapvina.android.navigation.ui.NavigationMapRoute
```

### Khai báo biến:

```kotlin
private lateinit var mapvinaMap: MapVinaMap
private var styleUrl = "https://maps.map-vina.com/styles/v1/streets.json?key=public_key"
private lateinit var navigationMapRoute: NavigationMapRoute
```

### Khởi tạo MapVina trong `onCreateView()`:

```kotlin
MapVina.getInstance(requireActivity())
```

### Thiết lập bản đồ trong `onViewCreated()`:

```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    
    mapView.onCreate(savedInstanceState)
    mapView.getMapAsync { map ->
        this.mapvinaMap = map
        
        map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
            enableLocationComponent(style)
        }
        
        navigationMapRoute = NavigationMapRoute(mapView, map)
        
        val latlng = LatLng(10.728073, 106.624054)
        map.cameraPosition = CameraPosition.Builder().target(latlng).zoom(12.0).build()
    }
}
```

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

[⭐️ MapVina Java - Chứa các thư viện hỗ trợ Map](https://github.com/map-vina/mapvina-java)

[⭐️ MapVina Native - Chứa các thư viện core deploy chính của Map Chọn Android](https://github.com/map-vina/mapvina-native)

[⭐️ MapVina Navigation - Chứa các thư viện Navigation, Directions của Map](https://github.com/map-vina/mapvina-navigation-android)

```

## 7. Kết luận
```kotlin
Với hướng dẫn trên, bạn đã có thể tích hợp MapVinaSample vào ứng dụng Android, thiết lập bản đồ với giao diện tuỳ chỉnh, và quản lý vòng đời của `MapView` đúng cách. Bạn có thể mở rộng tính năng như hiển thị marker, vẽ tuyến đường, và sử dụng navigation bằng cách tích hợp thêm các API của MapVina.
```