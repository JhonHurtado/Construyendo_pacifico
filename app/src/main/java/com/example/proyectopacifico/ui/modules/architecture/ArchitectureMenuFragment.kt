package com.example.proyectopacifico.ui.modules.architecture

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswerAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersAndQuestions
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.FragmentArchitectureMenuBinding
import com.example.proyectopacifico.domain.answer.AnswerRepoImpl
import com.example.proyectopacifico.presentation.AnswerViewModel
import com.example.proyectopacifico.presentation.AnswerViewModelFactory
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import com.google.android.material.snackbar.Snackbar
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsvBuilder
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.text.DateFormat


class ArchitectureMenuFragment : Fragment(R.layout.fragment_architecture_menu) {
    private lateinit var binding: FragmentArchitectureMenuBinding
    private val args by navArgs<ArchitectureMenuFragmentArgs>()
    private var answerObservationList = mutableListOf<AnswerAndQuestion>()
    private var answerCharacterizationList = mutableListOf<AnswerAndQuestion>()
    private var typeCsv = ""
    private val registerPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (false in it.values) {
                Snackbar.make(
                    binding.root,
                    "You must enable the permissions",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                if (typeCsv == "Characterization") {
                    permissions(answerObservationList, typeCsv)
                }else{
                    permissions(answerCharacterizationList, typeCsv)
                }
            }
        }

    private val viewModelAnswers by viewModels<AnswerViewModel> {
        AnswerViewModelFactory(
            AnswerRepoImpl(
                AppDatabase.getDatabase(requireContext()).AnswerDao(),
                RetrofitClient.webService
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentArchitectureMenuBinding.bind(view)

        obtainExistingArchitecture()


    }

    private fun obtainExistingArchitecture() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelAnswers.fetchAnswersByPlaceId(args.place.id_place)
                    .collect { r ->
                        when (r) {
                            is Result.Loading -> {
                            }
                            is Result.Success -> {
                                if (r.data.isEmpty()) {
                                    clicks("null")
                                } else {
                                    if (r.data.none { x -> x.question_type_id == 1 }) {
                                        clicks("null observation")
                                    } else {
                                        answerObservationList =
                                            r.data.filter { x -> x.question_type_id == 1 }
                                                .toMutableList()
                                        typeCsv = "Observation"
                                        clicks("observation")
                                    }
                                    if (r.data.none { x -> x.question_type_id == 2 }) {
                                        clicks("null characterization")
                                    } else {
                                        answerCharacterizationList = r.data.filter { x -> x.question_type_id == 2 }
                                            .toMutableList()
                                        typeCsv = "Characterization"
                                        clicks("characterization")
                                    }
                                }
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al obtener los datos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e(
                                    "Error",
                                    "setUpQuestions: ${r.exception.message}"
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun clicks(s: String) {
        binding.btnBackToMenu.setOnClickListener { findNavController().popBackStack() }
        if (s == "null") {
            binding.cvObservation.setOnClickListener {
                val action =
                    ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToObservationArchitectureFragment(
                        args.place
                    )
                findNavController().navigate(action)
            }
            binding.cvCharacterization.setOnClickListener {
                val action =
                    ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToCharacterizationArchitecturePart1Fragment(
                        args.place
                    )
                findNavController().navigate(action)
            }

        }
        if (s == "observation") {
            binding.lLObservation.visibility = View.VISIBLE
            binding.ivDownloadObservation.setOnClickListener {
                permissions(answerObservationList, typeCsv)
            }
            binding.ivEditObservation.setOnClickListener {
                val action = ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToObservationUpdateFragment(
                    AnswersAndQuestions(answerObservationList)
                )
                findNavController().navigate(action)
            }
            binding.ivSeeObservation.setOnClickListener {
                val action = ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToObservationDetailFragment(
                    AnswersAndQuestions(answerObservationList)
                )
                findNavController().navigate(action)
            }
        } else if (s == "null observation") {
            binding.cvObservation.setOnClickListener {
                val action =
                    ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToObservationArchitectureFragment(
                        args.place
                    )
                findNavController().navigate(action)
            }
        }
        if (s == "characterization") {
            binding.lLCharacterization.visibility = View.VISIBLE
            binding.ivDownloadCharacterization.setOnClickListener {
                permissions(
                    answerCharacterizationList,
                    typeCsv
                )
            }
            binding.ivEditCharacterization.setOnClickListener {
                val action = ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToCharacterizationUpdateFragment(
                    AnswersAndQuestions(answerCharacterizationList)
                )
                findNavController().navigate(action)
            }
            binding.ivSeeCharacterization.setOnClickListener {
                val action = ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToCharacterizationDetailFragment(
                    AnswersAndQuestions(answerCharacterizationList)
                )
                findNavController().navigate(action)
            }
        } else if (s == "null characterization") {
            binding.cvCharacterization.setOnClickListener {
                val action =
                    ArchitectureMenuFragmentDirections.actionArchitectureMenuFragmentToCharacterizationArchitecturePart1Fragment(
                        args.place
                    )
                findNavController().navigate(action)
            }
        }
    }

    private fun exportDatabaseToCSVFile(list: List<AnswerAndQuestion>, type: String) {
        val prefix: String = "Pacifico_Construccion_$type"
        val suffix: String = DateFormat
            .getDateTimeInstance()
            .format(System.currentTimeMillis())
            .toString()
            .replace(",", "")
            .replace(" ", "_")
            .replace(":", "_")

        val fileName: String = "$prefix$suffix.csv"
        Log.e("exportDatabaseToCSVFile: ", fileName)

        val csvFile = generateFile(requireContext(), fileName)
        if (csvFile != null) {
            csvWriter().open(csvFile, append = false) {
                // Header
                writeRow(
                    listOf(
                        "id_answer",
                        "question_id",
                        "option_question_id",
                        "place_id",
                        "open_answer"
                    )
                )
                list.forEach { list ->
                    writeRow(
                        listOf(
                            list.id_answer,
                            list.question_answer_id,
                            list.option_question_id,
                            list.place_id,
                            list.open_answer
                        )
                    )
                }
            }


            Toast.makeText(requireContext(), "csv generado", Toast.LENGTH_LONG).show()
            val intent = goToFileIntent(requireContext(), csvFile)
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "csv no generado", Toast.LENGTH_LONG).show()
        }
    }

    fun generateFile(context: Context, fileName: String): File? {
        val csvFile = File(context.filesDir, fileName)
        csvFile.createNewFile()

        return if (csvFile.exists()) {
            csvFile
        } else {
            null
        }
    }

    fun goToFileIntent(context: Context, file: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val contentUri =
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val mimeType = context.contentResolver.getType(contentUri)
        intent.setDataAndType(contentUri, mimeType)
        intent.flags =
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        return intent
    }

    private fun permissions(list: List<AnswerAndQuestion>, type: String) {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                exportDatabaseToCSVFile(list, type)
            }
            else -> {
                registerPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }
}