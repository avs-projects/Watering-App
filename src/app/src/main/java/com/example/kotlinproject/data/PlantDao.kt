package com.example.kotlinproject.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**

Dao creation allowing the management of the content of the database

 */

@Dao
interface PlantDao {

    // Recovery of all data, storage in alphabetical order
    @Query("SELECT * from plant ORDER BY firstName ASC")
    fun getPlants(): Flow<List<Plant>>

    /*
      Recovery of plants whose date is less than that of the day
      Allows you to trigger the sending of a notification if plants need to be watered
      LIMIT 1, in order not to browse all the plants that respect the condition -> decrease the load of the program
     */
    @Query("SELECT * FROM plant WHERE dateNextWatering <= date('now') OR dateNextNutrients <= date('now') LIMIT 1")
    fun getAnyPlants(): List<Plant>

    // Recovery of plants whose first or second name matches the one entered by the user
    @Query("SELECT * FROM plant WHERE firstName LIKE  '%' || :searchQuery || '%'OR secondName LIKE '%' ||  :searchQuery || '%' ")
    fun searchDatabase(searchQuery: String): Flow<List<Plant>>

    /*
     Recovery of plants whose first or second name matches the one entered by the user
     in the list of plants to water in the Watering section
     */
    @Query("SELECT * FROM plant WHERE (firstName LIKE  '%' || :searchQuery || '%'OR secondName LIKE '%' ||  :searchQuery || '%') AND (dateNextWatering <= date('now'))")
    fun searchDatabaseAlarmListWatering(searchQuery: String): Flow<List<Plant>>

    /*
     Recovery of plants whose first or second name matches the one entered by the user
     in the list of plants to water in the Nutrients section
    */
    @Query("SELECT * FROM plant WHERE (firstName LIKE  '%' || :searchQuery || '%'OR secondName LIKE '%' ||  :searchQuery || '%') AND (dateNextNutrients <= date('now'))")
    fun searchDatabaseAlarmListNutrients(searchQuery: String): Flow<List<Plant>>

    // Retrieve a specific plant via its id
    @Query("SELECT * from plant WHERE id = :id")
    fun getPlant(id: Int): Flow<Plant>

    // Inserting a plant
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: Plant)

    // Edit a plant
    @Update
    suspend fun update(plant: Plant)

    //Delete a plant
    @Delete
    suspend fun delete(plant: Plant)
}
