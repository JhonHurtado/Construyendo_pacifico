package com.example.proyectopacifico.data.localdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.proyectopacifico.data.localdb.populationEthnicGroup.PopulationEthnicGroupDao
import com.example.proyectopacifico.data.localdb.analysis.AnalysisDao
import com.example.proyectopacifico.data.localdb.answer.AnswerDao
import com.example.proyectopacifico.data.localdb.answerType.AnswerTypeDao
import com.example.proyectopacifico.data.localdb.department.DepartmentDao
import com.example.proyectopacifico.data.localdb.ethnicGroup.EthnicGroupDao
import com.example.proyectopacifico.data.localdb.feature.FeatureDao
import com.example.proyectopacifico.data.localdb.measure.MeasureDao
import com.example.proyectopacifico.data.localdb.moduleType.ModuleTypeDao
import com.example.proyectopacifico.data.localdb.municipality.MunicipalityDao
import com.example.proyectopacifico.data.localdb.optionQuestion.OptionQuestionDao
import com.example.proyectopacifico.data.localdb.parameter.ParameterDao
import com.example.proyectopacifico.data.localdb.place.PlaceDao
import com.example.proyectopacifico.data.localdb.populatedCenter.PopulatedCenterDao
import com.example.proyectopacifico.data.localdb.population.PopulationDao
import com.example.proyectopacifico.data.localdb.profile.ProfileDao
import com.example.proyectopacifico.data.localdb.question.QuestionDao
import com.example.proyectopacifico.data.localdb.questionType.QuestionTypeDao
import com.example.proyectopacifico.data.localdb.sample.SampleDao
import com.example.proyectopacifico.data.localdb.user.UserDao
import com.example.proyectopacifico.data.localdb.waterType.WaterTypeDao
import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity

@Database(
    entities = [
        AnalysisEntity::class,
        DepartmentEntity::class,
        EthnicGroupEntity::class,
        FeatureEntity::class,
        MeasureEntity::class,
        MunicipalityEntity::class,
        ParameterEntity::class,
        PopulatedCenterEntity::class,
        PopulationEntity::class,
        ProfileEntity::class,
        SampleEntity::class,
        UserEntity::class,
        WaterTypeEntity::class,
        PopulationEthnicGroupEntity::class,
        AnswerTypeEntity::class,
        ModuleTypeEntity::class,
        OptionQuestionEntity::class,
        QuestionEntity::class,
        QuestionTypeEntity::class,
        AnswersEntity::class,
        PlaceEntity::class,
               ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun AnalysisDao(): AnalysisDao
    abstract fun DepartmentDao(): DepartmentDao
    abstract fun EthnicGroupDao(): EthnicGroupDao
    abstract fun FeatureDao(): FeatureDao
    abstract fun MeasureDao(): MeasureDao
    abstract fun MunicipalityDao(): MunicipalityDao
    abstract fun ParameterDao(): ParameterDao
    abstract fun PopulatedCenterDao(): PopulatedCenterDao
    abstract fun PopulationDao(): PopulationDao
    abstract fun ProfileDao(): ProfileDao
    abstract fun SampleDao(): SampleDao
    abstract fun UserDao(): UserDao
    abstract fun WaterTypeDao(): WaterTypeDao
    abstract fun PopulationEthnicGroupDao(): PopulationEthnicGroupDao
    abstract fun AnswerTypeDao(): AnswerTypeDao
    abstract fun ModuleTypeDao(): ModuleTypeDao
    abstract fun OptionQuestionDao(): OptionQuestionDao
    abstract fun QuestionDao(): QuestionDao
    abstract fun QuestionTypeDao(): QuestionTypeDao
    abstract fun AnswerDao(): AnswerDao
    abstract fun PlaceDao(): PlaceDao

    companion object {

        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "pacifico_database"
            ).build()
            return INSTANCE!!
        }

    }
}