package com.example.proyectopacifico

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.domain.department.DepartmentRepoImpl
import com.example.proyectopacifico.domain.ethnicGroup.EthnicGroupRepoImpl
import com.example.proyectopacifico.domain.feature.FeatureRepoImpl
import com.example.proyectopacifico.domain.municipality.MunicipalityRepoImpl
import com.example.proyectopacifico.domain.parameter.ParameterRepoImpl
import com.example.proyectopacifico.domain.populatedCenter.PopulatedCenterRepoImpl
import com.example.proyectopacifico.domain.profile.ProfileRepoImpl
import com.example.proyectopacifico.domain.user.UserRepoImpl
import com.example.proyectopacifico.domain.waterType.WaterTypeRepoImpl
import com.example.proyectopacifico.presentation.*
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerTypeEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.OptionQuestionEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.QuestionTypeEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionEntity
import com.example.proyectopacifico.databinding.ActivitySplashBinding
import com.example.proyectopacifico.domain.answerType.AnswerTypeRepoImpl
import com.example.proyectopacifico.domain.moduleType.ModuleTypeRepoImpl
import com.example.proyectopacifico.domain.optionQuestion.OptionQuestionRepoImpl
import com.example.proyectopacifico.domain.question.QuestionRepoImpl
import com.example.proyectopacifico.domain.questionType.QuestionTypeRepoImpl
import com.google.android.material.snackbar.Snackbar
import java.io.BufferedReader
import java.io.InputStreamReader

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding


    private val viewModelDepartment by viewModels<DepartmentViewModel> {
        DepartmentViewModelFactory(
            DepartmentRepoImpl(
                AppDatabase.getDatabase(applicationContext).DepartmentDao()
            )
        )
    }
    private val viewModelMunicipality by viewModels<MunicipalityViewModel> {
        MunicipalityViewModelFactory(
            MunicipalityRepoImpl(
                AppDatabase.getDatabase(applicationContext).MunicipalityDao()
            )
        )
    }
    private val viewModelPopulatedCenter by viewModels<PopulatedCenterViewModel> {
        PopulatedCenterViewModelFactory(
            PopulatedCenterRepoImpl(
                AppDatabase.getDatabase(applicationContext).PopulatedCenterDao()
            )
        )
    }
    private val viewModelEthnicGroup by viewModels<EthnicGroupViewModel> {
        EthnicGroupViewModelFactory(
            EthnicGroupRepoImpl(
                AppDatabase.getDatabase(applicationContext).EthnicGroupDao()
            )
        )
    }
    private val viewModelProfiles by viewModels<ProfileViewModel> {
        ProfileViewModelFactory(
            ProfileRepoImpl(
                AppDatabase.getDatabase(applicationContext).ProfileDao()
            )
        )
    }
    private val viewModelUsers by viewModels<UserViewModel> {
        UserViewModelFactory(
            UserRepoImpl(
                AppDatabase.getDatabase(applicationContext).UserDao()
            )
        )
    }
    private val viewModelWaterType by viewModels<WaterTypeViewModel> {
        WaterTypeViewModelFactory(
            WaterTypeRepoImpl(
                AppDatabase.getDatabase(applicationContext).WaterTypeDao()
            )
        )
    }
    private val viewModelFeatures by viewModels<FeatureViewModel> {
        FeatureViewModelFactory(
            FeatureRepoImpl(
                AppDatabase.getDatabase(applicationContext).FeatureDao()
            )
        )
    }
    private val viewModelParameters by viewModels<ParameterViewModel> {
        ParameterViewModelFactory(
            ParameterRepoImpl(
                AppDatabase.getDatabase(applicationContext).ParameterDao()
            )
        )
    }
    private val viewModelAnswerType by viewModels<AnswerTypeViewModel> {
        AnswerTypeViewModelFactory(
            AnswerTypeRepoImpl(
                AppDatabase.getDatabase(applicationContext).AnswerTypeDao()
            )
        )
    }
    private val viewModelModuleType by viewModels<ModuleTypeViewModel> {
        ModuleTypeViewModelFactory(
            ModuleTypeRepoImpl(
                AppDatabase.getDatabase(applicationContext).ModuleTypeDao()
            )
        )
    }
    private val viewModelOptionQuestion by viewModels<OptionQuestionViewModel> {
        OptionQuestionViewModelFactory(
            OptionQuestionRepoImpl(
                AppDatabase.getDatabase(applicationContext).OptionQuestionDao()
            )
        )
    }
    private val viewModelQuestion by viewModels<QuestionViewModel> {
        QuestionViewModelFactory(
            QuestionRepoImpl(
                AppDatabase.getDatabase(applicationContext).QuestionDao()
            )
        )
    }
    private val viewModelQuestionType by viewModels<QuestionTypeViewModel> {
        QuestionTypeViewModelFactory(
            QuestionTypeRepoImpl(
                AppDatabase.getDatabase(applicationContext).QuestionTypeDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()



        val sharedPreferences = getSharedPreferences(Constants.SPLASH_KEY,Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean(Constants.SPLASH_FIRST_TIME,false)){
            saveCsv()
            with(sharedPreferences.edit()){
                putBoolean(Constants.SPLASH_FIRST_TIME,true)
                apply()
            }
            Snackbar.make(binding.root,"Estamos guardado informacion, porfavor esperar a que se complete",Snackbar.LENGTH_LONG).show()
        } else{
            startTimer(2000)
        }

    }

    private fun saveCsv() {
        saveCsvParameters()
        saveCsvDepartments()
        saveCsvMunicipalities()
        saveCsvPopulatedCenters()
        saveCsvEthnicGroup()
        saveCsvProfiles()
        saveCsvUsers()
        saveCsvWaterType()
        saveCsvFeatures()
        saveCsvAnswerType()
        saveCsvModuleTypes()
        saveCsvOptionQuestion()
        saveCsvQuestion()
        saveCsvQuestionType()


        startTimer(20000)
    }

    private fun saveCsvParameters() {

        val inputReader = InputStreamReader(resources.assets.open("parameters.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<ParameterEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    ParameterEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelParameters.saveParameter(
                        ParameterEntity(
                            r.id_parameter,
                            r.feature_id,
                            r.parameter_name,
                            r.units,
                            r.expected_value,
                            r.operator,
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveParameter: ",
                                    "guardado local bien param"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorParameter: ",
                                    "Error al guardar info local param"
                                )
                            }
                        }
                    }


                }
            }
        }

    }

    private fun saveCsvDepartments() {
        val inputReader = InputStreamReader(resources.assets.open("departments.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<DepartmentEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    DepartmentEntity(
                        row[0].toInt(),
                        row[1],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelDepartment.saveDepartment(
                        DepartmentEntity(
                            r.id_department.toInt(),
                            r.department_name
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveDepartments: ",
                                    "guardado local bien dep"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorDepartments: ",
                                    "Error al guardar info local dep"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvMunicipalities() {
        val inputReader = InputStreamReader(resources.assets.open("municipalities.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<MunicipalityEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    MunicipalityEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2]
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }
        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelMunicipality.saveDepartment(
                        MunicipalityEntity(
                            r.id_municipality,
                            r.department_id,
                            r.municipality_name
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveMunicipality: ",
                                    "guardado local bien muni"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveMunicipality: ",
                                    "Error al guardar info local muni"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvPopulatedCenters() {
        val inputReader = InputStreamReader(resources.assets.open("populated_centers.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<PopulatedCenterEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    PopulatedCenterEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2],
                        row[3]
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelPopulatedCenter.savePopulatedCenter(
                        PopulatedCenterEntity(
                            r.id_populated_center,
                            r.municipality_id,
                            r.populated_center_name,
                            r.populated_center_type
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "savePopulatedCenters: ",
                                    "guardado local bien popu"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "savePopulatedCenters: ",
                                    "Error al guardar info local popu"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvEthnicGroup() {
        val inputReader = InputStreamReader(resources.assets.open("grupos_etnicos.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<EthnicGroupEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    EthnicGroupEntity(
                        row[0].toInt(),
                        row[1]
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelEthnicGroup.saveEthnicGroup(
                        EthnicGroupEntity(
                            r.id_ethnic_group.toInt(),
                            r.ethnic_group_name
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveEthnicGroup: ",
                                    "guardado local bien ethn"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveEthnicGroup: ",
                                    "Error al guardar info local ethn"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvProfiles() {
        val inputReader = InputStreamReader(resources.assets.open("profiles.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<ProfileEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    ProfileEntity(
                        row[0].toInt(),
                        row[1]
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelProfiles.saveProfile(
                        ProfileEntity(
                            r.id_profile,
                            r.name_profile
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveProfiles: ",
                                    "guardado local bien prof"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveProfiles: ",
                                    "Error al guardar info local prof"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvUsers() {
        val inputReader = InputStreamReader(resources.assets.open("users.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<UserEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    UserEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        row[8],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelUsers.saveUser(
                        UserEntity(
                            r.id_user,
                            r.profile_id,
                            r.user_nick,
                            r.password,
                            r.usernames,
                            r.user_last_names,
                            r.phone_number,
                            r.email,
                            r.identification
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveUsers: ",
                                    "guardado local bien users"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveUsers: ",
                                    "Error al guardar info local users"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvWaterType() {
        val inputReader = InputStreamReader(resources.assets.open("water_types.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<WaterTypeEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    WaterTypeEntity(
                        row[0].toInt(),
                        row[1],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }
        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelWaterType.saveWaterType(
                        WaterTypeEntity(
                            r.id_water_type,
                            r.water_type_name
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveWaterType: ",
                                    "guardado local bien waterty"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveWaterType: ",
                                    "Error al guardar info local waterty"
                                )
                            }
                        }
                    }
                }
            }
        }

    }

    private fun saveCsvFeatures() {
        val inputReader = InputStreamReader(resources.assets.open("features.csv"),"ISO-8859-1")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<FeatureEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    FeatureEntity(
                        row[0].toInt(),
                        row[1]
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())
        }
        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelFeatures.saveFeatures(
                        FeatureEntity(
                            r.id_feature,
                            r.feature_name
                        )
                    ).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveFeatures: ",
                                    "guardado local bien feature"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "saveFeatures: ",
                                    "Error al guardar info local feature"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCsvAnswerType() {
        val inputReader = InputStreamReader(resources.assets.open("answer_type.csv"),"UTF-8")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<AnswerTypeEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    AnswerTypeEntity(
                        row[0].toInt(),
                        row[1],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelAnswerType.saveAnswerType(r).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveCsvAnswerType: ",
                                    "guardado local bien AnswerType"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorModuleTypes: ",
                                    "Error al guardar info local AnswerType"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCsvModuleTypes() {
        val inputReader = InputStreamReader(resources.assets.open("module_type.csv"),"UTF-8")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<ModuleTypeEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    ModuleTypeEntity(
                        row[0].toInt(),
                        row[1],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelModuleType.saveModuleType(r).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveCsvModuleTypes: ",
                                    "guardado local bien ModuleTypes"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorModuleTypes: ",
                                    "Error al guardar info local ModuleTypes"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCsvOptionQuestion() {
        val inputReader = InputStreamReader(resources.assets.open("options_question.csv"),"UTF-8")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<OptionQuestionEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    OptionQuestionEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelOptionQuestion.saveOptionQuestion(r).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveCsvOptionQuestion: ",
                                    "guardado local bien OptionQuestion"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorModuleTypes: ",
                                    "Error al guardar info local OptionQuestion"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCsvQuestion() {
        val inputReader = InputStreamReader(resources.assets.open("questions.csv"),"UTF-8")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<QuestionEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    QuestionEntity(
                        row[0].toInt(),
                        row[1].toInt(),
                        row[2].toInt(),
                        row[3].toInt(),
                        row[4],
                        row[5].toInt(),
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelQuestion.saveQuestion(r).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveCsvQuestion: ",
                                    "guardado local bien Question"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorModuleTypes: ",
                                    "Error al guardar info local Question"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveCsvQuestionType() {
        val inputReader = InputStreamReader(resources.assets.open("question_type.csv"),"UTF-8")
        val reader = BufferedReader(inputReader)

        val data = mutableListOf<QuestionTypeEntity>()
        try {
            var line: String
            while (reader.readLine().also { line = it } != null) {
                val row: List<String> = line.split(",")
                data.add(
                    QuestionTypeEntity(
                        row[0].toInt(),
                        row[1],
                    )
                )
            }

        } catch (e: Exception) {
            Log.e("Error: ", e.message.toString())

        }

        data.forEach { r ->
            lifecycleScope.launchWhenCreated {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelQuestionType.saveQuestionType(r).collect { rLocal ->
                        when (rLocal) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e(
                                    "saveCsvQuestionType: ",
                                    "guardado local bien QuestionType"
                                )
                            }
                            is Result.Failure -> {
                                Log.e(
                                    "ErrorModuleTypes: ",
                                    "Error al guardar info local QuestionType"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun startTimer(time:Long) {
        object : CountDownTimer(time, 100) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                val i = Intent(Intent(this@SplashActivity, LoginActivity::class.java))
                startActivity(i)
                finish()
            }
        }.start()
    }


}