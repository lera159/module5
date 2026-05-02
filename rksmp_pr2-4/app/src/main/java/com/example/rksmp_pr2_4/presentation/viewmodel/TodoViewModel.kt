package com.example.rksmp_pr2_4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rksmp_pr2_4.domain.model.Todo
import com.example.rksmp_pr2_4.domain.usecase.GetTasksUseCase
import com.example.rksmp_pr2_4.domain.usecase.ManageTaskUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TodoViewModel(
	private val getTasksUseCase: GetTasksUseCase,
	private val manageTaskUseCase: ManageTaskUseCase
) : ViewModel() {

	val tasks: StateFlow<List<Todo>> = getTasksUseCase()
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

	private val _isCompletedColorEnabled = MutableStateFlow(true)
	val isCompletedColorEnabled: StateFlow<Boolean> = _isCompletedColorEnabled.asStateFlow()

	private val _completedBackgroundColor = MutableStateFlow(0xFF4CAF50)
	val completedBackgroundColor: StateFlow<Long> = _completedBackgroundColor.asStateFlow()

	init {
		observePreferences()
	}

	private fun observePreferences() {
		viewModelScope.launch {
			manageTaskUseCase.let {
				// Наблюдаем настройки
			}
		}
	}

	fun addTask(title: String, description: String) {
		viewModelScope.launch {
			manageTaskUseCase.insert(
				Todo(
					title = title,
					description = description
				)
			)
		}
	}

	fun updateTask(todo: Todo) {
		viewModelScope.launch {
			manageTaskUseCase.update(todo)
		}
	}

	fun deleteTask(todo: Todo) {
		viewModelScope.launch {
			manageTaskUseCase.delete(todo)
		}
	}

	fun toggleComplete(todo: Todo) {
		viewModelScope.launch {
			manageTaskUseCase.toggleComplete(todo)
		}
	}

	fun setCompletedColorEnabled(enabled: Boolean) {
		viewModelScope.launch {
			manageTaskUseCase.setCompletedColorEnabled(enabled)
			_isCompletedColorEnabled.value = enabled
		}
	}

	fun setCompletedBackgroundColor(color: Long) {
		viewModelScope.launch {
			manageTaskUseCase.setCompletedBackgroundColor(color)
			_completedBackgroundColor.value = color
		}
	}
}