package com.example.proyectopacifico.ui.zones.adapter

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
import com.example.proyectopacifico.data.models.entities.PopulationEntity
import com.example.proyectopacifico.data.models.entities.relations.PopulationAndPopulatedCenterAndMunicipalityAndDepartment
import com.example.proyectopacifico.databinding.ConfirmDeleteZoneBinding
import com.example.proyectopacifico.databinding.ItemMenuZonesBinding
import com.example.proyectopacifico.presentation.PopulationViewModel
import com.example.proyectopacifico.ui.zones.ZonesMenuFragmentDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.google.android.material.snackbar.Snackbar

class ZonesMenuAdapter(
    private val populationList: List<PopulationAndPopulatedCenterAndMunicipalityAndDepartment>,
    private val viewModel: PopulationViewModel,
    private val context: Context
) :
    RecyclerView.Adapter<ZonesMenuAdapter.ZonesMenuViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ZonesMenuViewHolder {
        val itemBinding =
            ItemMenuZonesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ZonesMenuViewHolder(itemBinding, viewModel, context)
    }

    override fun onBindViewHolder(holder: ZonesMenuViewHolder, position: Int) {
        holder.bind(populationList[position])
    }

    override fun getItemCount(): Int = populationList.size

    inner class ZonesMenuViewHolder(
        private val binding: ItemMenuZonesBinding,
        private val viewModel: PopulationViewModel,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(population: PopulationAndPopulatedCenterAndMunicipalityAndDepartment) {
            binding.txtNameMunicipalities.text = population.municipality_name
            binding.txtVereda.text = population.populated_center_name
            binding.btnNextProcessZone.setOnClickListener {
                val action = ZonesMenuFragmentDirections.actionZonesMenuFragmentToPlacesMenuFragmentFragment(population)
                Navigation.findNavController(binding.root).navigate(action)
            }
            binding.btnEditZone.setOnClickListener {
                val action = ZonesMenuFragmentDirections.actionZonesMenuFragmentToZonesFormUpdateFragment(population)
                Navigation.findNavController(binding.root).navigate(action)
            }
            binding.btnDeleteZone.setOnClickListener {
                val dialogBinding = ConfirmDeleteZoneBinding.inflate(LayoutInflater.from(context))

                val dialog = AlertDialog.Builder(context).apply {
                    setView(dialogBinding.root)
                }.create()

                dialogBinding.textView8.text = "Desea eliminar esta zona?"
                dialogBinding.textView8.setTextColor(R.color.blueDarkSena)

                dialogBinding.idBtnCancelar.setOnClickListener { dialog.dismiss() }
                dialogBinding.idBtnEliminarZona.setOnClickListener {
                    val scope = CoroutineScope(Dispatchers.Main)
                    scope.launch {
                        viewModel.deletePopulation(
                            PopulationEntity(
                                population.id_population,
                                population.populated_center_id,
                                population.longitude,
                                population.latitude,
                                population.photography,
                                population.inhabitants_number
                            )
                        ).collect {
                            when (it) {
                                is Result.Loading -> {
                                    binding.progressBar.show()
                                    binding.options.hide()
                                }
                                is Result.Success -> {
                                    binding.options.show()
                                    binding.progressBar.hide()
                                    Snackbar.make(
                                        binding.root,
                                        "Eliminado correctamente",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                    Navigation.findNavController(binding.root)
                                        .navigate(R.id.action_zonesMenuFragment_self)
                                }
                                is Result.Failure -> {
                                    binding.options.show()
                                    binding.progressBar.hide()
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
                    dialog.dismiss()
                }
                dialog.setCancelable(false)
                dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog.show()
            }
        }

    }
}
