package com.example.proyectopacifico.data.localdb.population

import androidx.room.*
import androidx.room.OnConflictStrategy.ABORT
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment

@Dao
interface PopulationDao {
    @Query("select * from populationentity as p join populatedcenterentity as pc on p.populated_center_id = pc.id_populated_center join municipalityentity as m on pc.municipality_id = m.id_municipality join departmententity as d on m.department_id = d.id_department ")
    suspend fun getPopulations():List<PopulationAndPopulatedCenterAndMunicipalityAndDepartment>

    @Query("SELECT * FROM populationentity WHERE populated_center_id = :id_populated_center")
    suspend fun getPopulationByPopulatedCenter(id_populated_center:Int): PopulationEntity

    @Query("SELECT * FROM populationentity")
    suspend fun getAllPopulations(): List<PopulationEntity>

    @Insert(onConflict = ABORT)
    suspend fun savePopulation(populationsEntity: PopulationEntity):Long

    @Update
    suspend fun updatePopulation(populationsEntity: PopulationEntity):Int

    @Delete
    suspend fun deletePopulation(populationsEntity: PopulationEntity):Int
}