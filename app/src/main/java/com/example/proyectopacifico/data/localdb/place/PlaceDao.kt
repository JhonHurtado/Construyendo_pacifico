package com.example.proyectopacifico.data.localdb.place

import androidx.room.*
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment

@Dao
interface PlaceDao {
    @Query("select * from placeentity as pl join populationentity as po on pl.population_id = po.id_population join populatedcenterentity as pc on po.populated_center_id = pc.id_populated_center join municipalityentity as m on pc.municipality_id = m.id_municipality join departmententity as d on m.department_id = d.id_department where pl.population_id = :idPopulation")
    suspend fun getPlacesByIdPopulation(idPopulation:Int): List<PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment>

    @Query("SELECT * FROM placeentity")
    suspend fun getAllPlaces(): List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun savePlace(placeEntity: PlaceEntity): Long

    @Update
    suspend fun updatePlace(placeEntity: PlaceEntity): Int

    @Delete
    suspend fun deletePlace(placeEntity: PlaceEntity): Int
}