package sa.edu.iau.cshj.masarak.data

import android.content.Context

object DatabaseProvider {
    @Volatile
    private var database: MasarakDatabase? = null

    fun initialize(context: Context) {
        if (database == null) {
            synchronized(this) {
                if (database == null) {
                    database = MasarakDatabase.getDatabase(context)
                }
            }
        }
    }

    val db: MasarakDatabase
        get() = database ?: throw IllegalStateException("DatabaseProvider not initialized. Call initialize(context) first.")
}
