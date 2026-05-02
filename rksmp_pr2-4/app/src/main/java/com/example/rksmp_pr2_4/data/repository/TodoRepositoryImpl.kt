package com.example.rksmp_pr2_4.data.repository

import com.example.rksmp_pr2_4.data.local.TodoDao
import com.example.rksmp_pr2_4.data.model.TodoEntity
import com.example.rksmp_pr2_4.data.preferences.AppPreferences
import com.example.rksmp_pr2_4.domain.model.Todo
import com.example.rksmp_pr2_4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class TodoRepositoryImpl(
	private val todoDao: TodoDao,
	private val appPreferences: AppPreferences
) : TodoRepository {

	override fun getAllTodos(): Flow<List<Todo>> {
		return todoDao.getAllTodos().map { entities ->
			entities.map { it.toDomain() }
		}
	}

	override suspend fun getTodoById(id: Long): Todo? {
		return todoDao.getTodoById(id)?.toDomain()
	}

	override suspend fun insert(todo: Todo): Long {
		return todoDao.insert(todo.toEntity())
	}

	override suspend fun update(todo: Todo) {
		todoDao.update(todo.toEntity())
	}

	override suspend fun delete(todo: Todo) {
		todoDao.delete(todo.toEntity())
	}

	override fun isCompletedColorEnabled(): Flow<Boolean> {
		return appPreferences.isCompletedColorEnabled
	}

	override fun getCompletedBackgroundColor(): Flow<Long> {
		return appPreferences.completedBackgroundColor
	}

	override suspend fun setCompletedColorEnabled(enabled: Boolean) {
		appPreferences.setCompletedColorEnabled(enabled)
	}

	override suspend fun setCompletedBackgroundColor(color: Long) {
		appPreferences.setCompletedBackgroundColor(color)
	}

	private fun TodoEntity.toDomain(): Todo {
		return Todo(
			id = id,
			title = title,
			description = description,
			isCompleted = isCompleted,
			createdAt = createdAt
		)
	}

	private fun Todo.toEntity(): TodoEntity {
		return TodoEntity(
			id = id,
			title = title,
			description = description,
			isCompleted = isCompleted,
			createdAt = createdAt
		)
	}
}