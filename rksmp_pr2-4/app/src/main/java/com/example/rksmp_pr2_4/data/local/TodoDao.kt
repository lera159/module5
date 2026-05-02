package com.example.rksmp_pr2_4.data.local

import androidx.room.*
import com.example.rksmp_pr2_4.data.model.TodoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

	@Query("SELECT * FROM todos ORDER BY createdAt DESC")
	fun getAllTodos(): Flow<List<TodoEntity>>

	@Query("SELECT * FROM todos WHERE id = :id")
	suspend fun getTodoById(id: Long): TodoEntity?

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insert(todo: TodoEntity): Long

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(todos: List<TodoEntity>)

	@Update
	suspend fun update(todo: TodoEntity)

	@Delete
	suspend fun delete(todo: TodoEntity)

	@Query("DELETE FROM todos WHERE id = :id")
	suspend fun deleteById(id: Long)
}