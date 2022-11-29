package com.example.kotlinproject.list.viewmodel

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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import kotlin.properties.Delegates

/**

ViewModel of all the plants in the database

 */

@ExperimentalCoroutinesApi
@RequiresApi(Build.VERSION_CODES.O)

class PlantViewModel(private val plantDao: PlantDao) : ViewModel() {

    var frequence by Delegates.notNull<Int>()

    val searchQuery = MutableStateFlow("")

    private val plantFlow = searchQuery.flatMapLatest {
        plantDao.searchDatabase(it)
    }

    val plants = plantFlow.asLiveData()

    /**
     * Function to create the next classic watering date according to the frequency
     */
    private fun setDateNextWatering(
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastWatering: String,
    ): String {

        // Recovery of the month
        val monthLocal = LocalDateTime.now().monthValue

        // Use of frequencies (Seasons)
        frequence =
            if (monthLocal in 1..6) { // If the month is between January and June take frequency 1
                frequencyWatering1
            } else { // else take frequency 2
                frequencyWatering2
            }

        // Formatting the date in a particular form
        val dateList: List<String> = dateLastWatering.split("-")
        val day = dateList[2].toInt()
        val month = dateList[1].toInt()
        val year = dateList[0].toInt()

        // Incrementing the current date with the frequency value
        val period = Period.ofDays(frequence)
        val date = LocalDate.of(year, month, day)
        val modifiedDate = date.plus(period)

        return modifiedDate.toString()
    }

    /**
     * Function creation date next watering nutrients to the next day
     */
    private fun setDateNextNutrients(
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastNutrients: String,
    ): String {

        // Recovery of the month
        val monthLocal = LocalDateTime.now().monthValue

        // Utilisation des frequences (Saisons)
        frequence =
            if (monthLocal in 1..6) { // If the month is between January and June take frequency 1
                frequencyWatering1 * 3 // x3 to respect the topic regarding nutrient watering
            } else { // Else take frequency 2
                frequencyWatering2 * 3
            }

        // Formatting the date in a particular form
        val dateList: List<String> = dateLastNutrients.split("-")
        val day = dateList[2].toInt()
        val month = dateList[1].toInt()
        val year = dateList[0].toInt()

        // Incrementing the current date with the frequency value
        val period = Period.ofDays(frequence)
        val date = LocalDate.of(year, month, day)
        val modifiedDate = date.plus(period)

        return modifiedDate.toString()
    }

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
            dateNextWatering = setDateNextWatering(
                frequencyWatering1,
                frequencyWatering2,
                dateLastWatering
            ),
            dateNextNutrients = setDateNextNutrients(
                frequencyWatering1,
                frequencyWatering2,
                dateLastNutrients
            ),
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
     * Plant insertion function
     */
    fun addNewPlant(
        firstName: String,
        secondName: String,
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastWatering: String,
        dateLastNutrients: String,
        plantImage: String,
    ) {
        val newPlant = getNewPlantEntry(
            firstName,
            secondName,
            frequencyWatering1,
            frequencyWatering2,
            dateLastWatering,
            dateLastNutrients,
            plantImage
        )
        insertPlant(newPlant)
    }

    /**
     * Function to return an instance of Plant
     */
    private fun getNewPlantEntry(
        firstName: String,
        secondName: String,
        frequencyWatering1: Int,
        frequencyWatering2: Int,
        dateLastWatering: String,
        dateLastNutrients: String,
        plantImage: String,
    ): Plant {
        return Plant(
            firstName = firstName,
            secondName = secondName,
            frequencyWatering1 = frequencyWatering1,
            frequencyWatering2 = frequencyWatering2,
            dateLastWatering = dateLastWatering,
            dateLastNutrients = dateLastNutrients,
            dateNextWatering = setDateNextWatering(
                frequencyWatering1,
                frequencyWatering2,
                dateLastWatering
            ),
            dateNextNutrients = setDateNextNutrients(
                frequencyWatering1,
                frequencyWatering2,
                dateLastNutrients
            ),
            plantPhoto = plantImage,
        )
    }

    /**
     * Launch adding plant with Dao
     */
    private fun insertPlant(plant: Plant) {
        viewModelScope.launch {
            plantDao.insert(plant)
        }
    }

    /**
     * Launch adding plant with Dao
     */
    fun deletePlant(plant: Plant) {
        viewModelScope.launch {
            plantDao.delete(plant)
        }
    }

    /**
     * Recovery of a plant
     */
    fun retrievePlant(id: Int): LiveData<Plant> {
        return plantDao.getPlant(id).asLiveData()
    }

    /**
     * Verification of the content of edit text in order to validate their addition to the database
     */
    fun isEntryValid(
        firstName: String,
        frequencyWatering1: String,
        frequencyWatering2: String,
        dateLastWatering: String,
        dateLastNutrients: String,
        image: String,
    ): Boolean {
        if (firstName.isBlank() || frequencyWatering1.isBlank() || frequencyWatering2.isBlank() || dateLastWatering.isBlank() || dateLastNutrients.isBlank()
            || image.isBlank()
        ) {
            return false
        }
        return true
    }
}

/**
 * Factory to instantiate the ViewModel instance
 */
class PlantViewModelFactory(private val plantDao: PlantDao) : ViewModelProvider.Factory {
    @RequiresApi(Build.VERSION_CODES.O)
    @ExperimentalCoroutinesApi
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlantViewModel(plantDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
