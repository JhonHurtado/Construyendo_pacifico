package com.example.proyectopacifico.ui.modules.architecture.characterization

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.OptionQuestionAndQuestion
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.QuestionsAndSubQuestions
import com.example.proyectopacifico.data.models.entities.arquitecture.questions.SubQuestion
import com.example.proyectopacifico.databinding.FragmentCharacterizationDetailBinding
import com.example.proyectopacifico.databinding.FragmentObservationDetailBinding
import com.example.proyectopacifico.domain.optionQuestion.OptionQuestionRepoImpl
import com.example.proyectopacifico.domain.question.QuestionRepoImpl
import com.example.proyectopacifico.presentation.OptionQuestionViewModel
import com.example.proyectopacifico.presentation.OptionQuestionViewModelFactory
import com.example.proyectopacifico.presentation.QuestionViewModel
import com.example.proyectopacifico.presentation.QuestionViewModelFactory
import com.example.proyectopacifico.ui.modules.architecture.observation.ObservationDetailFragmentArgs
import com.google.android.material.snackbar.Snackbar


class CharacterizationDetailFragment : Fragment(R.layout.fragment_characterization_detail) {
    private lateinit var binding: FragmentCharacterizationDetailBinding

    private val args by navArgs<CharacterizationDetailFragmentArgs>()

    private val viewModelQuestion by viewModels<QuestionViewModel> {
        QuestionViewModelFactory(
            QuestionRepoImpl(
                AppDatabase.getDatabase(requireContext()).QuestionDao()
            )
        )
    }

    private val viewModelOptionQuestion by viewModels<OptionQuestionViewModel> {
        OptionQuestionViewModelFactory(
            OptionQuestionRepoImpl(
                AppDatabase.getDatabase(requireContext()).OptionQuestionDao()
            )
        )
    }

    private val optionQuestionList = mutableListOf<OptionQuestionAndQuestion>()

    private var totalQuestions = mutableListOf<Int>()

    private val optionList = mutableListOf<Pair<Any, String>>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCharacterizationDetailBinding.bind(view)

        clicks()
        obtainQuestions()
    }

    private fun clicks() {
        binding.btnBackToMenu.setOnClickListener { findNavController().popBackStack() }
    }

    private fun obtainQuestions() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelQuestion.fetchAllQuestions(2).collect {
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
                                            setAnswers()
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
                                                    setAnswers()
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

    private fun setAnswers() {
        var i = 0
        optionQuestionList.forEachIndexed { index, optionQuestionAndQuestion ->
            if (args.answersAndQuestions.answersAndQuestions.any { optionQuestionAndQuestion.id_option_question  == it.option_question_id.toInt()}){
                if (optionList[index].second == "editText") {
                    (optionList[index].first as EditText).setText(args.answersAndQuestions.answersAndQuestions[i].open_answer)
                } else if (optionList[index].second == "checkbox") {
                    (optionList[index].first as CheckBox).isChecked = true
                }else if (optionList[index].second == "radiobutton") {
                    (optionList[index].first as RadioButton).isChecked = true
                }
                i++
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

            } else if (it.answer_type_id == 2) {
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
            } else if (it.answer_type_id == 1) {
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

}