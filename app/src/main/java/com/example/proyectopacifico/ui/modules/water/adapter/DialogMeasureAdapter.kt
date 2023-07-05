package com.example.proyectopacifico.ui.modules.water.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectopacifico.data.models.entities.MeasureEntity
import com.example.proyectopacifico.data.models.entities.relations.SampleParameterAndMeasure
import com.example.proyectopacifico.databinding.ItemAnalizedParametersWaterBinding
import com.example.proyectopacifico.databinding.ItemMeasureBinding

class DialogMeasureAdapter(
    private val sampleList: List<MeasureEntity>,
    private val onClickDelete: (Int,MeasureEntity) -> Unit
) :
    RecyclerView.Adapter<DialogMeasureViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogMeasureViewHolder {
        val itemBinding = ItemMeasureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DialogMeasureViewHolder(itemBinding, parent.context)
    }

    override fun onBindViewHolder(holder: DialogMeasureViewHolder, position: Int) {
        holder.bind(sampleList[position],onClickDelete)
    }

    override fun getItemCount(): Int = sampleList.size
}

class DialogMeasureViewHolder(
    private val binding: ItemMeasureBinding,
    private val context: Context
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MeasureEntity,onClickDelete: (Int,MeasureEntity) -> Unit) {
        binding.tvValue.text = item.value
        binding.ivDelete.setOnClickListener { onClickDelete(adapterPosition,item)  }
    }
}