package com.example.kotlinproject.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**

Plant table which contains useful information for the program

 */

@Entity
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "firstName") // First name of the plant
    val firstName: String,
    @ColumnInfo(name = "secondName") // Optional second plant name
    val secondName: String,
    @ColumnInfo(name = "frequencyWatering1") // Corresponds to a season
    val frequencyWatering1: Int,
    @ColumnInfo(name = "frequencyWatering2") // Corresponds to a season
    val frequencyWatering2: Int,
    @ColumnInfo(name = "dateLastWatering") // Date of last regular watering of the plant
    val dateLastWatering: String,
    @ColumnInfo(name = "dateLastNutrients") // Date of last plant nutrient watering
    val dateLastNutrients: String,
    @ColumnInfo(name = "dateNextWatering") // Date of next regular watering of the plant
    val dateNextWatering: String,
    @ColumnInfo(name = "dateNextNutrients") // Next watering date plant nutrients
    val dateNextNutrients: String,
    @ColumnInfo(name = "plantPhoto") // The photo of the plant
    val plantPhoto: String
)