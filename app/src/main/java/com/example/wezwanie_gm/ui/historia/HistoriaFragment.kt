package com.example.wezwanie_gm.ui.historia

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wezwanie_gm.databinding.FragmentHistoriaBinding
import com.example.wezwanie_gm.models.Wezwanie
import com.example.wezwanie_gm.ui.wezwania.WezwaniaAdapter
import com.example.wezwanie_gm.utils.NetworkUtils

class HistoriaFragment : Fragment() {

    private var _binding: FragmentHistoriaBinding? = null
    private val binding get() = _binding!!
    private lateinit var historiaAdapter: WezwaniaAdapter
    private var rola: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoriaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPrefs = requireContext().getSharedPreferences("wezwanie_prefs", Context.MODE_PRIVATE)
        rola = sharedPrefs.getString("rola", "") ?: ""

        binding.rvHistoria.layoutManager = LinearLayoutManager(requireContext())

        historiaAdapter = WezwaniaAdapter(
            wezwania = mutableListOf(),
            onZaakceptujClicked = { _: Wezwanie -> } // ✅ poprawione
        )

        binding.rvHistoria.adapter = historiaAdapter

        odswiezHistorie()
    }

    private fun odswiezHistorie() {
        NetworkUtils.fetchHistoria(
            context = requireContext(),
            rola = rola,
            onSuccess = { lista: List<Wezwanie> ->
                historiaAdapter.updateData(lista)
                binding.tvEmptyHistoria.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            },
            onError = {
                binding.tvEmptyHistoria.visibility = View.VISIBLE
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}