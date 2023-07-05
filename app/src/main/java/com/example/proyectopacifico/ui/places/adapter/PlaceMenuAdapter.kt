package com.example.proyectopacifico.ui.places.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopacifico.R
import com.example.proyectopacifico.core.Result
import com.example.proyectopacifico.core.hide
import com.example.proyectopacifico.core.show
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.entities.PlaceEntity
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.databinding.ConfirmDeleteZoneBinding
import com.example.proyectopacifico.databinding.ItemMenuPlaceBinding
import com.example.proyectopacifico.databinding.ItemMenuZonesBinding
import com.example.proyectopacifico.presentation.PlaceViewModel
import com.example.proyectopacifico.presentation.PopulationViewModel
import com.example.proyectopacifico.ui.places.PlacesMenuFragmentFragmentDirections
import com.example.proyectopacifico.ui.zones.ZonesMenuFragmentDirections
import com.example.proyectopacifico.ui.zones.adapter.ZonesMenuAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaceMenuAdapter (
    private val placeList: List<PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment>,
    private val viewModel: PlaceViewModel,
    private val context: Context,
    private val onClickDelete: (PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment) -> Unit
) :
    RecyclerView.Adapter<PlaceMenuAdapter.PlaceMenuViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceMenuViewHolder {
        val itemBinding =
            ItemMenuPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceMenuViewHolder(itemBinding, viewModel, context)
    }

    override fun onBindViewHolder(holder: PlaceMenuViewHolder, position: Int) {
        holder.bind(placeList[position], onClickDelete)
    }

    override fun getItemCount(): Int = placeList.size

    inner class PlaceMenuViewHolder(
        private val binding: ItemMenuPlaceBinding,
        private val viewModel: PlaceViewModel,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment,onClickDelete: (PlaceAndPopulationAndPopulatedCenterAndMunicipalityAndDepartment) -> Unit) {
            binding.txtPlace.text = place.namePlace
            binding.btnNextProcessPlace.setOnClickListener {
                val action = PlacesMenuFragmentFragmentDirections.actionPlacesMenuFragmentFragmentToMenuModelsFragment(place)
                Navigation.findNavController(binding.root).navigate(action)
            }
            binding.btnEditPlace.setOnClickListener {
                val action = PlacesMenuFragmentFragmentDirections.actionPlacesMenuFragmentToPlaceUpdateFragment(place)
                Navigation.findNavController(binding.root).navigate(action)
            }
            binding.btnDeletePlace.setOnClickListener { onClickDelete(place) }
        }

    }
}
