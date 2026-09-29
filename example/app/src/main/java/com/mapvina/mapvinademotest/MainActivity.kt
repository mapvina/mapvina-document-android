package com.mapvina.mapvinademotest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.mapvina.android.MapVina
import io.github.mapvina.android.WellKnownTileServer
import io.github.mapvina.android.camera.CameraUpdateFactory
import io.github.mapvina.android.geometry.LatLng
import io.github.mapvina.android.maps.Style
import io.github.mapvina.android.maps.MapVinaMap
import com.mapvina.mapvinademotest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var styleUrl = "https://maps.mapvina.com/styles/v2/streets.json?key=public_key"
    private lateinit var mapvinaMap: MapVinaMap
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapVina.getInstance(this, "public", WellKnownTileServer.MapVina)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMap()
    }

    private fun initMap() {
        binding.mapTrack.getMapAsync { map ->
            this.mapvinaMap = map
            map.setStyle(Style.Builder().fromUri(styleUrl))
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng(10.7769, 106.7009), 12.0)
            map.moveCamera(cameraUpdate)
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mapTrack.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapTrack.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapTrack.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapTrack.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapTrack.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapTrack.onLowMemory()
    }
}
