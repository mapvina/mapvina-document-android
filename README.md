# 📍 MapVina Maps Android SDK V2- Hướng dẫn Tích hợp Chi tiết

## 📋 Mục lục
1. [Giới thiệu](#giới-thiệu)
2. [Tính năng chính](#tính-năng-chính)
3. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
4. [Cấu hình dự án](#cấu-hình-dự-án)
5. [Tích hợp chi tiết](#tích-hợp-chi-tiết)
6. [Các tính năng nâng cao](#các-tính-năng-nâng-cao)
7. [API Services](#api-services)
8. [Xử lý lỗi thường gặp](#xử-lý-lỗi-thường-gặp)
9. [Best Practices](#best-practices)
10. [Tài liệu tham khảo](#tài-liệu-tham-khảo)

---

## 🌟 Giới thiệu

MapVina Maps SDK là một giải pháp bản đồ mạnh mẽ được thiết kế đặc biệt cho thị trường Đông Nam Á, hỗ trợ nhiều quốc gia bao gồm Việt Nam, Singapore, Thái Lan, Malaysia và Taiwan. SDK cung cấp đầy đủ các tính năng từ hiển thị bản đồ cơ bản đến điều hướng phức tạp.

### Điểm nổi bật:
- ✅ Hỗ trợ đa quốc gia với dữ liệu địa phương chi tiết
- ✅ Tích hợp dễ dàng với Android (Java/Kotlin)
- ✅ Hiệu suất cao với render hardware acceleration
- ✅ Real-time navigation với turn-by-turn directions

---

## 🎯 Tính năng chính

### 1. **Map Display & Styles**
- 🗺️ Nhiều kiểu bản đồ: Streets, Satellite, Night, Simple, Terrain
- 🎨 Hỗ trợ custom style với Style Specification
- 🌐 Tile prefetching để tải bản đồ mượt mà

### 2. **Location Services**
- 📍 **Current Location**: Hiển thị vị trí hiện tại với độ chính xác cao
- 🧭 **Compass**: La bàn tích hợp để định hướng
- 📡 **GPS Tracking**: Theo dõi di chuyển real-time
- 🔄 **Location Updates**: Cập nhật vị trí liên tục

### 3. **Markers & Annotations**
- 📌 **Single Point Marker**: Đặt marker đơn với info window
- 📍 **Multi-point Markers**: Quản lý nhiều điểm trên bản đồ
- 🎯 **Custom Markers**: Tùy chỉnh icon và style
- 💬 **Info Windows**: Hiển thị thông tin chi tiết khi tap

### 4. **Clustering**
- 🔵 **Dynamic Clustering**: Tự động nhóm điểm khi zoom out
- 📊 **Cluster Statistics**: Hiển thị số lượng điểm trong cluster
- 🎨 **Custom Cluster Rendering**: Tùy chỉnh giao diện cluster
- ⚡ **Performance Optimization**: Xử lý hàng nghìn điểm hiệu quả

### 5. **Navigation & Routing**
- 🚗 **Turn-by-turn Navigation**: Chỉ đường chi tiết từng khúc
- 🛣️ **Multi-waypoint Routes**: Hỗ trợ nhiều điểm dừng

### 6. **Geocoding Services**
- 🔍 **Search/Autocomplete**: Tìm kiếm địa điểm với gợi ý
- 🏠 **Reverse Geocoding**: Chuyển tọa độ thành địa chỉ
- 🌍 **Multi-language Support**: Hỗ trợ tiếng Việt, Anh, Thái...
- 📍 **POI Search**: Tìm điểm quan tâm (ATM, nhà hàng, v.v.)

---

## 💻 Yêu cầu hệ thống

### Android Requirements:
```yaml
Minimum SDK: API 26 (Android 8.0)
Target SDK: API 35 (Android 15)
Compile SDK: 35
NDK: 27.1.12297006
Kotlin: 1.9.10
Android Gradle Plugin: 8.7.0
Gradle Wrapper: 8.9
```

> ✅ **Đã kiểm chứng bằng build/emulator** (xem mục "Kiểm chứng Build & Runtime"):
> các giá trị trên khớp với dự án `demo/` thực tế.

### Supported ABIs:
- `armeabi-v7a` (32-bit ARM)
- `arm64-v8a` (64-bit ARM)
- `x86` (32-bit x86)
- `x86_64` (64-bit x86)

---

## ⚙️ Cấu hình dự án

### 1. Root `build.gradle`
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
        mavenLocal()   // ⚠️ Bắt buộc: xem mục "Kiểm chứng Build & Runtime" bên dưới
        google()
        jcenter()
        mavenCentral()
    }
}
```

> ⚠️ **Vì sao cần `mavenLocal()`?** Artifact `io.github.mapvina:android-sdk-geojson:1.0.0`
> được publish công khai lại đóng gói sai namespace (`com.mapvina.geojson.*`), trong khi
> `android-sdk:1.0.1` được biên dịch để dùng `io.github.mapvina.geojson.*`. Bản geojson
> đóng gói đúng namespace hiện chỉ tồn tại trong Maven local (`~/.m2`). Chi tiết ở cuối tài liệu.

### 2. Module `build.gradle`
```gradle
android {
    compileSdk 35
    
    defaultConfig {
        applicationId "com.mapvina.sample"
        minSdk 26
        targetSdk 35
        versionCode 10
        versionName "1.0.7"
        multiDexEnabled true

        // Cấu hình ABI filters
        ndk {
            abiFilters 'armeabi-v7a', 'arm64-v8a', 'x86', 'x86_64'
        }
    }

    buildFeatures {
        viewBinding true
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}

dependencies {
    // MapVina Core SDK
    implementation 'io.github.mapvina:android-sdk:1.0.1'

    // MapVina GeoJSON (bắt buộc — demo dùng io.github.mapvina.geojson.*)
    implementation 'io.github.mapvina:android-sdk-geojson:1.0.0'

    // MapVina Plugins — loại bỏ artifact core cũ `android-sdk-opengl` bị trùng lớp
    implementation('io.github.mapvina:android-plugin-annotation-v9:1.0.0') {
        exclude group: 'io.github.mapvina', module: 'android-sdk-opengl'
    }
}
```

> ℹ️ **Khác biệt so với tài liệu cũ (đã kiểm chứng trên `demo/`):**
> - `versionName` thực tế là `1.0.7`, `versionCode` là `10` (không phải `2.0.0`).
> - Core SDK là `android-sdk:1.0.1` (không phải `1.0.0`).
> - `android-sdk-geojson:1.0.0` là **bắt buộc**; nếu thiếu, dự án **không biên dịch được**
>   (lỗi `Unresolved reference: geojson`).
> - `android-sdk-turf` không được import trực tiếp trong `demo/` (chỉ kéo theo gián tiếp),
>   nên không cần khai báo tường minh.
> - Cần `exclude ... android-sdk-opengl` để tránh lỗi trùng lớp (duplicate class).
> - Dự án `example/` (tham khảo) dùng cấu hình khác: `android-sdk:1.0.0` + `geojson` + `turf`
>   qua version catalog và repository JitPack/Sonatype — **không** phải cấu hình của `demo/`.

### 3. `gradle.properties`
```properties
android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx4096m
org.gradle.parallel=true
org.gradle.caching=true
```

### 4. AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

    <!-- Permissions (khớp với demo/) -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.CAMERA" />

    <!-- (Tuỳ chọn) Nếu bật location component, tự thêm — demo hiện KHÔNG khai báo:
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> -->

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        android:usesCleartextTraffic="true">

        <!-- Launcher Activity -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- MapVina Navigation Activity -->
        <activity
            android:name="com.mapvina.navigation.android.navigation.ui.v5.MapVinaNavigationActivity"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.NoActionBar" />

        <!-- ⚠️ BẢO MẬT: Không hardcode Google Maps API key trong manifest.
             Xem mục "Bảo mật khoá API" ở cuối tài liệu. -->
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="${MAPS_API_KEY}" />
    </application>
</manifest>
```

> ℹ️ **Khác biệt so với tài liệu cũ (đã kiểm chứng trên manifest thực tế):**
> - Không có `android:name=".MyApplication"` — demo **không** có custom Application class.
> - Không có service `MapboxNavigationService`. Thay vào đó là **activity**
>   `com.mapvina.navigation.android.navigation.ui.v5.MapVinaNavigationActivity`.
> - Manifest thực tế **không** khai báo `ACCESS_FINE/COARSE_LOCATION` (dù hướng dẫn có mô tả
>   location component). Nếu bạn dùng location, hãy tự thêm hai quyền này.

---

## 🚀 Tích hợp chi tiết

### 1. Khởi tạo MapVina

Demo **không** dùng custom `Application` class. MapVina được khởi tạo ngay trong
Fragment/Activity trước khi dùng `MapView`, kèm tên tile server:

```kotlin
import io.github.mapvina.android.MapVina
import io.github.mapvina.android.WellKnownTileServer

// Ví dụ trong Fragment.onCreateView() của demo:
MapVina.getInstance(requireActivity(), "public", WellKnownTileServer.MapVina)
```

### 2. Layout XML với MapView
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- MapView với đầy đủ cấu hình -->
    <io.github.mapvina.android.maps.MapView
        android:id="@+id/mapView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:mapvina_cameraTargetLat="10.728073"
        app:mapvina_cameraTargetLng="106.624054"
        app:mapvina_cameraZoom="12"
        app:mapvina_cameraZoomMax="20"
        app:mapvina_cameraZoomMin="4"
        app:mapvina_cameraBearing="0"
        app:mapvina_cameraTilt="0"
        app:mapvina_enableTilePrefetch="true"
        app:mapvina_enableZMediaOverlay="true"
        app:mapvina_renderTextureMode="true"
        app:mapvina_renderTextureTranslucentSurface="true"
        app:mapvina_uiAttribution="true"
        app:mapvina_uiAttributionGravity="bottom|start"
        app:mapvina_uiAttributionMarginLeft="4dp"
        app:mapvina_uiAttributionMarginBottom="4dp"
        app:mapvina_uiCompass="true"
        app:mapvina_uiCompassGravity="top|end"
        app:mapvina_uiCompassMarginTop="4dp"
        app:mapvina_uiCompassMarginRight="4dp"
        app:mapvina_uiDoubleTapGestures="true"
        app:mapvina_uiLogo="true"
        app:mapvina_uiLogoGravity="bottom|start"
        app:mapvina_uiLogoMarginLeft="4dp"
        app:mapvina_uiLogoMarginBottom="4dp"
        app:mapvina_uiRotateGestures="true"
        app:mapvina_uiScrollGestures="true"
        app:mapvina_uiTiltGestures="true"
        app:mapvina_uiZoomGestures="true" />
        
    <!-- Floating Action Buttons -->
    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabMyLocation"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_margin="16dp"
        android:src="@drawable/ic_my_location"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent" />
        
</androidx.constraintlayout.widget.ConstraintLayout>
```

### 3. Activity/Fragment Implementation

> 🏛️ **Kiến trúc thực tế:** `demo/` là ứng dụng **fragment-based**. `MainActivity` (host
> Navigation) + `MainViewModel` (state dùng chung) điều phối các màn hình dạng Fragment:
> `MapSinglePointFragment`, `MapClusterFragment`, `MapMultiPointFragment`,
> `MapWayPointFragment`, `MapDirectionPointFragment`, `MapAnimationFragment`,
> `MapFeatureFragment`. **Không** tồn tại một `MapActivity` khối đơn như mô tả cũ.
> Đoạn code dưới đây minh hoạ vòng đời/khởi tạo map (áp dụng tương tự trong một Fragment).

```kotlin
import io.github.mapvina.android.MapVina
import io.github.mapvina.android.maps.MapView
import io.github.mapvina.android.maps.MapVinaMap
import io.github.mapvina.android.maps.Style
import io.github.mapvina.android.camera.CameraPosition
import io.github.mapvina.android.geometry.LatLng
import io.github.mapvina.android.location.permissions.PermissionsListener
import io.github.mapvina.android.location.permissions.PermissionsManager

class MapActivity : AppCompatActivity(), PermissionsListener {

    private lateinit var mapView: MapView
    private lateinit var mapvinaMap: MapVinaMap
    private lateinit var permissionsManager: PermissionsManager
    // Navigation (tuỳ chọn) — cần module com.mapvina.navigation.* nếu dùng chỉ đường
    private lateinit var navigationMapRoute: NavigationMapRoute

    // Configuration — streets dùng ?key=public (đúng như demo)
    private val styleUrl = "https://maps.mapvina.com/styles/v2/streets.json?key=public"
    private val defaultLocation = LatLng(10.728073, 106.624054) // Ho Chi Minh City
    private val defaultZoom = 12.0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        
        // Initialize MapVina
        MapVina.getInstance(this)
        
        // Get MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        
        // Initialize map
        mapView.getMapAsync { map ->
            mapvinaMap = map
            
            // Set style
            map.setStyle(Style.Builder().fromUri(styleUrl)) { style ->
                // Enable location component
                enableLocationComponent(style)
                
                // Add map click listener
                map.addOnMapClickListener { point ->
                    addMarker(point)
                    return@addOnMapClickListener true
                }
                
                // Initialize navigation route
                navigationMapRoute = NavigationMapRoute(null, mapView, map)
                
                // Move camera to default location
                map.cameraPosition = CameraPosition.Builder()
                    .target(defaultLocation)
                    .zoom(defaultZoom)
                    .build()
            }
        }
    }
    
    @SuppressWarnings("MissingPermission")
    private fun enableLocationComponent(style: Style) {
        // Check permissions
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Enable location component
            val locationComponent = mapvinaMap.locationComponent
            
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(this, style)
                    .useDefaultLocationEngine(true)
                    .build()
            )
            
            locationComponent.isLocationComponentEnabled = true
            locationComponent.cameraMode = CameraMode.TRACKING
            locationComponent.renderMode = RenderMode.COMPASS
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }
    
    private fun addMarker(latLng: LatLng) {
        mapvinaMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title("Selected Location")
                .snippet("Lat: ${latLng.latitude}, Lng: ${latLng.longitude}")
        )
    }
    
    // Lifecycle methods
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
    
    // Permission callbacks
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "Location permission needed for navigation", Toast.LENGTH_LONG).show()
    }
    
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapView.getMapAsync { map ->
                map.getStyle { style ->
                    enableLocationComponent(style)
                }
            }
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_LONG).show()
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
```

---

## 🎨 Các tính năng nâng cao

### 1. Multi-point Navigation
```kotlin
class MultiPointNavigation {
    
    private fun calculateRoute(waypoints: List<Point>) {
        // Build route options
        val routeOptions = RouteOptions.builder()
            .apply {
                // Add all waypoints
                waypoints.forEach { point ->
                    coordinates(point)
                }
            }
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .steps(true)
            .annotations(DirectionsCriteria.ANNOTATION_DISTANCE)
            .build()
        
        // Get route from API
        NavigationRoute.builder(context)
            .accessToken(getString(R.string.mapbox_access_token))
            .routeOptions(routeOptions)
            .build()
            .getRoute(object : NavigationRoute.RouteListener {
                override fun onResponse(response: DirectionsResponse) {
                    val route = response.routes().firstOrNull()
                    route?.let {
                        // Display route on map
                        navigationMapRoute.addRoute(it)
                        
                        // Start navigation
                        startNavigation(it)
                    }
                }
                
                override fun onFailure(throwable: Throwable) {
                    Log.e("Navigation", "Route request failed: ${throwable.message}")
                }
            })
    }
    
    private fun startNavigation(route: DirectionsRoute) {
        val options = NavigationLauncherOptions.builder()
            .directionsRoute(route)
            .shouldSimulateRoute(true)
            .build()
            
        NavigationLauncher.startNavigation(this, options)
    }
}
```

### 2. Marker Clustering
```kotlin
class MarkerClustering {
    
    private fun setupClustering(style: Style, points: List<LatLng>) {
        // Convert points to Features
        val featureList = points.map { point ->
            Feature.fromGeometry(
                Point.fromLngLat(point.longitude, point.latitude)
            ).apply {
                addNumberProperty("cluster_id", Random.nextInt())
            }
        }
        
        // Create GeoJSON source with clustering
        val source = GeoJsonSource(
            "cluster-source",
            FeatureCollection.fromFeatures(featureList),
            GeoJsonOptions()
                .withCluster(true)
                .withClusterMaxZoom(14)
                .withClusterRadius(50)
        )
        
        style.addSource(source)
        
        // Add cluster circles
        val clusterLayer = CircleLayer("clusters", "cluster-source")
            .withProperties(
                PropertyFactory.circleColor(
                    Expression.step(
                        Expression.get("point_count"),
                        Expression.color(Color.parseColor("#51bbd6")),
                        Expression.stop(100, Expression.color(Color.parseColor("#f1f075"))),
                        Expression.stop(750, Expression.color(Color.parseColor("#f28cb1")))
                    )
                ),
                PropertyFactory.circleRadius(
                    Expression.step(
                        Expression.get("point_count"),
                        Expression.literal(20f),
                        Expression.stop(100, 30f),
                        Expression.stop(750, 40f)
                    )
                )
            )
            .withFilter(Expression.has("point_count"))
            
        style.addLayer(clusterLayer)
        
        // Add cluster count text
        val countLayer = SymbolLayer("cluster-count", "cluster-source")
            .withProperties(
                PropertyFactory.textField(Expression.toString(Expression.get("point_count"))),
                PropertyFactory.textSize(12f),
                PropertyFactory.textColor(Color.WHITE)
            )
            .withFilter(Expression.has("point_count"))
            
        style.addLayer(countLayer)
        
        // Add unclustered points
        val unclusteredLayer = CircleLayer("unclustered-point", "cluster-source")
            .withProperties(
                PropertyFactory.circleColor(Color.parseColor("#11b4da")),
                PropertyFactory.circleRadius(8f),
                PropertyFactory.circleStrokeWidth(1f),
                PropertyFactory.circleStrokeColor(Color.WHITE)
            )
            .withFilter(Expression.not(Expression.has("point_count")))
            
        style.addLayer(unclusteredLayer)
    }
}
```

### 3. Custom Map Styles
```kotlin
class CustomMapStyles {
    
    companion object {
        // ⚠️ key theo từng endpoint (đã kiểm chứng trong demo/ Constants.kt):
        //   - streets  -> ?key=public
        //   - night/simple -> ?key=public_key
        const val STYLE_VN_STREETS = "https://maps.mapvina.com/styles/v2/streets.json?key=public"
        const val STYLE_VN_NIGHT = "https://maps.mapvina.com/styles/v2/night.json?key=public_key"
        const val STYLE_VN_SIMPLE = "https://maps.mapvina.com/styles/v2/simple.json?key=public_key"
        const val STYLE_SG_STREETS = "https://sg-maps.mapvina.com/styles/v2/streets.json?key=public"
        const val STYLE_TH_STREETS = "https://th-maps.mapvina.com/styles/v2/streets.json?key=public"
    }
    
    fun switchMapStyle(style: String) {
        mapvinaMap.setStyle(Style.Builder().fromUri(style)) { newStyle ->
            // Re-enable location component with new style
            enableLocationComponent(newStyle)
            
            // Re-add sources and layers if needed
            reloadMapData(newStyle)
        }
    }
    
    fun createCustomStyle(): Style {
        return Style.Builder()
            .fromUri(STYLE_VN_STREETS)
            .withLayer(
                LineLayer("route-layer", "route-source")
                    .withProperties(
                        PropertyFactory.lineColor(Color.parseColor("#009688")),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND)
                    )
            )
            .withLayer(
                FillLayer("polygon-layer", "polygon-source")
                    .withProperties(
                        PropertyFactory.fillColor(Color.parseColor("#FF5722")),
                        PropertyFactory.fillOpacity(0.5f)
                    )
            )
            .build()
    }
}
```

### 4. Geofencing
```kotlin
class GeofencingExample {
    
    private fun createGeofence(center: LatLng, radius: Double) {
        // Create circular polygon for geofence
        val polygon = TurfTransformation.circle(
            Point.fromLngLat(center.longitude, center.latitude),
            radius,
            64,
            "meters"
        )
        
        // Add geofence to map
        val source = GeoJsonSource("geofence-source", polygon)
        style.addSource(source)
        
        // Add visual layer
        val fillLayer = FillLayer("geofence-fill", "geofence-source")
            .withProperties(
                PropertyFactory.fillColor(Color.parseColor("#FF5722")),
                PropertyFactory.fillOpacity(0.3f)
            )
        style.addLayer(fillLayer)
        
        // Check if location is inside geofence
        mapvinaMap.locationComponent.addOnLocationClickListener { location ->
            val userPoint = Point.fromLngLat(location.longitude, location.latitude)
            val isInside = TurfJoins.inside(userPoint, polygon)
            
            if (isInside) {
                Toast.makeText(context, "Inside geofence", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Outside geofence", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
```

---

## 🌐 API Services

### 1. API Configuration
```kotlin
object ApiConstants {
    // Base URLs by region
    const val BASE_URL_VN = "https://maps.mapvina.com/"
    const val BASE_URL_SG = "https://sg-maps.mapvina.com/"
    const val BASE_URL_TH = "https://th-maps.mapvina.com/"
    const val BASE_URL_TW = "https://tw-maps.mapvina.com/"
    const val BASE_URL_MY = "https://my-maps.mapvina.com/"
    
    // API Endpoints
    const val GEOCODING_ENDPOINT = "api/v1/geocode"
    const val REVERSE_GEOCODING_ENDPOINT = "api/v1/reverse"
    const val AUTOCOMPLETE_ENDPOINT = "api/v1/autocomplete"
    const val DIRECTIONS_ENDPOINT = "route/v1/car"
    const val ELEVATION_ENDPOINT = "api/v1/elevation"
    
    // API Keys
    const val PUBLIC_KEY = "public_key"
    const val PRIVATE_KEY = "your_private_key_here"
}
```

### 2. Retrofit Setup
```kotlin
class RetrofitClient {
    companion object {
        private const val TIMEOUT_READ = 30L
        private const val TIMEOUT_CONNECT = 30L
        
        fun getClient(baseUrl: String): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_READ, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val request = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .method(original.method, original.body)
                        .build()
                    chain.proceed(request)
                }
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor.Level.BODY
                    } else {
                        HttpLoggingInterceptor.Level.NONE
                    }
                })
                .build()
            
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
        }
    }
}
```

### 3. API Service Interface
```kotlin
interface MapVinaApiService {
    
    @GET("api/v1/reverse")
    suspend fun reverseGeocode(
        @Query("point.lat") latitude: Double,
        @Query("point.lon") longitude: Double,
        @Query("lang") language: String = "vi",
        @Query("key") apiKey: String = ApiConstants.PUBLIC_KEY
    ): Response<GeocodingResponse>
    
    @GET("api/v1/autocomplete")
    suspend fun autocomplete(
        @Query("text") query: String,
        @Query("lang") language: String = "vi",
        @Query("focus.point.lat") focusLat: Double? = null,
        @Query("focus.point.lon") focusLon: Double? = null,
        @Query("key") apiKey: String = ApiConstants.PUBLIC_KEY
    ): Response<AutocompleteResponse>
    
    @GET("route/v1/car/{coordinates}.json")
    suspend fun getDirections(
        @Path("coordinates") coordinates: String,
        @Query("geometries") geometries: String = "polyline6",
        @Query("steps") steps: Boolean = true,
        @Query("overview") overview: String = "full",
        @Query("key") apiKey: String = ApiConstants.PUBLIC_KEY
    ): Response<DirectionsResponse>
}
```

### 4. API Usage Examples
```kotlin
class ApiUsageExamples {
    private val apiService = RetrofitClient.getClient(ApiConstants.BASE_URL_VN)
        .create(MapVinaApiService::class.java)
    
    // Reverse Geocoding
    fun reverseGeocode(lat: Double, lng: Double) {
        lifecycleScope.launch {
            try {
                val response = apiService.reverseGeocode(lat, lng)
                if (response.isSuccessful) {
                    val address = response.body()?.features?.firstOrNull()?.properties?.label
                    Log.d("API", "Address: $address")
                }
            } catch (e: Exception) {
                Log.e("API", "Error: ${e.message}")
            }
        }
    }
    
    // Autocomplete Search
    fun searchLocation(query: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.autocomplete(
                    query = query,
                    focusLat = currentLocation?.latitude,
                    focusLon = currentLocation?.longitude
                )
                
                if (response.isSuccessful) {
                    val suggestions = response.body()?.features ?: emptyList()
                    displaySuggestions(suggestions)
                }
            } catch (e: Exception) {
                Log.e("API", "Search error: ${e.message}")
            }
        }
    }
    
    // Get Directions
    fun getRoute(waypoints: List<LatLng>) {
        val coordinates = waypoints.joinToString(";") { 
            "${it.longitude},${it.latitude}" 
        }
        
        lifecycleScope.launch {
            try {
                val response = apiService.getDirections(coordinates)
                if (response.isSuccessful) {
                    val route = response.body()?.routes?.firstOrNull()
                    route?.let {
                        displayRoute(it)
                    }
                }
            } catch (e: Exception) {
                Log.e("API", "Route error: ${e.message}")
            }
        }
    }
}
```

---

## ❌ Xử lý lỗi thường gặp

### 1. **Map không hiển thị**

**Nguyên nhân:**
- API key không hợp lệ hoặc thiếu
- URL style không đúng
- Không có kết nối Internet
- Thiếu permissions trong Manifest

**Giải pháp:**
```kotlin
// Kiểm tra API key
if (styleUrl.contains("key=")) {
    // Ensure key is valid
    val validatedUrl = styleUrl.replace("public", "public_key")
}

