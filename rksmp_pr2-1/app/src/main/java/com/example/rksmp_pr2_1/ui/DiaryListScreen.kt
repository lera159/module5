package com.example.rksmp_pr2_1.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rksmp_pr2_1.data.Note

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DiaryListScreen(
	notes: List<Note>,
	onAddClick: () -> Unit,
	onNoteClick: (Note) -> Unit,
	onDeleteClick: (Note) -> Unit
) {
	Scaffold(
		topBar = {
			TopAppBar(
				title = { Text("Мой дневник") },
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.primaryContainer
				)
			)
		},
		floatingActionButton = {
			FloatingActionButton(onClick = onAddClick) {
				Icon(Icons.Filled.Add, contentDescription = "Новая запись")
			}
		}
	) { padding ->
		if (notes.isEmpty()) {
			Box(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding),
				contentAlignment = Alignment.Center
			) {
				Column(horizontalAlignment = Alignment.CenterHorizontally) {
					Text(
						"У вас пока нет записей",
						style = MaterialTheme.typography.headlineSmall,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Spacer(modifier = Modifier.height(8.dp))
					Text(
						"Нажмите +, чтобы создать первую",
						style = MaterialTheme.typography.bodyMedium,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
				}
			}
		} else {
			LazyColumn(
				modifier = Modifier
					.fillMaxSize()
					.padding(padding),
				contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				items(notes, key = { it.fileName }) { note ->
					var showMenu by remember { mutableStateOf(false) }
					Card(
						modifier = Modifier
							.fillMaxWidth()
							.combinedClickable(
								onClick = { onNoteClick(note) },
								onLongClick = { showMenu = true }
							),
						elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
					) {
						Column(modifier = Modifier.padding(16.dp)) {
							Text(
								text = note.formattedDate,
								fontSize = 12.sp,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
							Spacer(modifier = Modifier.height(4.dp))
							if (note.title.isNotBlank()) {
								Text(
									text = note.title,
									fontWeight = FontWeight.Bold,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis
								)
								Spacer(modifier = Modifier.height(2.dp))
							}
							Text(
								text = note.preview,
								maxLines = 2,
								overflow = TextOverflow.Ellipsis,
								color = MaterialTheme.colorScheme.onSurface
							)
						}
						DropdownMenu(
							expanded = showMenu,
							onDismissRequest = { showMenu = false }
						) {
							DropdownMenuItem(
								text = { Text("Удалить") },
								onClick = {
									showMenu = false
									onDeleteClick(note)
								},
								leadingIcon = {
									Icon(Icons.Filled.Delete, contentDescription = null)
								}
							)
						}
					}
				}
			}
		}
	}
}