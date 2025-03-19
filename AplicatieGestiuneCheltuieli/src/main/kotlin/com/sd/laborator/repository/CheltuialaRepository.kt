package com.sd.laborator.repository


import com.sd.laborator.models.Cheltuiala
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CheltuialaRepository : JpaRepository<Cheltuiala, Int> {
    fun findByIdMembru(idMembru: Int): List<Cheltuiala>
}