// Add error handling
mapView.getMapAsync { map ->
    map.setStyle(Style.Builder().fromUri(styleUrl), 
        object : Style.OnStyleLoaded {
            override fun onStyleLoaded(style: Style) {
                // Style loaded successfully
            }
        }, 
        object : Style.OnStyleError {
            override fun onError(error: String) {
                Log.e("MapError", "Style loading error: $error")
                // Fallback to default style
                map.setStyle(Style.MAPBOX_STREETS)
            }
        }
    )
}
```

### 2. **Location permission issues**

**Giải pháp:**
```kotlin
class LocationPermissionHelper {
    
    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1234
        
        fun checkAndRequestPermissions(activity: Activity): Boolean {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            
            val permissionsToRequest = permissions.filter {
                ContextCompat.checkSelfPermission(activity, it) != 
                    PackageManager.PERMISSION_GRANTED
            }
            
            return if (permissionsToRequest.isEmpty()) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    permissionsToRequest.toTypedArray(),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
                false
            }
        }
    }
}
```

### 3. **OutOfMemoryError với nhiều markers**

**Giải pháp:**
```kotlin
class MarkerOptimization {
    
    // Use Symbol Layer instead of individual markers
    fun addMarkersEfficiently(points: List<LatLng>) {
        val features = points.map { point ->
            Feature.fromGeometry(
                Point.fromLngLat(point.longitude, point.latitude)
            )
        }
        
        val source = GeoJsonSource("markers-source", 
            FeatureCollection.fromFeatures(features))
        style.addSource(source)
        
        val symbolLayer = SymbolLayer("markers-layer", "markers-source")
            .withProperties(
                PropertyFactory.iconImage("custom-marker"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
        style.addLayer(symbolLayer)
    }
}
```

### 4. **Navigation route không hiển thị**

**Nguyên nhân:**
- NavigationMapRoute chưa được khởi tạo đúng
- Style thay đổi sau khi add route
- Route data không hợp lệ

**Giải pháp:**
```kotlin
private fun fixNavigationRoute() {
    // Ensure navigationMapRoute is initialized after style is loaded
    mapView.getMapAsync { map ->
        map.getStyle { style ->
            // Initialize navigation route with proper style
            navigationMapRoute = NavigationMapRoute(null, mapView, map)
            
            // Add route
            route?.let {
                navigationMapRoute.addRoute(it)
            }
        }
    }
}
```

### 5. **Memory leaks**

**Giải pháp:**
```kotlin
class MapFragment : Fragment() {
    
    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    
    override fun onDestroyView() {
        super.onDestroyView()
        
        // Clean up map resources
        navigationMapRoute?.onDestroy()
        mapvinaMap?.removeOnMapClickListener(mapClickListener)
        
        // Clean up binding
        _binding = null
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Cancel any ongoing operations
        lifecycleScope.coroutineContext.cancelChildren()
        
        // Destroy map view
        mapView?.onDestroy()
    }
}
```

### 6. **ANR (Application Not Responding)**

**Giải pháp:**
```kotlin
class BackgroundProcessing {
    
    // Process heavy operations in background
    fun processLargeDataSet(points: List<LatLng>) {
        lifecycleScope.launch(Dispatchers.IO) {
            // Heavy processing
            val processedData = points.map { 
                // Complex calculation
            }
            
            withContext(Dispatchers.Main) {
                // Update UI on main thread
                displayProcessedData(processedData)
            }
        }
    }
}
```

### 7. **Crash khi rotate device**

**Giải pháp:**
```xml
<!-- In AndroidManifest.xml -->
<activity
    android:name=".MapActivity"
    android:configChanges="orientation|screenSize|keyboardHidden"
    android:screenOrientation="sensor" />
```

```kotlin
override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    // Handle configuration change without recreating activity
    mapView?.onConfigurationChanged(newConfig)
}
```

---

## 📋 Best Practices

### 1. **Performance Optimization**
```kotlin
// Use view binding instead of findViewById
private lateinit var binding: ActivityMapBinding

// Reuse objects
private val markerOptions = MarkerOptions()

// Use object pool for frequent allocations
private val pointPool = Pools.SimplePool<Point>(20)

// Batch operations
fun addMultipleMarkers(points: List<LatLng>) {
    val features = mutableListOf<Feature>()
    points.forEach { point ->
        features.add(Feature.fromGeometry(
            Point.fromLngLat(point.longitude, point.latitude)
        ))
    }
    // Add all at once
    source.setGeoJson(FeatureCollection.fromFeatures(features))
}
```

### 2. **ProGuard Rules**
```proguard
# MapVina core SDK (namespace thực tế: io.github.mapvina.android.*)
-keep class io.github.mapvina.** { *; }
-keep interface io.github.mapvina.** { *; }
-keep enum io.github.mapvina.** { *; }

# GeoJSON + Navigation (một số module vẫn dùng namespace com.mapvina.*)
-keep class com.mapvina.** { *; }
-keep interface com.mapvina.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
```

---

## 📚 Tài liệu tham khảo

### Official Documentation
- 🔗 [MapVina Android SDK](https://github.com/mapvina/mapvina-native)
- 🔗 [MapVina Java](https://github.com/mapvina/mapvina-java)
- 🔗 [MapVina Navigation Android](https://github.com/mapvina/mapvina-navigation-android)
- 🔗 [MapBox Style Specification](https://docs.mapbox.com/mapbox-gl-js/style-spec/)

### API Documentation
- 📖 [Geocoding API](https://docs.mapvina.com/api/geocoding)
- 📖 [Directions API](https://docs.mapvina.com/api/directions)
- 📖 [Map Tiles API](https://docs.mapvina.com/api/maps)

### Sample Projects
- 💻 [MapVina Demo Android (repo này)](https://github.com/mapvina/mapvina-document-android)
- 💻 [MapVina Navigation Android](https://github.com/mapvina/mapvina-navigation-android)

### Community Resources
- 💬 [Stack Overflow - MapVina Tag](https://stackoverflow.com/questions/tagged/mapvina)
- 💬 [GitHub Issues](https://github.com/mapvina/mapvina-native/issues)
- 💬 [Discord Community](https://discord.gg/mapvina)

---

## 📄 License

MapVina SDK được phát hành dưới giấy phép BSD-3-Clause. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

---

## 📧 Support

Nếu bạn gặp vấn đề hoặc cần hỗ trợ:

- 📧 Email: support@mapvina.com
- 🐛 Report bugs: [GitHub Issues](https://github.com/mapvina/mapvina-native/issues)
- 💡 Feature requests: [GitHub Discussions](https://github.com/mapvina/mapvina-native/discussions)

---

## ✅ Kiểm chứng Build & Runtime

Tài liệu này đã được đồng bộ với dự án `demo/` thực tế và **kiểm chứng bằng cách build +
chạy trên emulator Android**.

### Đã kiểm chứng bằng build (verified by build)
- `demo/` biên dịch thành công `:app:assembleDebug` với **JDK 17**, **AGP 8.7.0**,
  **Gradle wrapper 8.9**, **compileSdk 35**, **NDK 27.1.12297006**.
- Các giá trị `versionName 1.0.7` / `versionCode 10`, dependency `android-sdk:1.0.1`,
  namespace `io.github.mapvina.android.*` + `io.github.mapvina.geojson.*`, và manifest
  (launcher `.MainActivity`, activity `MapVinaNavigationActivity`, không có
  `MyApplication`/`MapboxNavigationService`) đều khớp với mã nguồn thực tế.

### Đã kiểm chứng bằng runtime (verified on emulator)
- Cài APK lên emulator (AVD android-35), mở app → `MainActivity` hiển thị.
- Native lib `libmapvina.so` nạp thành công; log xác nhận nạp style:
  `https://maps.mapvina.com/styles/v2/streets.json?key=public`.
- Ảnh chụp màn hình xác nhận **style "streets" của MapVina render đúng** (nền đất be
  `rgb(244,244,232)`, nước/đường màu xanh) — xem `demo/emulator_map_verification.png`.

### ⚠️ Vấn đề hạ tầng SDK cần lưu ý (không phải lỗi tài liệu)
Dự án `demo/` như đang commit **không build được ngay** vì SDK publish công khai không
nhất quán. Để build/chạy được, `demo/app/build.gradle` cần 3 điều chỉnh (đã áp dụng):
1. Bổ sung `io.github.mapvina:android-sdk-geojson:1.0.0` (mã nguồn dùng nhưng chưa khai báo).
2. Thêm `mavenLocal()` — artifact geojson publish công khai đóng gói sai namespace
   (`com.mapvina.geojson.*`), trong khi `android-sdk:1.0.1` cần `io.github.mapvina.geojson.*`;
   bản đúng namespace hiện chỉ có trong `~/.m2`.
3. `exclude ... android-sdk-opengl` khỏi `android-plugin-annotation-v9` để tránh trùng lớp.

> 👉 **Khuyến nghị dài hạn:** publish lại `android-sdk-geojson`/`android-sdk-turf` đúng
> namespace `io.github.mapvina.geojson.*` và gỡ artifact core cũ `android-sdk-opengl`,
> để người dùng không phải phụ thuộc vào `mavenLocal()`.

### Chưa kiểm chứng trong môi trường này (documented, not runtime-verified)
- Điều hướng turn-by-turn, geocoding/autocomplete/directions API, và các style
  `night/satellite` phụ thuộc mạng + khoá hợp lệ; chỉ được mô tả theo mã nguồn, chưa chạy thực tế.

---

## 🔐 Bảo mật khoá API

Manifest của `demo/` hiện **hardcode một Google Maps API key** trong thẻ `meta-data`.
Đây là rủi ro lộ khoá.

- **Xoay (rotate) khoá** đã commit và gỡ khỏi lịch sử version control.
- Cấp khoá qua cơ chế **không commit**: đặt trong `local.properties` (đã có sẵn
  `secrets-gradle-plugin` trong dự án) rồi tham chiếu bằng placeholder trong manifest,
  ví dụ `android:value="${MAPS_API_KEY}"`.
- **Không** in giá trị khoá thật trong tài liệu hay commit.

---

**Made with ❤️ by MapVina Team**

*Last updated: 2026-07 — đồng bộ với `demo/` và kiểm chứng bằng build + emulator*
