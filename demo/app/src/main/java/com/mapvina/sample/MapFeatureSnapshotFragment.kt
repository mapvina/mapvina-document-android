package com.mapvina.sample

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.mapvina.android.MapVina
import com.mapvina.android.camera.CameraPosition
import com.mapvina.android.location.permissions.PermissionsListener
import com.mapvina.android.location.permissions.PermissionsManager
import com.mapvina.android.maps.MapView
import com.mapvina.android.maps.Style
import com.mapvina.android.maps.MapVinaMap
import com.mapvina.android.style.layers.LineLayer
import com.mapvina.android.style.layers.Property
import com.mapvina.android.style.layers.PropertyFactory
import com.mapvina.android.style.sources.GeoJsonSource
import com.mapvina.geojson.Feature
import com.mapvina.geojson.LineString
import com.mapvina.geojson.Point
import com.mapvina.sample.databinding.FragmentFeatureSnapshotBinding
import com.mapvina.sample.utils.MapUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import com.google.android.gms.maps.model.LatLng as GoogleLatLng
import com.mapvina.android.geometry.LatLng as MapVinaLatLng


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MapFeatureSnapshotFragment : Fragment(), PermissionsListener, View.OnClickListener {

    private var _binding: FragmentFeatureSnapshotBinding? = null
    private lateinit var mapvinaMap: MapVinaMap
    private lateinit var googleMap: GoogleMap

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding
    private var permissionsManager: PermissionsManager = PermissionsManager(this)

    private var styleUrl = "https://maps.map-vina.com/styles/v1/streets.json?key=public"
    private var latLngLocation: MapVinaLatLng? = MapVinaLatLng(10.728073, 106.624054)
    private var zoomLocation: Double = 10.0
    private var idCountry: String? = "vn"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentContext: Context? = context
        fragmentContext?.let {
            sharedPreferences = it.getSharedPreferences("mapvina", Context.MODE_PRIVATE)
            idCountry = sharedPreferences.getString("country", "vn")
            styleUrl = MapUtils(requireActivity()).urlStyle(idCountry)
            latLngLocation = MapUtils(requireActivity()).getLatlng(idCountry)
            zoomLocation = MapUtils(requireActivity()).zoom(idCountry)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        MapVina.getInstance(requireActivity())
        _binding = FragmentFeatureSnapshotBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.mapView2?.onCreate(savedInstanceState)
        initMap()
        initControl()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapView1) as? SupportMapFragment
        mapFragment?.getMapAsync { map ->
            googleMap = map
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
            var lat = 10.8516484
            var lng = 106.7086147

            val initialPosition = LatLng(lat, lng)
            googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(initialPosition, 10.0F)
            )

            drawRoute(
                origin = initialPosition,
                destination = LatLng(10.8516484, 106.863546)
            )

            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()
                drawRoute(
                    origin = initialPosition,
                    destination = LatLng(10.8516484, 106.863546)
                )
            }
        }

        binding?.mapView2?.getMapAsync { map ->
            Log.d("DOMAIN URL STYLE:", styleUrl)
            this.mapvinaMap = map
            map.setStyle(
                Style.Builder().fromUri(styleUrl)
            ) { style ->
                if (activity != null) {
                    MapUtils(requireActivity()).enableLocationComponent(
                        style,
                        idCountry,
                        mapvinaMap,
                        permissionsManager,
                        latLngLocation!!, zoomLocation
                    )
                }

                // Add click listener for MapVinaSample
                mapvinaMap.addOnMapClickListener { point ->
                    // Clear previous route
                    style.removeLayer("route-layer")
                    style.removeSource("route-source")

                    val origin = MapVinaLatLng(10.8516484, 106.7086147)
                    drawMapVinaRoute(origin, point)
                    true
                }
            }
            cameraAnimation(latLngLocation!!)
            binding?.mapView2?.addOnDidFinishRenderingFrameListener(idleListener)
        }

        binding?.locationMy?.setOnClickListener {
            val userLocation = mapvinaMap.locationComponent.lastKnownLocation
            if (userLocation != null) {
                mapvinaMap.cameraPosition =
                    CameraPosition.Builder()
                        .target(MapVinaLatLng(userLocation.latitude, userLocation.longitude))
                        .zoom(zoomLocation)
                        .build()
            }
        }
    }

    private fun cameraAnimation(point: MapVinaLatLng) {
        try {
            val cameraPosition = CameraPosition.Builder().target(point).zoom(zoomLocation).build()
            val cameraUpdate =
                com.mapvina.android.camera.CameraUpdateFactory.newCameraPosition(cameraPosition)
            mapvinaMap.animateCamera(cameraUpdate, 1000)
        } catch (e: Exception) {
            Log.d("ERROR CAMERA", e.toString())
        }
    }

    private fun initControl() {
        binding?.btnBack?.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {

    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapvinaMap.getStyle { style ->
                if (activity != null) {
                    MapUtils(requireActivity()).enableLocationComponent(
                        style,
                        idCountry,
                        mapvinaMap,
                        permissionsManager,
                        latLngLocation!!, zoomLocation
                    )
                }
            }
        } else {
            // Xử lý khi quyền truy cập vị trí không được cấp
        }
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private val idleListener = object : MapView.OnDidFinishRenderingFrameListener {
        override fun onDidFinishRenderingFrame(fully: Boolean, p1: Double, p2: Double) {
            try {
                if (fully) {
                    binding?.mapView2?.removeOnDidFinishRenderingFrameListener(this)
                    mapvinaMap.snapshot { snapshot ->
                        val targetLatLng: MapVinaLatLng? = mapvinaMap.cameraPosition.target
                        val zoomLevel: Double = mapvinaMap.cameraPosition.zoom
                        if (targetLatLng != null) {
                            googleMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    GoogleLatLng(
                                        targetLatLng.latitude,
                                        targetLatLng.longitude
                                    ),
                                    (zoomLevel + 1).toFloat()
                                )
                            )
                        }
                        binding?.mapView2?.addOnDidFinishRenderingFrameListener(this)
                    }
                }
            } catch (e: Exception) {

            }
        }
    }

    private fun drawRoute(origin: GoogleLatLng, destination: GoogleLatLng) {
        val client = OkHttpClient()

        val url = "https://dev.maps.map-vina.com/route/v1/directions/json?" +
                "mode=driving&" +
                "origin=${origin.longitude},${origin.latitude}&" +
                "destination=${destination.longitude},${destination.latitude}"

        Log.d("Route URL", url)

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Route Error", "Failed to get route: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonStr ->
                    try {
                        val jsonResponse = JSONObject(jsonStr)
                        val routes = jsonResponse.getJSONArray("routes")
                        if (routes.length() > 0) {
                            val route = routes.getJSONObject(0)
                            val geometry = route.getString("geometry")

                            // Decode polyline and draw on map
                            activity?.runOnUiThread {
                                val decodedPath = decodePolyline(geometry)
                                val polylineOptions = PolylineOptions()
                                    .addAll(decodedPath)
                                    .width(12f)
                                    .color(Color.parseColor("#009688"))
                                googleMap.addPolyline(polylineOptions)
                            }
                        } else {
                            print("routes < 0")
                        }
                    } catch (e: Exception) {
                        Log.e("JSON Parse Error", "Failed to parse route response: ${e.message}")
                    }
                }
            }
        })
    }

    private fun decodePolyline(encoded: String): List<GoogleLatLng> {
        val poly = ArrayList<GoogleLatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = GoogleLatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

    private fun drawMapVinaRoute(origin: MapVinaLatLng, destination: MapVinaLatLng) {
        val client = OkHttpClient()

        val url = "https://dev.maps.map-vina.com/route/v1/directions/json?" +
                "mode=driving&" +
                "origin=${origin.longitude},${origin.latitude}&" +
                "destination=${destination.longitude},${destination.latitude}"

        Log.d("MapVina Route URL", url)

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Route Error", "Failed to get route: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.string()?.let { jsonStr ->
                    try {
                        val jsonResponse = JSONObject(jsonStr)
                        val routes = jsonResponse.getJSONArray("routes")
                        if (routes.length() > 0) {
                            val route = routes.getJSONObject(0)
                            val geometry = route.getString("geometry")

                            activity?.runOnUiThread {
                                // Draw route on MapVinaSample
                                mapvinaMap.getStyle { style ->
                                    val coordinates = decodeMapVinaPolyline(geometry)
                                    val lineString = LineString.fromLngLats(coordinates)
                                    val feature = Feature.fromGeometry(lineString)

                                    style.addSource(GeoJsonSource("route-source", feature))

                                    val routeLayer = LineLayer("route-layer", "route-source")
                                    routeLayer.setProperties(
                                        PropertyFactory.lineColor(Color.parseColor("#009688")),
                                        PropertyFactory.lineWidth(6f),
                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND)
                                    )

                                    style.addLayer(routeLayer)
                                }
                            }
                        } else {

                        }
                    } catch (e: Exception) {
                        Log.e("JSON Parse Error", "Failed to parse route response: ${e.message}")
                    }
                }
            }
        })
    }

    private fun decodeMapVinaPolyline(encoded: String): List<Point> {
        val poly = ArrayList<Point>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = Point.fromLngLat(
                lng.toDouble() / 1E5,
                lat.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

}