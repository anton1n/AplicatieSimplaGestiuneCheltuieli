package com.sd.laborator.models

import javax.persistence.*

@Entity
data class Cheltuiala(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val idMembru: Int = 0,
    @Enumerated(EnumType.STRING)
    val tip: TipCheltuiala = TipCheltuiala.PERSONALE,
    val numeCheltuiala: String = "",
    val cost: Double = 0.0
)
