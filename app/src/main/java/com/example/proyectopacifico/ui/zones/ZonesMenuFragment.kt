package com.example.proyectopacifico.ui.zones

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.*
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.*
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.web.analysis.AnalysisBody
import com.example.proyectopacifico.data.models.web.answers.AnswerBody
import com.example.proyectopacifico.data.models.web.measure.MeasureBody
import com.example.proyectopacifico.data.models.web.place.PlaceBody
import com.example.proyectopacifico.data.models.web.population.PopulationBody
import com.example.proyectopacifico.data.models.web.sample.SampleBody
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.AddSamplesBinding
import com.example.proyectopacifico.databinding.FragmentZonesMenuBinding
import com.example.proyectopacifico.databinding.ItemUploadBinding
import com.example.proyectopacifico.domain.analysis.AnalysisRepoImpl
import com.example.proyectopacifico.domain.answer.AnswerRepoImpl
import com.example.proyectopacifico.domain.measure.MeasureRepoImpl
import com.example.proyectopacifico.domain.place.PlaceRepoImpl
import com.example.proyectopacifico.domain.population.PopulationRepoImpl
import com.example.proyectopacifico.domain.sample.SampleRepoImpl
import com.example.proyectopacifico.presentation.*
import com.example.proyectopacifico.ui.zones.adapter.ZonesMenuAdapter
import com.google.android.material.snackbar.Snackbar


class ZonesMenuFragment : Fragment(R.layout.fragment_zones_menu) {
    private lateinit var binding: FragmentZonesMenuBinding
    private var populationList = listOf<PopulationEntity>()
    private var placesList = listOf<PlaceEntity>()
    private var answersArchitectureList = listOf<AnswersEntity>()
    private var analysisList = listOf<AnalysisEntity>()
    private var sampleList = listOf<SampleEntity>()
    private var measureList = listOf<MeasureEntity>()

    private val viewModelPopulation by viewModels<PopulationViewModel> {
        PopulationViewModelFactory(
            PopulationRepoImpl(
                AppDatabase.getDatabase(requireContext()).PopulationDao(),
                RetrofitClient.webService
            )
        )
    }

    private val viewModelPlace by viewModels<PlaceViewModel> {
        PlaceViewModelFactory(
            PlaceRepoImpl(
                AppDatabase.getDatabase(requireContext()).PlaceDao(),
                RetrofitClient.webService
            )
        )
    }

    private val viewModelAnswers by viewModels<AnswerViewModel> {
        AnswerViewModelFactory(
            AnswerRepoImpl(
                AppDatabase.getDatabase(requireContext()).AnswerDao(),
                RetrofitClient.webService
            )
        )
    }

    private val viewModelAnalysis by viewModels<AnalysisViewModel> {
        AnalysisViewModelFactory(
            AnalysisRepoImpl(
                AppDatabase.getDatabase(requireContext()).AnalysisDao(),
                RetrofitClient.webService
            )
        )
    }

    private val viewModelSamples by viewModels<SamplesViewModel> {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentZonesMenuBinding.bind(view)

        clicks()
        obtainPopulations()


    }

