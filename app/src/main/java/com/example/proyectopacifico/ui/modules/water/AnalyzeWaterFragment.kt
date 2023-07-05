package com.example.proyectopacifico.ui.modules.water

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ArrayAdapter
import com.example.proyectopacifico.core.Result
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.core.hide
import com.example.proyectopacifico.core.show
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.AnalysisEntity
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.entities.ParameterEntity
import com.example.proyectopacifico.data.models.entities.SampleEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.AddSamplesBinding
import com.example.proyectopacifico.databinding.FragmentAnalyzeWaterBinding
import com.example.proyectopacifico.databinding.ItemWaterSampleBinding
import com.example.proyectopacifico.domain.analysis.AnalysisRepoImpl
import com.example.proyectopacifico.domain.measure.MeasureRepoImpl
import com.example.proyectopacifico.domain.parameter.ParameterRepoImpl
import com.example.proyectopacifico.domain.sample.SampleRepoImpl
import com.example.proyectopacifico.presentation.*
import com.example.proyectopacifico.ui.modules.water.adapter.DialogMeasureAdapter
import com.example.proyectopacifico.ui.modules.water.adapter.WaterSamplesAdapter
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*


class AnalyzeWaterFragment : Fragment(R.layout.fragment_analyze_water),
    RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: FragmentAnalyzeWaterBinding
    private val args by navArgs<AnalyzeWaterFragmentArgs>()

    private lateinit var parametersList: MutableList<ParameterEntity>
    private lateinit var analysis: AnalysisEntity
    private lateinit var sample: SampleEntity

    private var waterType: Int = 0
    private lateinit var selectedParameter: ParameterEntity
    private val acceptableSamplesList = listOf("Aceptable", "No Aceptable")
    private var existSample = false
    private lateinit var dialogMeasureAdapter: DialogMeasureAdapter
    private var measureList = mutableListOf<MeasureEntity>()
    private lateinit var sampleClicked: SampleEntity

    private val viewModelAnalysis by viewModels<AnalysisViewModel> {
        AnalysisViewModelFactory(
            AnalysisRepoImpl(AppDatabase.getDatabase(requireContext()).AnalysisDao(),
                RetrofitClient.webService
            )
        )


    }

    private val viewModelSample by viewModels<SamplesViewModel> {
        SamplesViewModelFactory(
            SampleRepoImpl(
                AppDatabase.getDatabase(requireContext()).SampleDao(),
                RetrofitClient.webService
            )
        )


    }

    private val viewModelMeasure by viewModels<MeasureViewModel> {
        MeasureViewModelFactory(
            MeasureRepoImpl(
                AppDatabase.getDatabase(requireContext()).MeasureDao(),
                RetrofitClient.webService
            )
        )


    }

    private val viewModelParameters by viewModels<ParameterViewModel> {
        ParameterViewModelFactory(
            ParameterRepoImpl(
                AppDatabase.getDatabase(requireContext()).ParameterDao()
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAnalyzeWaterBinding.bind(view)

        referenceData()
        obtainAnalysis()
        clicks()
        obtainParameters(false)


    }

    private fun clicks() {
        binding.imgBtnBack.setOnClickListener { findNavController().popBackStack() }
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.txtFParameter.setOnItemClickListener { parent, view, position, id ->
            selectedParameter = parametersList[position]
        }
        binding.btnSaveAnalysis.setOnClickListener { validateSaveAnalysis() }
        binding.btnAddSample.setOnClickListener { validateSample() }
    }

    private fun obtainParameters(existsSmellAndTaste: Boolean) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelParameters.getDbParameters().collect {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressBar?.show()
                            binding.screen?.hide()
                        }
                        is Result.Success -> {
                            binding.progressBar?.hide()
                            binding.screen?.show()
                            val parameters: MutableList<String> = mutableListOf()
                            parametersList = it.data.toMutableList()
                            it.data.forEach {
                                parameters.add(it.parameter_name)
                            }
                            if (existsSmellAndTaste) {
                                parameters.removeAll { parameter -> parameter == "Olor y Sabor " }
                                parametersList.removeAll { parameterList -> parameterList.id_parameter == 2 }
                            }
                            setUpParameters(parameters)
                        }
                        is Result.Failure -> {
                            binding.progressBar?.hide()
                            binding.screen?.show()
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun setUpParameters(parameters: MutableList<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, parameters)
        binding.txtFParameter.setAdapter(adapter)
    }

    private fun validateSaveAnalysis() {
        val results = arrayOf(validateSup(), validateSub(), validateCap())
        if (false in results)
            return

        if (waterType == 0) {
            binding.cbNaturalWater.error = "Debe estar minimo un campo seleccionado"
            return
        }
        saveAnalysis()
    }

    private fun validateCap(): Boolean {
        return if (binding.txtCatchemntType.text.toString().isNullOrEmpty()) {
            binding.txtILCatchmentType.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILCatchmentType.error = null
            true
        }
    }

    private fun validateSub(): Boolean {
        return if (binding.txtUndergroundSources.text.toString().isNullOrEmpty()) {
            binding.txtILUnderGroundSources.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILUnderGroundSources.error = null
            true
        }
    }

    private fun validateSup(): Boolean {
        return if (binding.txtSurfaceSources.text.toString().isNullOrEmpty()) {
            binding.txtILSurfaceSources.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILSurfaceSources.error = null
            true
        }
    }

    private fun validateSample() {
        val results = arrayOf(
            validateParameter(),
        )
        if (false in results) {
            return
        }
        addSample()
    }

    private fun validateParameter(): Boolean {
        return if (binding.txtFParameter.text.toString().isNullOrEmpty()) {
            binding.txtFParameter.error = "Este campo no puede estar vacio"
            false
        } else {
            binding.txtFParameter.error = null
            true
        }
    }

    private fun addSample() {

        val dialogBinding = AddSamplesBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.txtTitleParameter.text =
            "Ingrese el dato de la muestra de ${binding.txtFParameter.text.toString()} :"
        if (selectedParameter.id_parameter == 2) {

            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, acceptableSamplesList)
            dialogBinding.autocompleteSelectSamplesAcceptable.setAdapter(adapter)

            dialogBinding.txtILSampleAcceptable.show()
            dialogBinding.txtILSample.hide()
        }
        dialogBinding.imgClose.setOnClickListener { dialog.dismiss() }
        dialogBinding.btnSample.setOnClickListener {
            var valueSample = ""
            if (!dialogBinding.edtSample.text.toString().isNullOrEmpty()) {
                valueSample = dialogBinding.edtSample.text.toString()
            } else if (!dialogBinding.autocompleteSelectSamplesAcceptable.text.toString()
                    .isNullOrEmpty()
            ) {
                valueSample = dialogBinding.autocompleteSelectSamplesAcceptable.text.toString()
            }
            val results = if (valueSample.isNullOrEmpty()) {
                dialogBinding.txtILSample.error = "Este campo no puede estar vacio"
                dialogBinding.txtILSampleAcceptable.error = "Este campo no puede estar vacio"
                false
            } else {
                dialogBinding.txtILSample.error = null
                dialogBinding.txtILSampleAcceptable.error = null
                true
            }
            if (!results) {
                return@setOnClickListener
            }
            getExistingSample(valueSample, dialogBinding, dialog)
        }
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()


    }

    private fun getExistingSample(
        value: String,
        dialog: AddSamplesBinding,
        alertDialog: AlertDialog
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.getExistingSample(
                    analysis.id_analysis,
                    selectedParameter.id_parameter
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data == null) {
                                saveSample(value, dialog, alertDialog)
                            } else {
                                if (it.data.parameter_id == 2) {
                                    alertDialog.dismiss()
                                    Snackbar.make(
                                        binding.root,
                                        "Este parametro solo se puede registrar una vez",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                } else {
                                    sample = it.data
                                    existSample = true
                                    saveMeasure(it.data.id_sample, value, alertDialog)
                                }
                            }
                        }
                        is Result.Failure -> {
                            Log.e(
                                "getExistingSample: ",
                                "Error al buscar una muestra ${it.exception.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveSample(value: String, dialog: AddSamplesBinding, alertDialog: AlertDialog) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.saveSample(
                    SampleEntity(
                        0,
                        selectedParameter.id_parameter,
                        analysis.id_analysis,
                        value,
                    )
                ).collect { sample ->
                    when (sample) {
                        is Result.Loading -> {
                            dialog.progressBar?.show()
                            dialog.btnSample?.hide()
                        }
                        is Result.Success -> {
                            dialog.btnSample?.show()
                            dialog.progressBar?.hide()
                            alertDialog.dismiss()
                            obtainLastSample(value)
                        }
                        is Result.Failure -> {
                            dialog.btnSample?.show()
                            dialog.progressBar?.hide()
                            Snackbar.make(
                                binding.root,
                                "Error al registrarse",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "sendUser: ${sample.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainLastSample(value: String) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.getLastSample().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            saveMeasure(it.data.id_sample, value, null)
                        }
                        is Result.Failure -> {
                            Log.e(
                                "obtainLastSample: ",
                                "Error al obtener la ultima muestra ${it.exception.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveMeasure(sample_id: Int, value: String, alertDialog: AlertDialog?) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMeasure.saveMeasure(
                    MeasureEntity(
                        0,
                        value,
                        SimpleDateFormat("dd/MM/yyyy KK:mma").format(Date()).toString(),
                        sample_id,
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            Snackbar.make(
                                binding.root,
                                "Se guardo correctamente las medidas",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            alertDialog?.dismiss()
                            if (existSample) {
                                obtainMeasures(sample_id)
                            } else {
                                obtainSamples()
                            }
                        }
                        is Result.Failure -> {
                            Log.e(
                                "obtainLastSample: ",
                                "saveMeasure ${it.exception.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun obtainMeasures(sample_id: Int) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMeasure.getMeasuresBySample(sample_id).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            var totalValues = 0.0
                            it.data.forEach { m ->
                                totalValues += m.value.toDouble()
                            }
                            updateSampleAverage(
                                it.data.size, totalValues, sample.id_sample,
                                sample.parameter_id,
                                sample.analysis_id
                            )
                        }
                        is Result.Failure -> {
                            Log.e(
                                "obtainMeasures: ",
                                "obtainMeasures ${it.exception.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateSampleAverage(
        totalMeasures: Int,
        totalValuesSum: Double,
        idSample: Int,
        parameterId: Int,
        analysisId: Int
    ) {
        val average = totalValuesSum / totalMeasures
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.updateSample(
                    SampleEntity(
                        idSample,
                        parameterId,
                        analysisId,
                        average.toString()
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            obtainSamples()
                        }
                        is Result.Failure -> {
                            Log.e(
                                "updateSampleAverage: ",
                                "updateSampleAverage ${it.exception.message}"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun saveAnalysis() {
        val shared =
            requireActivity().getSharedPreferences(Constants.SHARED_USER, Context.MODE_PRIVATE)
        val idUser = shared.getInt("idUser", 0)
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelAnalysis.saveAnalysis(
                    AnalysisEntity(
                        0,
                        idUser,
                        args.place.id_place,
                        waterType,
                        SimpleDateFormat("dd/mm/yyyy").format(Date()).toString(),
                        SimpleDateFormat("KK:mma").format(Date()).toString(),
                        "prueba",
                        binding.txtSurfaceSources.text.toString(),
                        binding.txtUndergroundSources.text.toString(),
                        binding.txtCatchemntType.text.toString(),

                        )
                ).collect {
                    when (it) {
                        is Result.Loading -> {
                            enableCheckButtons(false)
                            binding.btnSaveAnalysis.hide()
                            binding.btnSaveProgressBar.show()
                        }
                        is Result.Success -> {
                            binding.screenNoAnalysis.hide()
                            binding.screenSamples.show()
                            binding.btnSaveProgressBar.hide()
                            obtainAnalysis()
                        }
                        is Result.Failure -> {
                            enableCheckButtons(true)
                            binding.btnSaveProgressBar.hide()
                            Snackbar.make(
                                binding.root,
                                "Error al guardar el analisis",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "save Analisis: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainAnalysis() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelAnalysis.getOneAnalysis(args.place.id_place).collect {
                    when (it) {
                        is Result.Loading -> {

                        }
                        is Result.Success -> {
                            Log.e("obtainAnalysis: ", it.toString())
                            if (it.data != null) {
                                binding.btnSaveAnalysis.hide()
                                binding.screenNoAnalysis.hide()
                                binding.screenSamples.show()
                                enableCheckButtons(false)
                                selectedCheckButton(it.data.water_type_id)
                                binding.txtVDate.text = "${it.data.date} ${it.data.hour}"
                                binding.txtUndergroundSources.setText(it.data.underground_sources)
                                binding.txtSurfaceSources.setText(it.data.surface_sources)
                                binding.txtCatchemntType.setText(it.data.catchment_type)
                                analysis = it.data
                                obtainSamples()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            binding.screen.show()
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainSamples() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.getSampleParametersAndMeasures(analysis.id_analysis).collect {
                    when (it) {
                        is Result.Loading -> {
                            binding.progressBar?.show()
                            binding.screen?.hide()
                        }
                        is Result.Success -> {
                            binding.progressBar?.hide()
                            binding.screen?.show()
                            if (it.data == null) {
                                binding.noSamples.show()
                                binding.rvSamples.hide()
                            } else {
                                binding.noSamples.hide()
                                binding.rvSamples.show()
                            }
                            knowIfExistsSmellAndTaste(it)
                            val adapter = WaterSamplesAdapter(
                                it.data,
                                requireContext(),
                                onClickItem = { sampleParam -> onClickWaterSampleAdapter(sampleParam) })
                            binding.rvSamples.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvSamples.adapter = adapter

                        }
                        is Result.Failure -> {
                            binding.progressBar?.hide()
                            binding.screen?.show()
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "obtainAbilities: ${it.exception.message}")
                        }
                    }
                }
            }
        }
    }

    private fun onClickWaterSampleAdapter(sampleParam: SampleParameterAndMeasure) {
        sampleParam.apply {
            sampleClicked = SampleEntity(
                this.sampleParameter.id_sample,
                this.sampleParameter.parameter_id,
                this.sampleParameter.analysis_id,
                this.sampleParameter.average
            )
        }
        val dialog = ItemWaterSampleBinding.inflate(layoutInflater)
        val alert = AlertDialog.Builder(requireContext()).apply {
            setView(dialog.root)
        }.create()

        measureList = sampleParam.measures.toMutableList()
        dialogMeasureAdapter = DialogMeasureAdapter(
            measureList,
            onClickDelete = { position, measure ->
                onDeleteMeasureInSample(
                    position,
                    measure,
                    dialog,
                    alert
                )
            })

        dialog.tvTitle.text = sampleParam.sampleParameter.parameter_name
        dialog.tvAverage.text = sampleParam.sampleParameter.average
        dialog.tvLimit.text = sampleParam.sampleParameter.expected_value
        dialog.tvOperator.text = sampleParam.sampleParameter.operator
        dialog.rvMeasures.adapter = dialogMeasureAdapter
        dialog.rvMeasures.layoutManager = LinearLayoutManager(requireContext())
        dialog.btnDelete.setOnClickListener {
            deleteMeasureById(
                sampleParam.sampleParameter.id_sample,
                alert
            )
        }


        alert.window?.setBackgroundDrawableResource(R.color.transparent)
        alert.show()
    }

    private fun onDeleteMeasureInSample(
        position: Int,
        measure: MeasureEntity,
        dialog: ItemWaterSampleBinding,
        alert: AlertDialog,
    ) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMeasure.deleteMeasure(measure).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            measureList.removeAt(position)
                            dialogMeasureAdapter.notifyItemRemoved(position)
                            changeSampleAverage(dialog,alert)
                            obtainSamples()
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al elimnar la medida",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    }

    private fun changeSampleAverage(dialog: ItemWaterSampleBinding,alert: AlertDialog) {
        var total = 0.0
        if (measureList.isNotEmpty()) {
            measureList.forEach {
                total += it.value.toDouble()
            }
            dialog.tvAverage.text = (total / measureList.size).toString()
            updateSampleAverage(
                measureList.size,
                total,
                sampleClicked.id_sample,
                sampleClicked.parameter_id,
                sampleClicked.analysis_id
            )
        } else {
            deleteSampleById(sampleClicked.id_sample,alert)
        }

    }

    private fun deleteMeasureById(idSample: Int, alert: AlertDialog) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMeasure.deleteMeasuresById(idSample).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            deleteSampleById(idSample, alert)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al eliminar las medidas",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun deleteSampleById(idSample: Int, alert: AlertDialog) {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSample.deleteSampleById(idSample).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            obtainSamples()
                            alert.dismiss()
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al eliminar la muestra",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun knowIfExistsSmellAndTaste(it: Result.Success<List<SampleParameterAndMeasure>>) {
        val smellAndTasteFilter = it.data.filter { it.sampleParameter.id_parameter == 2 }
        if (!smellAndTasteFilter.isNullOrEmpty()) {
            obtainParameters(true)
        }
    }

    private fun selectedCheckButton(waterId: Int) {
        when (waterId) {
            1 -> {
                binding.cbNaturalWater.isChecked = true
            }
            2 -> {
                binding.cbTreatedWater.isChecked = true
            }
            3 -> {
                binding.cbResidualWater.isChecked = true
            }
            4 -> {
                binding.cbOtherWater.isChecked = true
            }
        }
    }

    private fun enableCheckButtons(param: Boolean) {
        binding.cbNaturalWater.isEnabled = param
        binding.cbTreatedWater.isEnabled = param
        binding.cbResidualWater.isEnabled = param
        binding.cbOtherWater.isEnabled = param
        binding.txtILCatchmentType.isEnabled = param
        binding.txtILSurfaceSources.isEnabled = param
        binding.txtILUnderGroundSources.isEnabled = param
    }

    private fun referenceData() {
        obtainActualDate()
        binding.txtVMunicipality.text = args.place.municipality_name
        binding.txtVPopulatedCenter.text = args.place.populated_center_name
    }

    private fun obtainActualDate() {
        val date = SimpleDateFormat("MMM dd,yyyy KK:mma").format(Date())
        binding.txtVDate.text = date
    }

    override fun onCheckedChanged(p0: RadioGroup?, checkedId: Int) {
        when (checkedId) {
            R.id.cbNaturalWater -> {
                waterType = 1
            }
            R.id.cbTreatedWater -> {
                waterType = 2
            }
            R.id.cbResidualWater -> {
                waterType = 3
            }
            R.id.cbOtherWater -> {
                waterType = 4
            }
        }
    }

}