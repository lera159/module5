package com.example.rksmp_pr2_1.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rksmp_pr2_1.data.Note

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(
	existingNote: Note?,
	onSave: (title: String, content: String) -> Unit,
	onNavigateBack: () -> Unit
) {
	var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
	var content by remember(existingNote) { mutableStateOf(existingNote?.content ?: "") }
	val isNew = existingNote == null

	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text(if (isNew) "Новая запись" else "Редактирование") },
				navigationIcon = {
					IconButton(onClick = onNavigateBack) {
						Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
					}
				},
				actions = {
					TextButton(
						onClick = {
							if (content.isNotBlank()) {
								onSave(title, content)
								onNavigateBack()
							}
						},
						enabled = content.isNotBlank()
					) {
						Text("Сохранить")
					}
				}
			)
		}
	) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(16.dp)
		) {
			OutlinedTextField(
				value = title,
				onValueChange = { title = it },
				label = { Text("Заголовок (необязательно)") },
				modifier = Modifier.fillMaxWidth(),
				singleLine = true
			)
			Spacer(modifier = Modifier.height(16.dp))
			OutlinedTextField(
				value = content,
				onValueChange = { content = it },
				label = { Text("Текст заметки") },
				modifier = Modifier
					.fillMaxWidth()
					.weight(1f),
				minLines = 5
			)
		}
	}
}