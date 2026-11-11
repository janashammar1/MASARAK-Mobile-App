package sa.edu.iau.cshj.masarak.data.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val ITERATIONS = 600_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16

    data class HashResult(val hash: String, val salt: String)

    fun hashPassword(password: String): HashResult {
        val saltBytes = ByteArray(SALT_LENGTH)
        SecureRandom().nextBytes(saltBytes)
        val saltString = Base64.encodeToString(saltBytes, Base64.NO_WRAP)
        val hashString = hashWithSalt(password, saltBytes)
        return HashResult(hashString, saltString)
    }

    fun verifyPassword(password: String, storedHash: String, storedSalt: String?): Boolean {
        if (storedSalt.isNullOrEmpty()) {
            // Backward-compatible fallback for legacy plain-text passwords (e.g. "password123")
            return password == storedHash
        }
        val saltBytes = try {
            Base64.decode(storedSalt, Base64.NO_WRAP)
        } catch (e: Exception) {
            return false
        }
        val computedHash = hashWithSalt(password, saltBytes)
        return computedHash == storedHash
    }

    private fun hashWithSalt(password: String, salt: ByteArray): String {
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance(ALGORITHM)
        val hashBytes = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }
}
