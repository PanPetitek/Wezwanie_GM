package com.example.wezwanie_gm.ui.historia

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wezwanie_gm.databinding.ItemWezwanieBinding
import com.example.wezwanie_gm.models.Wezwanie

class HistoriaAdapter(private val lista: List<Wezwanie>) :
    RecyclerView.Adapter<HistoriaAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemWezwanieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Wezwanie) {
            binding.tvMaszyna.text = item.nazwaMaszyny
            binding.tvData.text = "${item.data} ${item.godzina}"
            binding.tvStatus.text = "Przyjęte przez: ${item.ktoZaakceptowal ?: "?"}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWezwanieBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    override fun getItemCount(): Int = lista.size
}
