package com.example.rksmp_pr2_1.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rksmp_pr2_1.data.Note

@Composable
fun DiaryApp() {
	val darkTheme = isSystemInDarkTheme()
	MaterialTheme(
		colorScheme = if (darkTheme) darkColorScheme() else lightColorScheme()
	) {
		val navController = rememberNavController()
		val viewModel: DiaryViewModel = viewModel()
		val notes by viewModel.notes.collectAsState()

		NavHost(navController = navController, startDestination = "list") {
			composable("list") {
				DiaryListScreen(
					notes = notes,
					onAddClick = {
						navController.navigate("edit/new")
					},
					onNoteClick = { note ->
						navController.navigate("edit/${note.fileName}")
					},
					onDeleteClick = { note ->
						viewModel.deleteNote(note.fileName)
					}
				)
			}

			composable(
				route = "edit/{fileName}",
				arguments = listOf(navArgument("fileName") { type = NavType.StringType })
			) { backStackEntry ->
				val fileName = backStackEntry.arguments?.getString("fileName") ?: "new"
				val existingNote = if (fileName == "new") null
				else notes.find { it.fileName == fileName }
				NoteEditScreen(
					existingNote = existingNote,
					onSave = { title, content ->
						if (existingNote == null) {
							viewModel.addNote(title, content)
						} else {
							viewModel.updateNote(existingNote, title, content)
						}
					},
					onNavigateBack = { navController.popBackStack() }
				)
			}
		}
	}
}