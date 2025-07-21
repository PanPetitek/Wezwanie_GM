package com.example.wezwanie_gm.ui.wezwania

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wezwanie_gm.databinding.FragmentWezwaniaBinding
import com.example.wezwanie_gm.models.Wezwanie
import com.example.wezwanie_gm.utils.NetworkUtils

class WezwaniaFragment : Fragment() {

    private var _binding: FragmentWezwaniaBinding? = null
    private val binding get() = _binding!!
    private lateinit var wezwanieAdapter: WezwaniaAdapter
    private var rola: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWezwaniaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicjalizacja RecyclerView
        binding.rvWezwania.layoutManager = LinearLayoutManager(requireContext())

        // Odczyt roli i użytkownika z SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("wezwanie_prefs", Context.MODE_PRIVATE)
        rola = sharedPrefs.getString("rola", "") ?: ""
        val uzytkownik = sharedPrefs.getString("uzytkownik", "Nieznany") ?: "Nieznany"

        // Inicjalizacja adaptera
        wezwanieAdapter = WezwaniaAdapter(
            wezwania = mutableListOf(),
            onZaakceptujClicked = { wezwanie: Wezwanie ->
                NetworkUtils.zatwierdzWezwanie(
                    id = wezwanie.id,
                    kto = uzytkownik,
                    onSuccess = {
                        Toast.makeText(requireContext(), "Wezwanie zaakceptowane", Toast.LENGTH_SHORT).show()
                        odswiezWezwania()
                    },
                    onError = {
                        Toast.makeText(requireContext(), "Błąd podczas akceptacji", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        )

        binding.rvWezwania.adapter = wezwanieAdapter

        // Pierwsze pobranie danych
        odswiezWezwania()
    }

    private fun odswiezWezwania() {
        NetworkUtils.fetchWezwania(
            rola = rola,
            onSuccess = { lista ->
                wezwanieAdapter.updateData(lista)
                binding.tvEmpty.visibility = if (lista.isEmpty()) View.VISIBLE else View.GONE
            },
            onError = {
                binding.tvEmpty.visibility = View.VISIBLE
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