    private fun obtainPopulations() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.getDbPopulations().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                binding.noData.show()
                                binding.rvPopulation.hide()
                            } else {
                                binding.noData.hide()
                                binding.rvPopulation.show()
                            }
                            val adapter =
                                ZonesMenuAdapter(it.data, viewModelPopulation, requireContext())

                            val displayMetrics = DisplayMetrics()
                            val metrics = DisplayMetrics()
                            requireActivity().getWindowManager().getDefaultDisplay()
                                .getMetrics(metrics);
                            requireActivity().getWindowManager().defaultDisplay.getMetrics(
                                displayMetrics
                            )
                            val height = displayMetrics.heightPixels
                            val width = displayMetrics.widthPixels

                            Log.e("obtainPopulations: ", width.toString())
                            Log.e("obtainPopulations: ", height.toString())
                            if (width >= 2000) {
                                binding.rvPopulation.layoutManager =
                                    GridLayoutManager(requireContext(), 8)
                            } else {
                                binding.rvPopulation.layoutManager =
                                    GridLayoutManager(requireContext(), 3)
                            }

                            binding.rvPopulation.adapter = adapter
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun clicks() {
        binding.btnRegisterNewZone.setOnClickListener { findNavController().navigate(R.id.action_zonesMenuFragment_to_zonesFormCreateFragment) }
        binding.swList.setOnClickListener {
            if (binding.swList.isChecked) {
                binding.rvPopulation.layoutManager = LinearLayoutManager(requireContext())
            } else {
                obtainPopulations()
            }
        }
        binding.ivUploadData.setOnClickListener {
            val dialogBinding = ItemUploadBinding.inflate(layoutInflater)

            val dialog = AlertDialog.Builder(requireContext()).apply {
                setView(dialogBinding.root)
            }.create()
            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }
            dialogBinding.btnConfirm.setOnClickListener {
                obtainAllTables()
                dialog.dismiss()
            }

            dialog.setCancelable(false)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
            dialog.show()
        }
    }

    private fun obtainAllTables() {
        obtainAllPopulations()

    }

    private fun obtainAllPopulations() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.getAllPopulations().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    "No Tiene Poblaciones creadas para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                populationList = it.data
                                obtainPlaces()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainPlaces() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPlace.getAllPlaces().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root, "No Tiene Sitios creadps para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                placesList = it.data
                                obtainsAnswersArchitecture()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainsAnswersArchitecture() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelAnswers.getAllAnswers().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    "No Tiene Formularios de arquitectura para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                obtainAnalysis()
                            } else {
                                answersArchitectureList = it.data
                                obtainAnalysis()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainAnalysis() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelAnalysis.getAllAnalysis().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    "No Tiene Analysis de agua para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                analysisList = it.data
                                obtainSamples()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainSamples() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelSamples.getAllSamples().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root,
                                    "No Tiene Muestras de agua para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                sampleList = it.data
                                obtainMeasure()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun obtainMeasure() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelMeasure.getAllMeasures().collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isEmpty()) {
                                Snackbar.make(
                                    binding.root, "No Tiene Medidas de agua para subir al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            } else {
                                measureList = it.data
                                sendDataToServer()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun sendDataToServer() {
        if (populationList.isNotEmpty()) {
            sendPopulationsToServer(populationList)
        }
    }

    private fun sendPopulationsToServer(populationList: List<PopulationEntity>) {
        val populations = populationList.toPopulationBody(PopulationBody(mutableListOf()))
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPopulation.saveWebPopulation(populations).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            Log.e("onSuccessPop", "${it.data}")
                            if (it.data.status == "sucess") {
                                Snackbar.make(
                                    binding.root, it.data.message,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                sendPlacesToServer(placesList)
                            } else {
                                Snackbar.make(
                                    binding.root, "Error al subir la informacion al servidor",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is Result.Failure -> {
                            binding.progressBar.hide()
                            Snackbar.make(
                                binding.root, "Error del servidor ${it.exception}",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun sendPlacesToServer(places: List<PlaceEntity>) {
        if (places.isNotEmpty()) {
            val place = places.toPlaceBody(PlaceBody(mutableListOf()))
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelPlace.saveWebPlace(place).collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e("onSuccessPlace", "${it.data}")
                                if (it.data.status == "sucess") {
                                    Snackbar.make(
                                        binding.root, it.data.message,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    sendAnswersArchitecture(answersArchitectureList)
                                } else {
                                    Snackbar.make(
                                        binding.root, "Error al subir la informacion al servidor",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                binding.progressBar.hide()
                                Snackbar.make(
                                    binding.root, "Error del servidor ${it.exception}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendAnswersArchitecture(answers: List<AnswersEntity>) {
        if (answers.isNotEmpty()) {
            val answer = answers.toAnswerBody(AnswerBody(mutableListOf()))
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelAnswers.saveWebAnswers(answer).collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e("onSuccessAnswer", "${it.data}")
                                if (it.data.status == "sucess") {
                                    Snackbar.make(
                                        binding.root, it.data.message,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    sendAnalysis(analysisList)
                                } else {
                                    Snackbar.make(
                                        binding.root, "Error al subir la informacion al servidor",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                binding.progressBar.hide()
                                Snackbar.make(
                                    binding.root, "Error del servidor ${it.exception}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                            }
                        }
                    }
                }
            }
        } else {
            sendAnalysis(analysisList)
        }
    }

    private fun sendAnalysis(analysis: List<AnalysisEntity>) {
        if (analysis.isNotEmpty()) {
            val listAnalysis = analysis.toAnalysisBody(AnalysisBody(mutableListOf()))
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelAnalysis.saveWebAnalysis(listAnalysis).collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e("onSuccessAnalysis", "${it.data}")
                                if (it.data.status == "sucess") {
                                    Snackbar.make(
                                        binding.root, it.data.message,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    sendSamples(sampleList)
                                } else {
                                    Snackbar.make(
                                        binding.root, "Error al subir la informacion al servidor",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                binding.progressBar.hide()
                                Snackbar.make(
                                    binding.root, "Error del servidor ${it.exception}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendSamples(sampleList: List<SampleEntity>) {
        if (sampleList.isNotEmpty()) {
            val sample = sampleList.toSampleBody(SampleBody(mutableListOf()))
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelSamples.saveWebSample(sample).collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e("onSuccessSample", "${it.data}")
                                if (it.data.status == "sucess") {
                                    Snackbar.make(
                                        binding.root, it.data.message,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    sendMeasure(measureList)
                                } else {
                                    Snackbar.make(
                                        binding.root, "Error al subir la informacion al servidor",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                binding.progressBar.hide()
                                Snackbar.make(
                                    binding.root, "Error del servidor ${it.exception}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendMeasure(measureList: List<MeasureEntity>) {
        if (measureList.isNotEmpty()){
            val measure  = measureList.toMeasureBody(MeasureBody(mutableListOf()))
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelMeasure.saveWebMeasure(measure).collect {
                        when (it) {
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Log.e("onSuccessMeasure", "${it.data}")
                                if (it.data.status == "sucess") {
                                    Snackbar.make(
                                        binding.root, it.data.message,
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                }else{
                                    Snackbar.make(
                                        binding.root, "Error al subir la informacion al servidor",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is Result.Failure -> {
                                binding.progressBar.hide()
                                Snackbar.make(
                                    binding.root, "Error del servidor ${it.exception}",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Errorrrrrrrrrrrrrrr", "${it.exception}")
                            }
                        }
                    }
                }
            }
        }
    }
}