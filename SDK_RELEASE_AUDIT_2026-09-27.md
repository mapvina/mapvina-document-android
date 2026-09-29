# MapVina SDK: kiểm chứng phát hành và sample (27/09/2026)

Tài liệu này phân biệt **đã publish**, **build được** và **chạy được**. Kiểm tra
artifact công khai trên Maven Central, pub.dev, npm và GitHub; không suy ra trạng
thái release từ `VERSION` trong source hoặc cache Maven local.

## Phiên bản công khai

| Kênh | Artifact | Phiên bản mới nhất đã xác minh | Trạng thái |
| --- | --- | --- | --- |
| [Maven Central](https://repo.maven.apache.org/maven2/io/github/mapvina/) | `android-sdk`, `android-sdk-debug`, `android-sdk-opengl`, `android-sdk-opengl-debug`, `android-sdk-vulkan`, `android-sdk-vulkan-debug` | `1.0.2` | Cả sáu có AAR, POM, chữ ký `.asc`, checksum `.sha1` (HTTP 200). |
| Maven Central | `android-sdk-geojson`, `android-sdk-turf`, `mapvina-android-gestures` | `1.0.1` | GeoJSON/Turf `1.0.1` có package `io.github.mapvina.*` trong JAR công khai. |
| Maven Central | sáu `android-plugin-*-v9`, `android-sdk-ktx-v7`, `earcut4j`, `navigation/*`, `spatialk/*` | `1.0.0` | Chưa có `1.0.1` cho nhóm plugin/navigation; không áp cùng version core cho mọi module. |
| [GitHub Native](https://github.com/mapvina/mapvina-native/releases) / [SPM](https://github.com/mapvina/mapvina-gl-native-distribution/releases) | iOS XCFramework / Swift Package | `ios-v1.0.0` / `1.0.0` | Source `platform/ios/VERSION` là `1.0.1`, **chưa** có iOS release `1.0.1` công khai. |
| CocoaPods trunk | `MapVina` | chưa thấy pod | API trunk trả 404; không hướng dẫn `pod 'MapVina'` như bản đã publish. |
| [pub.dev](https://pub.dev/packages/mapvina_gl) | `mapvina_gl`, `_web`, `_platform_interface` | `1.0.1` | Đã publish, nhưng Android runtime còn lỗi API key (xem bên dưới). |
| [npm](https://www.npmjs.com/package/@mapvina-com/mapvina-react-native) | `@mapvina-com/mapvina-react-native` | `1.0.2` | Đã publish, nhưng Android RN CLI sample không compile từ package này (xem bên dưới). |

## Sample được chạy trong lượt kiểm tra này

| Sample | Build | Máy ảo và kết quả |
| --- | --- | --- |
| `demo/` | `./gradlew :app:assembleDebug --refresh-dependencies` thành công với Maven Central, không dùng `mavenLocal()` | Android API 35: bản đồ streets và logo MapVina ngang ở góc dưới trái hiển thị; `libmapvina.so` nạp. |
| `example/` | `./gradlew :app:assembleDebug` thành công sau khi nâng Kotlin và sửa package import/layout | Android API 35: bản đồ streets và logo MapVina ngang ở góc dưới trái hiển thị. |
| `../mapvina-document-ios-github/demo/` | `xcodebuild -workspace MapVinaSample.xcworkspace -scheme MapVinaSample -configuration Debug -sdk iphonesimulator ... build` thành công | iPhone 17 Pro simulator: bản đồ streets hiển thị, **nhưng logo ornament vẫn là pin tròn MAP VIỆT NAM**. `demo/libs/MapVina.xcframework` là symlink sang checkout local của distribution, nên ảnh này không chứng minh nội dung zip public `1.0.0`; SPM của project resolve tag `1.0.0`. Không được tuyên bố iOS logo mới đã phát hành. |
| `../mapvina-document-flutter-github/` | `flutter pub get` resolve ba gói `1.0.1`; `flutter build apk --debug` thành công sau khi bỏ `mavenLocal()` | Android API 35: native crash `You must provide API key for tile sources`; package Flutter `1.0.1` vẫn gọi `MapVina.getInstance(context)` và ghi đè API key thành null. |
| `../mapvina-document-reactnative-github/MapVina-react-native-app/` | `./gradlew :app:assembleDebug` thất bại tại `:mapvina-com_mapvina-react-native:compileDebugKotlin` | Chưa thể chạy sample trên emulator: npm package `1.0.2` kéo GeoJSON/Turf `1.0.0` sai namespace; plugin annotation `1.0.0` đóng gói `com.mapvina.android.plugins.annotation.*`, trong khi RN import `io.github.mapvina.android.plugins.*`. Nâng mỗi GeoJSON/Turf chưa đủ để sửa plugin. |
| `../MapVina-Android-Auto-Sample/` | Sửa alias Navigation sang artifact Maven Central `io.github.mapvina.navigation:navigation-ui-android:1.0.0`; `:app:assembleDebug` vẫn thất bại | Bản navigation kéo SpatialK metadata Kotlin `2.3.0`, trong khi sample dùng compiler `2.1.0`. Chưa chạy Android Auto/AAOS; cần xử lý toolchain và transitive metadata riêng. |

Sample iOS trong `../mapvina-navigation-android/sample/ios/` còn tham chiếu
`pod 'MapVina', '~> 1.0.0'` (pod không có trên trunk); xem README của sample.

Ảnh kiểm chứng ngày 27/09 chỉ được lưu tạm ở `/tmp` và đã mất sau khi đổi môi trường.
Xem phần kiểm chứng bổ sung ngày 29/09 ở cuối tài liệu cho ảnh và kết quả mới.
Chưa kiểm thử Expo, Flutter iOS,
React Native iOS, navigation, satellite, thiết bị thật hoặc mọi style/screen size.

## Tích hợp khuyến nghị và các điều kiện phát hành

1. Android thuần: thêm `google()` + `mavenCentral()`; dùng
   `implementation("io.github.mapvina:android-sdk:1.0.2")`. Nếu code gọi GeoJSON/Turf
   trực tiếp, khai báo `android-sdk-geojson:1.0.1` /
   `android-sdk-turf:1.0.1`. Chọn **một** core renderer; plugin annotation `1.0.0`
   vẫn cần xử lý dependency core cũ để tránh duplicate class (xem `demo/`).
2. iOS: dùng [SPM distribution `1.0.0`](https://github.com/mapvina/mapvina-gl-native-distribution)
   hoặc XCFramework từ GitHub `ios-v1.0.0`. Không hướng dẫn `1.0.1` cho tới khi
   XCFramework, checksum, release và Swift Package tag mới được xác minh cùng nhau.
   Logo mới trên iOS chưa có bằng chứng từ binary public; sample local còn pin cũ.
3. Flutter: pub.dev có `mapvina_gl: 1.0.1`, nhưng **chưa có luồng Android chạy
   thành công từ package công khai**. Cần sửa API key trong upstream, build +
   chạy sample sạch và phát hành bản mới trước khi khuyến nghị production.
4. React Native: npm `^1.0.2` đúng version hiện có, **chưa có Android sample build
   sạch**. Cần đồng bộ namespace plugin, kiểm classpath Android với Java/Turf/
   Gestures `1.0.1`, build RN CLI + Expo, chạy map và chỉ sau đó publish npm mới.
5. Các repo wrapper/build (`flutter-mapvina-gl`, `mapvina-react-native`,
   `mapvina-navigation-android`, `mapvina-plugins-android`) đã được chỉnh pin
   GeoJSON/Turf/Gestures theo Maven Central **trong source local**; thay đổi này
   không sửa binary đã publish trên pub.dev/npm/Maven. Kiểm CI và phát hành riêng.

Kiểm tra lại version Maven bằng `maven-metadata.xml` tại repository công khai;
build với Maven Central trước Maven local, tránh cache che giấu lỗi. Chỉ ghi
"logo đúng" khi có ảnh runtime **từ đúng binary/version phát hành**.

## Kiểm chứng logo bổ sung (29/09/2026)

- Hai SVG master trong Brand Kit v1.0 khớp byte với `mapvina-native/platform/branding/source/`.
  Resource `mapvina_logo_icon.png` của AAR public `android-sdk:1.0.2` khớp byte
  với source Android hiện tại: logo ngang xanh navy/xanh lá, nền trong suốt.
- `demo/` và `example/` Android build thành công bằng Maven Central. Chạy lại
  `demo/` trên Pixel 9 API 35 ở tọa độ TP.HCM: streets render, logo bản đồ
  nằm bottom-start, logo ngôi sao trên header. Ảnh lưu tại
  [`demo/emulator_logo_20260929.png`](demo/emulator_logo_20260929.png).
- Giải nén ZIP XCFramework public `ios-v1.0.0`, checksum
  `931b726a76d7b79c241a8fac70702f9da0ec109a7ff01f01a403c50e9ff4f156`:
  `Assets.car` còn `mapbox_helmet` và `mapvina-logo-icon`, chưa có asset
  `mapvina-logo-horizontal-primary`. Release GitHub/SPM vẫn chỉ là `1.0.0`.
- XCFramework build **local** từ `mapvina-native/main` có asset
  `mapvina-logo-horizontal-primary`, `mapvina-icon-primary`. Xcode build sample
  với `MAPVINA_NATIVE_PACKAGE_PATH` trỏ đến package binary local thành công;
  iPhone 16 iOS 18.6 render streets, logo ngang bottom-start và icon header.
  Ảnh lưu tại
  [`../mapvina-document-ios-github/demo/simulator_logo_local_20260929.png`](../mapvina-document-ios-github/demo/simulator_logo_local_20260929.png).
  Đây **không phải** bản iOS công khai; sample mặc định vẫn ghim SPM `1.0.0`.
- Flutter `1.0.1` vẫn lỗi API key tại runtime; RN npm `1.0.2` còn lỗi compile
  namespace plugin; Android Auto còn xung đột Kotlin metadata. Chưa công bố
  các integration này là đã chạy được chỉ vì core Android có logo đúng.
