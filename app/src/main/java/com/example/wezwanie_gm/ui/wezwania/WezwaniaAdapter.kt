package com.example.wezwanie_gm.ui.wezwania

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wezwanie_gm.databinding.ItemWezwanieBinding
import com.example.wezwanie_gm.models.Wezwanie

class WezwaniaAdapter(
    private var wezwania: MutableList<Wezwanie> = mutableListOf(),
    private val onZaakceptujClicked: (Wezwanie) -> Unit
) : RecyclerView.Adapter<WezwaniaAdapter.WezwanieViewHolder>() {

    inner class WezwanieViewHolder(private val binding: ItemWezwanieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Wezwanie) {
            binding.tvMaszyna.text = item.nazwaMaszyny
            binding.tvData.text = "${item.data} ${item.godzina}"
            binding.tvStatus.text = if (item.zaakceptowane) "Przyjęte" else "Oczekuje"

            if (!item.zaakceptowane) {
                binding.btnZaakceptuj.visibility = View.VISIBLE
                binding.btnZaakceptuj.setOnClickListener {
                    onZaakceptujClicked(item)
                }
            } else {
                binding.btnZaakceptuj.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WezwanieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWezwanieBinding.inflate(inflater, parent, false)
        return WezwanieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WezwanieViewHolder, position: Int) {
        holder.bind(wezwania[position])
    }

    override fun getItemCount(): Int = wezwania.size

    fun updateData(newData: List<Wezwanie>) {
        wezwania.clear()
        wezwania.addAll(newData)
        notifyDataSetChanged()
    }
}
