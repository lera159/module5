package com.example.rksmp_pr2_4.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rksmp_pr2_4.domain.model.Todo
import com.example.rksmp_pr2_4.presentation.ui.component.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
	tasks: List<Todo>,
	isCompletedColorEnabled: Boolean,
	completedBackgroundColor: Long,
	onToggleComplete: (Todo) -> Unit,
	onDelete: (Todo) -> Unit,
	onEdit: (Todo) -> Unit,
	onAdd: () -> Unit,
	onToggleCompletedColor: (Boolean) -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("TodoList") },
				actions = {
					Row(
						verticalAlignment = Alignment.CenterVertically,
						horizontalArrangement = Arrangement.spacedBy(8.dp)
					) {
						Text(
							"Цвет завершенных",
							style = MaterialTheme.typography.bodySmall
						)
						Switch(
							checked = isCompletedColorEnabled,
							onCheckedChange = onToggleCompletedColor
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onAdd) {
				Icon(Icons.Default.Add, contentDescription = "Добавить")
			}
		}
	) { innerPadding ->
		if (tasks.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding),
				contentAlignment = Alignment.Center
			) {
				Text("Нет задач", style = MaterialTheme.typography.bodyLarge)
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(innerPadding)
					.padding(horizontal = 16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp),
				contentPadding = PaddingValues(vertical = 16.dp)
			) {
				items(tasks, key = { it.id }) { task ->
					TaskItem(
						task = task,
						isCompletedColorEnabled = isCompletedColorEnabled,
						completedBackgroundColor = completedBackgroundColor,
						onToggleComplete = { onToggleComplete(task) },
						onDelete = { onDelete(task) },
						onEdit = { onEdit(task) }
					)
				}
			}
		}
	}
}