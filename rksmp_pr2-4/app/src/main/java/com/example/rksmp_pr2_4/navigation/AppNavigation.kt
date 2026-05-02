package com.example.rksmp_pr2_4.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rksmp_pr2_4.presentation.ui.screen.AddEditTaskScreen
import com.example.rksmp_pr2_4.presentation.ui.screen.TodoListScreen
import com.example.rksmp_pr2_4.presentation.viewmodel.TodoViewModel

@Composable
fun AppNavigation(
	navController: NavHostController,
	viewModel: TodoViewModel
) {
	NavHost(
		navController = navController,
		startDestination = "todo_list"
	) {
		composable("todo_list") {
			TodoListScreen(
				tasks = viewModel.tasks.collectAsState().value,
				isCompletedColorEnabled = viewModel.isCompletedColorEnabled.collectAsState().value,
				completedBackgroundColor = viewModel.completedBackgroundColor.collectAsState().value,
				onToggleComplete = viewModel::toggleComplete,
				onDelete = viewModel::deleteTask,
				onEdit = { task ->
					navController.navigate("add_edit_task/${task.id}")
				},
				onAdd = {
					navController.navigate("add_edit_task/-1")
				},
				onToggleCompletedColor = viewModel::setCompletedColorEnabled
			)
		}

		composable(
			"add_edit_task/{taskId}",
			arguments = listOf(navArgument("taskId") { type = NavType.LongType })
		) { backStackEntry ->
			val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
			val existingTask = if (taskId != -1L) {
				viewModel.tasks.collectAsState().value.find { it.id == taskId }
			} else null

			AddEditTaskScreen(
				existingTask = existingTask,
				onSave = { title, description ->
					if (existingTask != null) {
						viewModel.updateTask(
							existingTask.copy(title = title, description = description)
						)
					} else {
						viewModel.addTask(title, description)
					}
				},
				onNavigateBack = { navController.popBackStack() }
			)
		}
	}
}