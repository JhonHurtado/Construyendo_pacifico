package com.example.proyectopacifico.data.localdb.populationEthnicGroup

import androidx.room.*
import com.example.proyectopacifico.data.models.entities.PopulationEthnicGroupEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndEthnicGroups

@Dao
interface PopulationEthnicGroupDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun savePopulationEthnicGroup(populationEthnicGroupEntity: PopulationEthnicGroupEntity):Long

    @Transaction
    @Query("select * from populationethnicgroupentity as p join ethnicgroupentity as e on p.ethnic_group_id = e.id_ethnic_group where population_id = :id_population")
    suspend fun getEthnicGroupsByPopulationId(id_population:Int):List<PopulationAndEthnicGroups>

    @Query("delete from populationethnicgroupentity where population_id = :id_population")
    suspend fun deleteEthnicGroupsByPopulationId(id_population:Int):Int

}