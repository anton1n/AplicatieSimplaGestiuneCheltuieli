package com.sd.laborator.services

import org.springframework.stereotype.Service
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Service
class EncryptionService {

    private val algorithm = "AES"
    private val secretKey = "1234567890123456".toByteArray()
    private val bCrypt = BCryptPasswordEncoder()

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretKey, algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val encrypted = cipher.doFinal(data.toByteArray())
        return Base64.getEncoder().encodeToString(encrypted)
    }

    fun decrypt(encryptedData: String): String {
        val cipher = Cipher.getInstance(algorithm)
        val keySpec = SecretKeySpec(secretKey, algorithm)
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val decoded = Base64.getDecoder().decode(encryptedData)
        val decrypted = cipher.doFinal(decoded)
        return String(decrypted)
    }

    fun hashPassword(username: String, password: String): String {
        val combined = username + password
        return bCrypt.encode(combined)
    }

    fun verifyPassword(username: String, rawPassword: String, hashed: String): Boolean {
        val combined = username + rawPassword
        return bCrypt.matches(combined, hashed)
    }
}