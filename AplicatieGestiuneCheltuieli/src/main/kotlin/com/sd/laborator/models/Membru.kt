package com.sd.laborator.models

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class Membru(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,
    val nume: String = "",
    //val prenume: String,
    var buget: Double = 0.0,
    val parola: String = ""
)
