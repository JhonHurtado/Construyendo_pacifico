package com.example.proyectopacifico.ui.modules.architecture.observation

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.arquitecture.AnswersEntity
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionsAndSubQuestions
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.SubQuestion
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.FragmentObservationArchitectureBinding
import com.example.proyectopacifico.domain.answer.AnswerRepoImpl
import com.example.proyectopacifico.domain.optionQuestion.OptionQuestionRepoImpl
import com.example.proyectopacifico.domain.question.QuestionRepoImpl
import com.example.proyectopacifico.presentation.*
import com.google.android.material.snackbar.Snackbar


class ObservationArchitectureFragment : Fragment(R.layout.fragment_observation_architecture) {

    private lateinit var binding: FragmentObservationArchitectureBinding

    private val args by navArgs<ObservationArchitectureFragmentArgs>()

    private val optionList = mutableListOf<Pair<Any, String>>()

    private var totalQuestions = mutableListOf<Int>()

    private val optionQuestionList = mutableListOf<OptionQuestionAndQuestion>()

    private val viewModelOptionQuestion by viewModels<OptionQuestionViewModel> {
        OptionQuestionViewModelFactory(
            OptionQuestionRepoImpl(
                AppDatabase.getDatabase(requireContext()).OptionQuestionDao()
            )
        )
    }

