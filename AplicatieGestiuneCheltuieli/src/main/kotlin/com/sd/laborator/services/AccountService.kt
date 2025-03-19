package com.sd.laborator.services

import com.sd.laborator.models.Membru
import com.sd.laborator.repository.MembruRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val accountRepository: MembruRepository,
    private val encryptionService: EncryptionService
) {

    fun registerAccount(username: String, rawPassword: String, _buget: Double): Membru? {
        val encryptedUsername = encryptionService.encrypt(username)
        if (accountRepository.findByNume(encryptedUsername) != null) {
            return null
        }
        val hashedPassword = encryptionService.hashPassword(username, rawPassword)
        val account = Membru(nume = encryptedUsername, parola = hashedPassword, buget = _buget)
        return accountRepository.save(account)
    }

    fun authenticate(username: String, rawPassword: String): Boolean {
        val encryptedUsername = encryptionService.encrypt(username)
        val account = accountRepository.findByNume(encryptedUsername) ?: return false
        return encryptionService.verifyPassword(username, rawPassword, account.parola)
    }

    fun decrementBudget(idMembru: Int, cost: Double): Boolean {
        val optMembru = accountRepository.findById(idMembru)
        if (optMembru.isPresent) {
            val membru = optMembru.get()
            if (membru.buget >= cost) {
                membru.buget -= cost
                accountRepository.save(membru)
                return true
            }
        }
        return false
    }

    fun getBudgetForMembru(idMembru: Int): Double? {
        return accountRepository.findById(idMembru).orElse(null)?.buget
    }

    fun addBudgetForMembru(idMembru: Int, amount: Double): Boolean {
        val optMembru = accountRepository.findById(idMembru)
        if (optMembru.isPresent) {
            val membru = optMembru.get()
            membru.buget += amount
            accountRepository.save(membru)
            return true
        }
        return false
    }

    fun getAccount(username: String) : Membru?
    {
        return accountRepository.findByNume(encryptionService.encrypt(username))
    }
}
