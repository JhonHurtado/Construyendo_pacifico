package com.example.proyectopacifico.ui.modules

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.proyectopacifico.R
import com.example.proyectopacifico.databinding.FragmentMenuModelsBinding


class MenuModelsFragment : Fragment(R.layout.fragment_menu_models) {
  private lateinit var binding: FragmentMenuModelsBinding
    private val args by navArgs<MenuModelsFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                super.onViewCreated(view, savedInstanceState)
                binding = FragmentMenuModelsBinding.bind(view)

        clicks()
    }
    private fun clicks() {
        binding.btnBackToMenu.setOnClickListener { findNavController().popBackStack(R.id.placesMenuFragment,false) }
        binding.btnCardWater.setOnClickListener {
            val action = MenuModelsFragmentDirections.actionMenuModelsFragmentToAnalyzeWaterFragment(args.place)
            findNavController().navigate(action)
        }
        binding.cvArchitecture.setOnClickListener {
            val action = MenuModelsFragmentDirections.actionMenuModelsFragmentToArchitectureMenuFragment(args.place)
            findNavController().navigate(action)
        }
    }
}