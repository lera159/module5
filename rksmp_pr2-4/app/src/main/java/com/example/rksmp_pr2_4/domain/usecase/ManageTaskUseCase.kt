package com.example.rksmp_pr2_4.domain.usecase

import com.example.rksmp_pr2_4.domain.model.Todo
import com.example.rksmp_pr2_4.domain.repository.TodoRepository

class ManageTaskUseCase(private val repository: TodoRepository) {

	suspend fun insert(todo: Todo): Long {
		return repository.insert(todo)
	}

	suspend fun update(todo: Todo) {
		repository.update(todo)
	}

	suspend fun delete(todo: Todo) {
		repository.delete(todo)
	}

	suspend fun toggleComplete(todo: Todo) {
		repository.update(todo.copy(isCompleted = !todo.isCompleted))
	}

	suspend fun setCompletedColorEnabled(enabled: Boolean) {
		repository.setCompletedColorEnabled(enabled)
	}

	suspend fun setCompletedBackgroundColor(color: Long) {
		repository.setCompletedBackgroundColor(color)
	}
}