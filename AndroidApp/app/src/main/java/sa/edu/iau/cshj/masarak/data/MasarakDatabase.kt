
package sa.edu.iau.cshj.masarak.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import sa.edu.iau.cshj.masarak.data.converter.Converters
import sa.edu.iau.cshj.masarak.data.dao.*
import sa.edu.iau.cshj.masarak.data.entity.*

@Database(
    entities = [
        UserEntity::class,
        TrainingRecordEntity::class,
        TrainingWeekEntity::class,
        TaskEntity::class,
        TaskSubmissionEntity::class,
        WeeklyEvaluationEntity::class,
        TaskAttachmentEntity::class,
        TaskReviewEntity::class,
        SupervisorLinkEntity::class,
        ConversationEntity::class,
        ConversationMemberEntity::class,
        MessageEntity::class,
        MessageAttachmentEntity::class,
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MasarakDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun trainingDao(): TrainingDao
    abstract fun taskDao(): TaskDao
    abstract fun chatDao(): ChatDao
    abstract fun notificationDao(): NotificationDao

    companion object {

        @Volatile
        private var INSTANCE: MasarakDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE users ADD COLUMN passwordSalt TEXT"
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE UNIQUE INDEX IF NOT EXISTS
                    index_users_email ON users(email)
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): MasarakDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MasarakDatabase::class.java,
                    "masarak_database"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3
                    )
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
