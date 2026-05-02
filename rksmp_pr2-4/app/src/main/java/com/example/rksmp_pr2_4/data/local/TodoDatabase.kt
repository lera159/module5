package com.example.rksmp_pr2_4.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.rksmp_pr2_4.data.model.TodoEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [TodoEntity::class], version = 1, exportSchema = false)
abstract class TodoDatabase : RoomDatabase() {

	abstract fun todoDao(): TodoDao

	companion object {
		@Volatile
		private var INSTANCE: TodoDatabase? = null

		fun getDatabase(context: Context): TodoDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					TodoDatabase::class.java,
					"todo_database"
				)
					.addCallback(object : Callback() {
						override fun onCreate(db: SupportSQLiteDatabase) {
							super.onCreate(db)
							INSTANCE?.let { database ->
								CoroutineScope(Dispatchers.IO).launch {
									populateDatabase(database, context)
								}
							}
						}
					})
					.build()
				INSTANCE = instance
				instance
			}
		}

		private suspend fun populateDatabase(db: TodoDatabase, context: Context) {
			try {
				val inputStream = context.assets.open("tasks.json")
				val jsonString = inputStream.bufferedReader().use { it.readText() }
				val type = object : TypeToken<List<ImportTask>>() {}.type
				val tasks: List<ImportTask> = Gson().fromJson(jsonString, type)

				val entities = tasks.map { task ->
					TodoEntity(
						title = task.title,
						description = task.description ?: "",
						isCompleted = task.isCompleted ?: false,
						createdAt = System.currentTimeMillis()
					)
				}
				db.todoDao().insertAll(entities)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}
}

data class ImportTask(
	val title: String,
	val description: String? = null,
	val isCompleted: Boolean? = false
)