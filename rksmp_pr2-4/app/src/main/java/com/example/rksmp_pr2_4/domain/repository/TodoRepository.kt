package com.example.rksmp_pr2_4.domain.repository

import com.example.rksmp_pr2_4.domain.model.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
	fun getAllTodos(): Flow<List<Todo>>
	suspend fun getTodoById(id: Long): Todo?
	suspend fun insert(todo: Todo): Long
	suspend fun update(todo: Todo)
	suspend fun delete(todo: Todo)
	fun isCompletedColorEnabled(): Flow<Boolean>
	fun getCompletedBackgroundColor(): Flow<Long>
	suspend fun setCompletedColorEnabled(enabled: Boolean)
	suspend fun setCompletedBackgroundColor(color: Long)
}