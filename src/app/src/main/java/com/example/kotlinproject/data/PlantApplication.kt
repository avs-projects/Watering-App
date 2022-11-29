package com.example.kotlinproject.data

import android.app.Application

/**

Allows simplified call of the database in the program

 */

class PlantApplication : Application() {
    val database: PlantRoomDatabase by lazy { PlantRoomDatabase.getDatabase(this) }
}