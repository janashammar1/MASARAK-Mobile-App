
package sa.edu.iau.cshj.masarak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import sa.edu.iau.cshj.masarak.data.dao.ProfileSupervisorRow
import sa.edu.iau.cshj.masarak.data.entity.TrainingRecordEntity
import sa.edu.iau.cshj.masarak.data.entity.UserEntity
import sa.edu.iau.cshj.masarak.data.repository.ChangePasswordResult
import sa.edu.iau.cshj.masarak.data.repository.ProfileLinkResult
import sa.edu.iau.cshj.masarak.data.repository.ProfileRepository
import sa.edu.iau.cshj.masarak.ui.screens.profile.ProfilePerson
import sa.edu.iau.cshj.masarak.ui.screens.profile.ProfileRole

data class ProfileUiState(
    val isLoading: Boolean = false,
    val person: ProfilePerson? = null,
    val recordId: Long? = null,
    val pendingRequests: List<ProfileSupervisorRow> = emptyList(),
    val processingLinkId: Long? = null,
    val isChangingPassword: Boolean = false,
    val passwordChanged: Boolean = false,
    val error: String? = null,
    val actionMessage: String? = null,
    val passwordError: String? = null
)

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    private var currentUserId: Long? = null
    private var loadJob: Job? = null
    private var linkJob: Job? = null
    private var passwordJob: Job? = null

    private var loadVersion = 0L

    @OptIn(ExperimentalCoroutinesApi::class)
    fun loadProfile(userId: Long) {
        if (
            currentUserId == userId &&
            loadJob?.isActive == true
        ) {
            return
        }

        loadVersion++
        val version = loadVersion

        loadJob?.cancel()
        linkJob?.cancel()
        passwordJob?.cancel()

        currentUserId = userId.takeIf { it > 0L }

        _uiState.value = ProfileUiState(isLoading = true)

        if (userId <= 0L) {
            _uiState.value = ProfileUiState(
                error = "Please log in again."
            )
            return
        }

        loadJob = viewModelScope.launch {
            try {
                repository.observeUser(userId)
                    .flatMapLatest { user ->
                        val role = parseRole(user?.role)

                        when {
                            user == null -> flowOf(
                                ProfileUiState(
                                    error = "Account not found."
                                )
                            )

                            role == null -> flowOf(
                                ProfileUiState(
                                    error = "Unsupported account role."
                                )
                            )

                            role == ProfileRole.STUDENT ->
                                repository.observeTrainingRecords(userId)
                                    .flatMapLatest { records ->
                                        val record = pickRecord(records)

                                        if (record == null) {
                                            flowOf(
                                                ProfileUiState(
                                                    person = toPerson(
                                                        user,
                                                        role
                                                    )
                                                )
                                            )
                                        } else {
                                            repository.observeSupervisorLinks(
                                                studentId = userId,
                                                recordId = record.recordId
                                            ).combine(flowOf(user)) {
                                                    links, account ->

                                                studentState(
                                                    account,
                                                    record.recordId,
                                                    links
                                                )
                                            }
                                        }
                                    }

                            else -> {
                                val oppositeRole =
                                    if (role == ProfileRole.FIELD) {
                                        "academic"
                                    } else {
                                        "field"
                                    }

                                combine(
                                    repository.observeLinkedStudentsCount(
                                        userId
                                    ),
                                    repository.observeLinkedSupervisorsCount(
                                        userId,
                                        oppositeRole
                                    )
                                ) { peopleCount, supervisorsCount ->
                                    ProfileUiState(
                                        person = toPerson(user, role).copy(
                                            linkedPeople = peopleCount,
                                            linkedSupervisors =
                                                supervisorsCount
                                        )
                                    )
                                }
                            }
                        }
                    }
                    .collect { loaded ->
                        if (version != loadVersion) return@collect

                        val previous = _uiState.value

                        _uiState.value = loaded.copy(
                            processingLinkId =
                                previous.processingLinkId,
                            isChangingPassword =
                                previous.isChangingPassword,
                            passwordChanged =
                                previous.passwordChanged,
                            actionMessage =
                                previous.actionMessage,
                            passwordError =
                                previous.passwordError
                        )
                    }

            } catch (cancelled: CancellationException) {
                throw cancelled

            } catch (e: Exception) {
                if (version == loadVersion) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Unable to load your profile. Please retry."
                    )
                }
            }
        }
    }

    fun acceptRequest(linkId: Long) {
        respondToRequest(linkId, accept = true)
    }

    fun declineRequest(linkId: Long) {
        respondToRequest(linkId, accept = false)
    }

    private fun respondToRequest(
        linkId: Long,
        accept: Boolean
    ) {
        val state = _uiState.value

        if (state.processingLinkId != null) return

        val userId = currentUserId ?: return
        val recordId = state.recordId ?: return

        if (
            state.person?.role != ProfileRole.STUDENT ||
            state.pendingRequests.none { it.linkId == linkId }
        ) {
            _uiState.value = state.copy(
                actionMessage = "This request is no longer available."
            )
            return
        }

        val version = loadVersion

        _uiState.value = state.copy(
            processingLinkId = linkId,
            actionMessage = null
        )

        linkJob = viewModelScope.launch {
            try {
                val result = if (accept) {
                    repository.acceptLinkRequest(
                        userId,
                        recordId,
                        linkId
                    )
                } else {
                    repository.declineLinkRequest(
                        userId,
                        recordId,
                        linkId
                    )
                }

                if (version != loadVersion) return@launch

                val message = when (result) {
                    ProfileLinkResult.SUCCESS ->
                        if (accept) {
                            "Link request accepted."
                        } else {
                            "Link request declined."
                        }

                    ProfileLinkResult.INVALID_REQUEST ->
                        "Invalid link request."

                    ProfileLinkResult.REQUEST_NOT_UPDATED ->
                        "The request changed or cannot be processed. Check your current links."
                }

                _uiState.value = _uiState.value.copy(
                    actionMessage = message
                )

            } catch (cancelled: CancellationException) {
                throw cancelled

            } catch (e: Exception) {
                if (version == loadVersion) {
                    _uiState.value = _uiState.value.copy(
                        actionMessage = "Unable to save. Please try again."
                    )
                }

            } finally {
                if (version == loadVersion) {
                    _uiState.value = _uiState.value.copy(
                        processingLinkId = null
                    )
                }
            }
        }
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ) {
        if (_uiState.value.isChangingPassword) return

        val userId = currentUserId ?: return

        val validationError = when {
            currentPassword.isBlank() ->
                "Enter your current password."

            newPassword.isBlank() || newPassword.length < 8 ->
                "New password must contain at least 8 characters."

            newPassword != confirmPassword ->
                "Passwords do not match."

            else -> null
        }

        _uiState.value = _uiState.value.copy(
            passwordError = validationError,
            passwordChanged = false
        )

        if (validationError != null) return

        val version = loadVersion

        _uiState.value = _uiState.value.copy(
            isChangingPassword = true
        )

        passwordJob = viewModelScope.launch {
            try {
                val result = repository.changePassword(
                    userId,
                    currentPassword,
                    newPassword
                )

                if (version != loadVersion) return@launch

                val message = when (result) {
                    ChangePasswordResult.SUCCESS -> null

                    ChangePasswordResult.USER_NOT_FOUND ->
                        "Account not found. Please log in again."

                    ChangePasswordResult.INCORRECT_CURRENT_PASSWORD ->
                        "Current password is incorrect."

                    ChangePasswordResult.INVALID_NEW_PASSWORD ->
                        "New password must contain at least 8 characters."

                    ChangePasswordResult.SAME_PASSWORD ->
                        "Choose a password different from your current one."

                    ChangePasswordResult.ACCOUNT_CHANGED ->
                        "Account details changed. Please try again."
                }

                _uiState.value = _uiState.value.copy(
                    passwordError = message,
                    passwordChanged =
                        result == ChangePasswordResult.SUCCESS
                )

            } catch (cancelled: CancellationException) {
                throw cancelled

            } catch (e: Exception) {
                if (version == loadVersion) {
                    _uiState.value = _uiState.value.copy(
                        passwordError =
                            "Unable to change password. Please try again."
                    )
                }

            } finally {
                if (version == loadVersion) {
                    _uiState.value = _uiState.value.copy(
                        isChangingPassword = false
                    )
                }
            }
        }
    }

    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(
            actionMessage = null
        )
    }

    fun clearPasswordFeedback() {
        if (_uiState.value.isChangingPassword) return

        _uiState.value = _uiState.value.copy(
            passwordError = null,
            passwordChanged = false
        )
    }

    fun clearProfile() {
        loadVersion++

        loadJob?.cancel()
        linkJob?.cancel()
        passwordJob?.cancel()

        currentUserId = null
        _uiState.value = ProfileUiState()
    }

    private fun studentState(
        user: UserEntity,
        recordId: Long,
        links: List<ProfileSupervisorRow>
    ): ProfileUiState {
        val accepted = links.filter {
            it.status.trim().equals("accepted", ignoreCase = true)
        }

        val field = accepted.firstOrNull {
            parseRole(it.supervisorRole) == ProfileRole.FIELD
        }

        val academic = accepted.firstOrNull {
            parseRole(it.supervisorRole) == ProfileRole.ACADEMIC
        }

        val pending = links.filter {
            it.status.trim().equals("pending", ignoreCase = true) &&
                    parseRole(it.supervisorRole) in
                    listOf(ProfileRole.FIELD, ProfileRole.ACADEMIC)
        }

        return ProfileUiState(
            person = toPerson(user, ProfileRole.STUDENT).copy(
                fieldSupervisor = field?.supervisorName,
                fieldOrganization = field?.organization,
                academicSupervisor = academic?.supervisorName,
                pendingAcademicRequest = pending.firstOrNull {
                    parseRole(it.supervisorRole) ==
                            ProfileRole.ACADEMIC
                }?.supervisorName
            ),
            recordId = recordId,
            pendingRequests = pending
        )
    }

    private fun toPerson(
        user: UserEntity,
        role: ProfileRole
    ): ProfilePerson =
        ProfilePerson(
            name = user.fullName,
            email = user.email,
            initials = user.fullName.trim()
                .split(Regex("\\s+"))
                .filter { it.isNotBlank() && !it.endsWith(".") }
                .take(2)
                .joinToString("") {
                    it.first().uppercaseChar().toString()
                },
            role = role,
            organization = user.workOrganization
        )

    private fun parseRole(value: String?): ProfileRole? =
        when (value?.trim()?.lowercase(Locale.ROOT)) {
            "student" -> ProfileRole.STUDENT
            "field", "field supervisor" -> ProfileRole.FIELD
            "academic", "academic supervisor" -> ProfileRole.ACADEMIC
            else -> null
        }

    private fun pickRecord(
        records: List<TrainingRecordEntity>
    ): TrainingRecordEntity? {
        val now = System.currentTimeMillis()

        val current = records.filter {
            it.startDate <= now &&
                    (it.endDate == null || it.endDate >= now)
        }

        return (current.ifEmpty { records })
            .maxWithOrNull(
                compareBy<TrainingRecordEntity> { it.startDate }
                    .thenBy { it.recordId }
            )
    }
}