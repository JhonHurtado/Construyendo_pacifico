package com.example.proyectopacifico.ui.places

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.FragmentPlaceCreateBinding
import com.example.proyectopacifico.domain.place.PlaceRepoImpl
import com.example.proyectopacifico.presentation.PlaceViewModel
import com.example.proyectopacifico.presentation.PlaceViewModelFactory
import com.google.android.material.snackbar.Snackbar


class PlaceCreateFragment : Fragment(R.layout.fragment_place_create) {

   private lateinit var binding: FragmentPlaceCreateBinding

   private val args by navArgs<PlaceCreateFragmentArgs>()

    private val viewModelPlace by viewModels<PlaceViewModel> {
        PlaceViewModelFactory(
            PlaceRepoImpl(
                AppDatabase.getDatabase(requireContext()).PlaceDao(),
                RetrofitClient.webService
            )
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPlaceCreateBinding.bind(view)

        clicks()

    }

    private fun clicks() {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnGuardar.setOnClickListener { validate() }

    }

    private fun validate() {
        val results = arrayOf(validateName())
        if (false in results) {
            return
        }
        savePlace()
    }

    private fun savePlace() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPlace.savePlace(
                    PlaceEntity(
                        0,
                        args.population.id_population,
                        binding.txtPlace.text.toString(),
                    )
                ).collect {
                    when (it) {
                        is Result.Loading -> {
                        }
                        is Result.Success -> {
                            findNavController().popBackStack()
                            Snackbar.make(
                                binding.root,
                                "Se guardo correctamente",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                        is Result.Failure -> {
                            Snackbar.make(
                                binding.root,
                                "Error al registrar un sitio",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            Log.e("Error", "sendUser: ${it.exception}")
                        }
                    }
                }
            }
        }
    }

    private fun validateName(): Boolean {
        return if (binding.txtPlace.text.isNullOrEmpty()) {
            binding.txtILPlace.error = "Este campo es obligatorio"
            false
        } else {
            binding.txtILPlace.error = null
            true
        }
    }

}