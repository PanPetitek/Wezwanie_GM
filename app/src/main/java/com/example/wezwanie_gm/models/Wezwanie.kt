package com.example.wezwanie_gm.models
import com.example.wezwanie_gm.models.Wezwanie

data class Wezwanie(
    val id: Int,
    val nazwaMaszyny: String,
    val data: String,
    val godzina: String,
    val zaakceptowane: Boolean,
    val typZgloszenia: String,
    val ktoZaakceptowal: String?
)
