package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.UserRepository
import sa.edu.iau.cshj.masarak.data.util.PasswordHasher

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun login(email: String, passwordInput: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null && PasswordHasher.verifyPassword(passwordInput, user.passwordHash, user.passwordSalt)) {
                    _currentUser.value = user
                    _loginError.value = null
                } else {
                    _loginError.value = "Invalid email or password"
                }
            } catch (e: Exception) {
                _loginError.value = e.localizedMessage ?: "Login failed"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginError.value = null
    }
}