    private val viewModelQuestion by viewModels<QuestionViewModel> {
        QuestionViewModelFactory(
            QuestionRepoImpl(
                AppDatabase.getDatabase(requireContext()).QuestionDao()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentObservationArchitectureBinding.bind(view)


        clicks()
        obtainQuestions()

    }

    private fun obtainQuestions() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelQuestion.fetchAllQuestions(1).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            val finalList = mutableListOf<QuestionsAndSubQuestions>()
                            it.data.forEach { question ->
                                if (question.question_id == -1 || question.question_id == -2) {
                                    finalList.add(
                                        QuestionsAndSubQuestions(
                                            question.id_question,
                                            question.module_type_id,
                                            question.answer_type_id,
                                            question.question_type_id,
                                            question.question_statement,
                                            question.question_id,
                                            null,
                                            null
                                        )
                                    )
                                }
                            }
                            finalList.forEachIndexed { index, questionsAndSubQuestions ->
                                val childrenList =
                                    it.data.filter { x -> x.question_id == questionsAndSubQuestions.id_question }
                                if (childrenList.isNotEmpty()) {
                                    val subList = mutableListOf<SubQuestion>()
                                    childrenList.forEach { x ->
                                        subList.add(
                                            SubQuestion(
                                                x.id_question,
                                                x.module_type_id,
                                                x.answer_type_id,
                                                x.question_type_id,
                                                x.question_statement,
                                                x.question_id,
                                            )
                                        )
                                    }
                                    finalList[index].subQuestions = subList
                                }
                            }
                            fetchOptionQuestions(finalList)
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al obtener los datos",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "setUpQuestions: ${it.exception.message}")
                        }
                    }
                }
            }
        }

    }

    private fun fetchOptionQuestions(list: List<QuestionsAndSubQuestions>) {
        var count = 0
        list.forEachIndexed { indexGeneral, questionsAndSubQuestions ->
            if (questionsAndSubQuestions.subQuestions.isNullOrEmpty()) {
                viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        viewModelOptionQuestion.fetchOptionQuestionAndQuestionByQuestionId(
                            questionsAndSubQuestions.id_question
                        )
                            .collect {
                                when (it) {
                                    is Result.Loading -> {
                                    }
                                    is Result.Success -> {
                                        optionQuestionList.addAll(it.data)
                                        list[indexGeneral].optionQuestion = it.data
                                        count++
                                        if (count == list.size) {
                                            Log.e("fetchOptionQuestions: ", list.toString())
                                            createData(list)
                                        }
                                    }
                                    is Result.Failure -> {
                                        Snackbar.make(
                                            binding.root,
                                            "Error al obtener los datos",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        Log.e("Error", "setUpQuestions: ${it.exception.message}")
                                    }
                                }
                            }
                    }
                }
            } else {
                count++
                questionsAndSubQuestions.subQuestions?.let { subQuestions ->
                    subQuestions.forEachIndexed { index, subQuestion ->
                        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                                viewModelOptionQuestion.fetchOptionQuestionAndQuestionByQuestionId(
                                    subQuestion.id_question
                                )
                                    .collect {
                                        when (it) {
                                            is Result.Loading -> {
                                            }
                                            is Result.Success -> {
                                                optionQuestionList.addAll(it.data)
                                                list[indexGeneral].subQuestions?.get(index)?.optionQuestion =
                                                    it.data
                                                if (count == list.size) {
                                                    Log.e("fetchOptionQuestions: ", list.toString())
                                                    createData(list)
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
                                                    "setUpQuestions: ${it.exception.message}"
                                                )
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createData(finalList: List<QuestionsAndSubQuestions>) {
        finalList.forEach {
            val bugTextView = TextView(requireContext()).apply {
                text = it.question_statement
                TextViewCompat.setTextAppearance(
                    this,
                    R.style.Theme_ProyectoPacifico
                )
                setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.blueDarkSena
                    )
                )
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
                textSize = 18f//


                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            binding.lLQuestions.addView(bugTextView)
            if (it.subQuestions != null) {
                it.subQuestions?.let { subQuestion ->
                    createSubQuestions(subQuestion)
                }
            } else {
                it.optionQuestion?.let { option ->
                    totalQuestions.add(it.id_question)
                    createOptions(option)
                }
            }
        }
    }

    private fun createOptions(data: List<OptionQuestionAndQuestion>) {
        val radioGroup = RadioGroup(requireContext()).apply { // (2)
            layoutParams = LinearLayout.LayoutParams( // (7)
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        binding.lLQuestions.addView(radioGroup) // (8)
        data.forEach {
            if (it.answer_type_id == 3) {
                val editText = EditText(requireContext()).apply { // (2)
                    hint = it.option_question_statement// (3)
                    TextViewCompat.setTextAppearance( // (4)
                        this,
                        R.style.Theme_ProyectoPacifico
                    )
                    setTextColor(
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.graySenaVariant
                        )
                    ) // (5)
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD) //
                    textSize = 14f// (6)


                    layoutParams = LinearLayout.LayoutParams( // (7)
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                optionList.add(Pair(editText, "editText"))
                binding.lLQuestions.addView(editText) // (8)

            } else if(it.answer_type_id == 2) {
                val checkBox = CheckBox(requireContext()).apply { // (2)
                    text = it.option_question_statement// (3)
                    TextViewCompat.setTextAppearance( // (4)
                        this,
                        R.style.Theme_ProyectoPacifico
                    )
                    setTextColor(
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.graySenaVariant
                        )
                    ) // (5)
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD) //
                    textSize = 14f// (6)


                    layoutParams = LinearLayout.LayoutParams( // (7)
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                optionList.add(Pair(checkBox, "checkbox"))
                binding.lLQuestions.addView(checkBox) // (8)
            }else if(it.answer_type_id == 1){
                val radioButton = RadioButton(requireContext()).apply { // (2)
                    text = it.option_question_statement// (3)
                    TextViewCompat.setTextAppearance( // (4)
                        this,
                        R.style.Theme_ProyectoPacifico
                    )
                    setTextColor(
                        AppCompatResources.getColorStateList(
                            requireContext(),
                            R.color.graySenaVariant
                        )
                    ) // (5)
                    setTypeface(Typeface.MONOSPACE, Typeface.BOLD) //
                    textSize = 14f// (6)


                    layoutParams = LinearLayout.LayoutParams( // (7)
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                optionList.add(Pair(radioButton, "radiobutton"))
                radioGroup.addView(radioButton) // (8)
            }
        }
    }

    private fun createSubQuestions(subQuestions: List<SubQuestion>) {
        subQuestions.forEach {
            val bugTextView = TextView(requireContext()).apply { // (2)
                text = it.question_statement// (3)
                TextViewCompat.setTextAppearance( // (4)
                    this,
                    R.style.Theme_ProyectoPacifico
                )
                setTextColor(
                    AppCompatResources.getColorStateList(
                        requireContext(),
                        R.color.graySenaVariant
                    )
                ) // (5)
                setTypeface(Typeface.MONOSPACE, Typeface.BOLD) //
                textSize = 14f// (6)


                layoutParams = LinearLayout.LayoutParams( // (7)
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            binding.lLQuestions.addView(bugTextView) // (8)
            it.optionQuestion?.let { option ->
                totalQuestions.add(it.id_question)
                createOptions(option)
            }
        }
    }

    private fun clicks() {
        binding.btnBackToMenu.setOnClickListener { findNavController().popBackStack() }
        binding.btnSaveObservation.setOnClickListener { obtainAnswers() }
    }

    private fun obtainAnswers() {
        if (false in validateForm()) {
            Snackbar.make(
                binding.root,
                "Debe llenar todos lo datos del formulario",
                Snackbar.LENGTH_SHORT
            ).show()
            return
        } else {
            val answersList = mutableListOf<AnswersEntity>()
            for (i in optionList.indices) {
                if (optionList[i].second == "editText") {
                    if ((optionList[i].first as EditText).text.toString()
                            .isNotEmpty()
                    ) answersList.add(
                        AnswersEntity(
                            0,
                            optionQuestionList[i].id_question.toString(),
                            optionQuestionList[i].id_option_question.toString(),
                            args.place.id_place,
                            (optionList[i].first as EditText).text.toString(),
                        )
                    )
                } else if (optionList[i].second == "checkbox") {
                    if ((optionList[i].first as CheckBox).isChecked) answersList.add(
                        AnswersEntity(
                            0,
                            optionQuestionList[i].id_question.toString(),
                            optionQuestionList[i].id_option_question.toString(),
                            args.place.id_place,
                            "",
                        )
                    )
                }else if (optionList[i].second == "radiobutton") {
                    if ((optionList[i].first as RadioButton).isChecked) answersList.add(
                        AnswersEntity(
                            0,
                            optionQuestionList[i].id_question.toString(),
                            optionQuestionList[i].id_option_question.toString(),
                            args.place.id_place,
                            "",
                        )
                    )
                }
            }
            totalQuestions.forEach { v ->
                if (!answersList.any { v  == it.question_id.toInt()}){
                    Snackbar.make(
                        binding.root,
                        "Debe llenar todos lo datos del formulario",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
            }
            postAnswers(answersList)
        }
    }

    private fun postAnswers(answersList: MutableList<AnswersEntity>) {
        Log.e("postAnswers: ", answersList.toString())
        var cantPostAnswers = 0
        answersList.forEach {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelAnswers.saveAnswer(it)
                        .collect { r ->
                            when (r) {
                                is Result.Loading -> {
                                }
                                is Result.Success -> {
                                    cantPostAnswers++
                                    if (cantPostAnswers == answersList.size) {
                                        Snackbar.make(
                                            binding.root,
                                            "Se ha guardado satisfactoriamente",
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        findNavController().popBackStack()
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
    }

    private fun validateForm(): MutableList<Boolean> {
        val validations = mutableListOf<Boolean>()
        for (i in optionList.indices) {
            if (optionList[i].second == "editText") {
                if ((optionList[i].first as EditText).text.toString().isEmpty()) {
                    (optionList[i].first as EditText).error = "Este campo es obligatorio"
                    validations.add(false)
                } else {
                    validations.add(true)
                }
            }
        }
        return validations
    }

}