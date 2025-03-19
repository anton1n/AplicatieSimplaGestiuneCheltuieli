package com.sd.laborator.service


import com.sd.laborator.models.Cheltuiala
import com.sd.laborator.repository.CheltuialaRepository
import com.sd.laborator.services.AccountService
import org.springframework.stereotype.Service

@Service
class CheltuialaService(private val cheltuialaRepository: CheltuialaRepository,
                        private val accountService: AccountService
) {

    fun adaugaCheltuiala(cheltuiala: Cheltuiala): Cheltuiala {
        if (!accountService.decrementBudget(cheltuiala.idMembru, cheltuiala.cost)) {
            throw IllegalArgumentException("Buget insuficient")
        }
        return cheltuialaRepository.save(cheltuiala)
    }

    fun getCheltuieliPentruMembru(idMembru: Int): List<Cheltuiala> {
        return cheltuialaRepository.findByIdMembru(idMembru)
    }

    fun actualizeazaCheltuiala(id: Int, cheltuialaNoua: Cheltuiala): Cheltuiala? {
        return cheltuialaRepository.findById(id).map { existing ->
            val updated = existing.copy(
                idMembru = cheltuialaNoua.idMembru,
                tip = cheltuialaNoua.tip,
                numeCheltuiala = cheltuialaNoua.numeCheltuiala,
                cost = cheltuialaNoua.cost
            )
            cheltuialaRepository.save(updated)
        }.orElse(null)
    }

    fun stergeCheltuiala(id: Int) {
        cheltuialaRepository.deleteById(id)
    }
}
