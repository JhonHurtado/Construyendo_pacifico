package com.example.proyectopacifico.ui.places

import android.app.AlertDialog
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getColorStateList
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.core.hide
import com.example.proyectopacifico.core.show
import com.example.proyectopacifico.data.localdb.AppDatabase
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.rest.RetrofitClient
import com.example.proyectopacifico.databinding.ConfirmDeleteZoneBinding
import com.example.proyectopacifico.databinding.FragmentPlacesMenuFragmentBinding
import com.example.proyectopacifico.domain.place.PlaceRepoImpl
import com.example.proyectopacifico.domain.population.PopulationRepoImpl
import com.example.proyectopacifico.presentation.PlaceViewModel
import com.example.proyectopacifico.presentation.PlaceViewModelFactory
import com.example.proyectopacifico.presentation.PopulationViewModel
import com.example.proyectopacifico.presentation.PopulationViewModelFactory
import com.example.proyectopacifico.ui.modules.MenuModelsFragmentArgs
import com.example.proyectopacifico.ui.places.adapter.PlaceMenuAdapter
import com.example.proyectopacifico.ui.zones.ZonesMenuFragmentDirections
import com.example.proyectopacifico.ui.zones.adapter.ZonesMenuAdapter
import com.google.android.material.snackbar.Snackbar


class PlacesMenuFragmentFragment : Fragment(R.layout.fragment_places_menu_fragment) {
    private lateinit var binding: FragmentPlacesMenuFragmentBinding
    private val args by navArgs<PlacesMenuFragmentFragmentArgs>()

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
        binding = FragmentPlacesMenuFragmentBinding.bind(view)

        setData()
        clicks()
        obtainPlaces()

    }

    private fun setData() {
        binding.txtVMunicipality.text = args.population.municipality_name
        binding.txtVPopulatedCenter.text = args.population.populated_center_name
    }

    private fun clicks() {
        binding.btnRegisterNewPlace.setOnClickListener {
            val action =
                PlacesMenuFragmentFragmentDirections.actionPlacesMenuFragmentFragmentToPlaceCreateFragment(
                    args.population
                )
            findNavController().navigate(action)
        }
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.swList.setOnClickListener {
            if (binding.swList.isChecked) {
                binding.rvPlace.layoutManager = LinearLayoutManager(requireContext())
            } else {
                obtainPlaces()
            }
        }
    }

    private fun obtainPlaces() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModelPlace.getPlaces(args.population.id_population).collect {
                    when (it) {
                        is Result.Loading -> {}
                        is Result.Success -> {
                            if (it.data.isNullOrEmpty()) {
                                binding.noData.show()
                                binding.rvPlace.hide()
                            } else {
                                binding.noData.hide()
                                binding.rvPlace.show()
                            }
                            val adapter = PlaceMenuAdapter(
                                it.data,
                                viewModelPlace,
                                requireContext(),
                                onClickDelete = { place -> onClickDelete(place) })

                            val displayMetrics = DisplayMetrics()
                            val metrics = DisplayMetrics()
                            requireActivity().getWindowManager().getDefaultDisplay()
                                .getMetrics(metrics);
                            requireActivity().getWindowManager().defaultDisplay.getMetrics(
                                displayMetrics
                            )
                            val height = displayMetrics.heightPixels
                            val width = displayMetrics.widthPixels

                            if (width >= 2000) {
                                binding.rvPlace.layoutManager =
                                    GridLayoutManager(requireContext(), 8)
                            } else {
                                binding.rvPlace.layoutManager =
                                    GridLayoutManager(requireContext(), 3)
                            }

                            binding.rvPlace.adapter = adapter
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

    private fun onClickDelete(place: PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment) {

        val dialogBinding = ConfirmDeleteZoneBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogBinding.root)
        }.create()

        dialogBinding.tvStatement.text = "Esta seguro de querer eliminar esta sitio?"

        dialogBinding.textView8.text = "Desea eliminar este sitio?"
        dialogBinding.textView8.backgroundTintList =
            getColorStateList(requireContext(), R.color.blueDarkSena)

        dialogBinding.idBtnCancelar.setOnClickListener { dialog.dismiss() }
        dialogBinding.idBtnEliminarZona.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModelPlace.deletePlace(
                        PlaceEntity(
                            place.id_place,
                            place.population_id,
                            place.namePlace,
                        )
                    ).collect {
                        when (it) {
                            is Result.Loading -> {
                            }
                            is Result.Success -> {
                                Snackbar.make(
                                    binding.root,
                                    "Eliminado correctamente",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                obtainPlaces()
                            }
                            is Result.Failure -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al eliminar los datos",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                                Log.e("Error", "bind: ${it.exception}")
                            }
                        }
                    }
                }
            }
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }
}
