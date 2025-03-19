package com.sd.laborator.repository

import com.sd.laborator.models.Membru
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MembruRepository : JpaRepository<Membru, Int> {
    fun findByNume(nume: String): Membru?
}