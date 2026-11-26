package sa.edu.iau.cshj.masarak.ui.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.UserRepository
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> =
        _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> =
        _loginError.asStateFlow()

    private val _isLoggingIn = MutableStateFlow(false)
    val isLoggingIn: StateFlow<Boolean> =
        _isLoggingIn.asStateFlow()

    private var loginJob: Job? = null
    private var requestVersion = 0L

    fun clearLoginError() {
        _loginError.value = null
    }

    fun login(email: String, passwordInput: String) {
        if (_isLoggingIn.value) return

        val trimmedEmail = email.trim()

        _loginError.value = null

        if (trimmedEmail.isBlank()) {
            _loginError.value = "Please enter your email."
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _loginError.value = "Please enter a valid email address."
            return
        }

        if (passwordInput.isBlank()) {
            _loginError.value = "Please enter your password."
            return
        }

        val thisRequest = ++requestVersion

        _currentUser.value = null
        _isLoggingIn.value = true

        loginJob = viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    userRepository.getUserByEmail(trimmedEmail)
                }

                val passwordIsCorrect = if (user != null) {
                    withContext(Dispatchers.Default) {
                        PasswordHasher.verifyPassword(
                            password = passwordInput,
                            storedHash = user.passwordHash,
                            storedSalt = user.passwordSalt
                        )
                    }
                } else {
                    false
                }

                if (thisRequest != requestVersion) return@launch

                if (user == null || !passwordIsCorrect) {
                    _loginError.value = "Invalid email or password."
                    return@launch
                }

                val supportedRole = when (
                    user.role.trim().lowercase(Locale.ROOT)
                ) {
                    "student",
                    "field",
                    "field supervisor",
                    "academic",
                    "academic supervisor" -> true

                    else -> false
                }

                if (!supportedRole) {
                    _loginError.value =
                        "Your account role is not supported. Please contact support."
                    return@launch
                }

                _currentUser.value = user

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                if (thisRequest == requestVersion) {
                    _loginError.value =
                        "Unable to log in. Please try again."
                }

            } finally {
                if (thisRequest == requestVersion) {
                    _isLoggingIn.value = false
                }
            }
        }
    }

    fun logout() {
        requestVersion++

        loginJob?.cancel()
        loginJob = null

        _currentUser.value = null
        _loginError.value = null
        _isLoggingIn.value = false
    }
}