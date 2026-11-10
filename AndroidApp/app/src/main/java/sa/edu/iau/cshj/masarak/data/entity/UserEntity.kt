package sa.edu.iau.cshj.masarak.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0L,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String? = null,
    val fullName: String,
    val role: String,
    val workOrganization: String? = null
)
