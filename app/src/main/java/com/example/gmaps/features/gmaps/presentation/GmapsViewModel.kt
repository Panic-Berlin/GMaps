package com.example.gmaps.features.gmaps.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gmaps.utils.asLiveData
import com.google.maps.android.SphericalUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class GmapsViewModel @Inject constructor() : ViewModel() {

    var json = MutableLiveData<JSONObject>()
    val distance = MutableLiveData<List<Int>>()
    private val _isLoading = MutableLiveData(true)
    val isLoading = _isLoading.asLiveData()

    init {
        loadJson()
    }

    /**
     *  Получение координате из бэка
     */
    private fun loadJson() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = URL("https://waadsu.com/api/russia.geo.json").readText()
            json.postValue(JSONObject(result))
        }
    }

    /**
     * Вычисление дистанции
     */
    fun calculatingDistance(layer: GeoJsonLayer) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val sumDistance = mutableListOf<Int>()
            for (feature in layer.features) {
                var polygonSize = 0
                val polygons = feature.geometry as GeoJsonMultiPolygon
                //Цикл прохождения полигонов
                while (polygonSize < polygons.polygons.size) {
                    val polygon = polygons.polygons[polygonSize]
                    val coordinates = polygon.coordinates[0]
                    var coordinateSize = 1
                    var sumCoordinates = 0.0
                    //Вычисление дистанции по координатам
                    while (coordinateSize < coordinates.size) {
                        sumCoordinates += SphericalUtil.computeDistanceBetween(
                            coordinates[coordinateSize - 1],
                            coordinates[coordinateSize]
                        )
                        coordinateSize++
                    }
                    polygonSize++
                    sumDistance.add(sumCoordinates.toInt())
                    distance.postValue(sumDistance)
                }
            }
        }
        _isLoading.value = false
    }
}
