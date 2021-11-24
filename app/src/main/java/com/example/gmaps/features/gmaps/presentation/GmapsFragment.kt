package com.example.gmaps.features.gmaps.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.gmaps.R
import com.example.gmaps.databinding.GmapsFragmentBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class GmapsFragment : Fragment(R.layout.gmaps_fragment), OnMapReadyCallback {

    private val binding: GmapsFragmentBinding by viewBinding(GmapsFragmentBinding::bind)
    private val viewModel: GmapsViewModel by viewModels()
    private lateinit var mapFragment: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapFragment = binding.mpMap
        mapFragment.onCreate(savedInstanceState)
        mapFragment.getMapAsync(this)
        initViews()
        observe()
    }

    /**
     * Получение GeoJson и расчет расстояния
     */
    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(42.0, 132.0)))
        viewModel.json.observe(viewLifecycleOwner, {
            val layer = GeoJsonLayer(googleMap, it)
            viewModel.calculatingDistance(layer)
            layer.addLayerToMap()
        })
    }

    /**
     * Работа с xml элементами
     */
    private fun initViews() {
        binding.tvDistance.setText(R.string.loading)
    }

    /**
     * Наблюдение за изменениями
     */
    @SuppressLint("SetTextI18n")
    private fun observe() {
        viewModel.distance.observe(viewLifecycleOwner, {
            val distance = it.sum()
            binding.tvDistance.text = getString(R.string.distance, distance)
        })
        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.tempLoading.isVisible = it
        }
    }

    override fun onResume() {
        mapFragment.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment.onLowMemory()
    }
}
