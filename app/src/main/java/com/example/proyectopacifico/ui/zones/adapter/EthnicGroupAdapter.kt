package com.example.proyectopacifico.ui.zones.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopacifico.core.Constants
import com.example.proyectopacifico.data.models.entities.EthnicGroupEntity
import com.example.proyectopacifico.databinding.ItemEthnicGroupsBinding

class EthnicGroupAdapter(
    private val ethnicGroups: List<EthnicGroupEntity>,
    private val onClickDelete: (Int) -> Unit
) : RecyclerView.Adapter<EthnicGroupAdapter.EthnicGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EthnicGroupViewHolder {
        val binding = ItemEthnicGroupsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EthnicGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EthnicGroupViewHolder, position: Int) {
            holder.bind(ethnicGroups[position], onClickDelete)
    }

    override fun getItemCount(): Int = ethnicGroups.size

    inner class EthnicGroupViewHolder(private val binding: ItemEthnicGroupsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(ethnicGroups: EthnicGroupEntity, onClickDelete: (Int) -> Unit) {
            binding.txtName.text = ethnicGroups.ethnic_group_name
            binding.imgClose.setOnClickListener {  onClickDelete(adapterPosition) }
        }
    }
}
