package com.example.kotlinproject.alarm.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.data.PlantDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**

Classic Irrigation ViewModel

 */

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)

class PlantAlarmWateringViewModel(private val plantDao: PlantDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val plantFlow = searchQuery.flatMapLatest {
        plantDao.searchDatabaseAlarmListWatering(it)
    }

    val plants = plantFlow.asLiveData()

    /**
     * Plant update function
     */
    fun updatePlant(
        plantId: Int,
        firstName: String,
        secondName: String,
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastWatering: String,
        dateLastNutrients: String,
        dateNextWatering: String,
        dateNextNutrients: String,
        plantImage: String,
    ) {
        val updatedPlant = getUpdatedPlantEntry(
            plantId,
            firstName,
            secondName,
            frequencyWatering1,
            frequencyWatering2,
            dateLastWatering,
            dateLastNutrients,
            dateNextWatering,
            dateNextNutrients,
            plantImage
        )
        updatePlant(updatedPlant)
    }

    /**
     * Function to retrieve an instance of Plant with updated information
     */
    private fun getUpdatedPlantEntry(
        plantId: Int,
        firstName: String,
        secondName: String,
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastWatering: String,
        dateLastNutrients: String,
        dateNextWatering: String,
        dateNextNutrients: String,
        plantImage: String,
    ): Plant {
        return Plant(
            id = plantId,
            firstName = firstName,
            secondName = secondName,
            frequencyWatering1 = frequencyWatering1,
            frequencyWatering2 = frequencyWatering2,
            dateLastWatering = dateLastWatering,
            dateLastNutrients = dateLastNutrients,
            dateNextWatering = dateNextWatering, // HERE
            dateNextNutrients = dateNextNutrients,
            plantPhoto = plantImage,
        )
    }

    /**
     * Launch Dao to refresh the plant
     */
    private fun updatePlant(plant: Plant) {
        viewModelScope.launch {
            plantDao.update(plant)
        }
    }

    /**
     * Recovery of a plant
     */
    fun retrievePlant(id: Int): LiveData<Plant> {
        return plantDao.getPlant(id).asLiveData()
    }
}

/**
 * Factory to instantiate the ViewModel instance
 */
class PlantAlarmWateringViewModelFactory(private val plantDao: PlantDao) :
    ViewModelProvider.Factory {
    @ExperimentalCoroutinesApi
    @RequiresApi(Build.VERSION_CODES.O)
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantAlarmWateringViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantAlarmWateringViewModel(plantDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
