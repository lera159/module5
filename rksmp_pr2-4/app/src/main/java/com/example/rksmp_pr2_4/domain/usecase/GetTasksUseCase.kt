package com.example.rksmp_pr2_4.domain.usecase

import com.example.rksmp_pr2_4.domain.model.Todo
import com.example.rksmp_pr2_4.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TodoRepository) {
	operator fun invoke(): Flow<List<Todo>> {
		return repository.getAllTodos()
	}
}