package com.example.wezwanie_gm.ui.ustawienia

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.wezwanie_gm.databinding.FragmentUstawieniaBinding
import com.example.wezwanie_gm.utils.NetworkUtils

class UstawieniaFragment : Fragment() {

    private var _binding: FragmentUstawieniaBinding? = null
    private val binding get() = _binding!!

    private val prefsName = "wezwanie_prefs"
    private val keyName = "user_name"
    private val keyRole = "user_role"
    private val keyIp   = "server_ip"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUstawieniaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        // 1. Załaduj zapisane imię i rolę
        binding.etOperatorName.setText(prefs.getString(keyName, ""))
        when (prefs.getString(keyRole, "ustawiacz")) {
            "ustawiacz" -> binding.rbUstawiacz.isChecked = true
            "wozkowy"   -> binding.rbWozkowy.isChecked = true
        }

        // 2. Załaduj i ustaw IP serwera
        val savedIp = prefs.getString(keyIp, "192.168.31.182") ?: "192.168.31.182"
        binding.etServerIp.setText(savedIp)
        NetworkUtils.setServerIp(savedIp)     // ← od razu ustawiamy w NetworkUtils

        // --- Zapis IP ---
        binding.btnSaveIp.setOnClickListener {
            val newIp = binding.etServerIp.text.toString().trim()
            if (newIp.isNotEmpty()) {
                prefs.edit().putString(keyIp, newIp).apply()
                NetworkUtils.setServerIp(newIp)
                Toast.makeText(requireContext(), "Zapisano IP serwera", Toast.LENGTH_SHORT).show()
            }
        }

        // --- Zapis imienia + roli ---
        binding.btnSaveUser.setOnClickListener {
            val name = binding.etOperatorName.text.toString().trim()
            val role = if (binding.rbUstawiacz.isChecked) "ustawiacz" else "wozkowy"

            prefs.edit()
                .putString(keyName, name)
                .putString(keyRole, role)
                .apply()

            Toast.makeText(requireContext(), "Zapisano ustawienia użytkownika", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
