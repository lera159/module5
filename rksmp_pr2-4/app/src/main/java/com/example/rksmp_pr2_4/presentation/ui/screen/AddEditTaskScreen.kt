package com.example.rksmp_pr2_4.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rksmp_pr2_4.domain.model.Todo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
	existingTask: Todo? = null,
	onSave: (String, String) -> Unit,
	onNavigateBack: () -> Unit
) {
	var title by remember { mutableStateOf(existingTask?.title ?: "") }
	var description by remember { mutableStateOf(existingTask?.description ?: "") }

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(if (existingTask != null) "Редактировать" else "Новая задача") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.surface
				)
			)
		}
	) { innerPadding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(innerPadding)
				.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			OutlinedTextField(
				value = title,
				onValueChange = { title = it },
				label = { Text("Название") },
				singleLine = true,
				modifier = Modifier.fillMaxWidth()
			)

			OutlinedTextField(
				value = description,
				onValueChange = { description = it },
				label = { Text("Описание") },
				modifier = Modifier.fillMaxWidth(),
				maxLines = 5
			)

			Button(
				onClick = {
					if (title.isNotBlank()) {
						onSave(title, description)
						onNavigateBack()
					}
				},
				modifier = Modifier.fillMaxWidth()
			) {
				Text("Сохранить")
			}
		}
	}
}