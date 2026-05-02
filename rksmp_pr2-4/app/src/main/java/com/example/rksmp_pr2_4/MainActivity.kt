package com.example.rksmp_pr2_4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.rksmp_pr2_4.data.local.TodoDatabase
import com.example.rksmp_pr2_4.data.preferences.AppPreferences
import com.example.rksmp_pr2_4.data.repository.TodoRepositoryImpl
import com.example.rksmp_pr2_4.domain.usecase.GetTasksUseCase
import com.example.rksmp_pr2_4.domain.usecase.ManageTaskUseCase
import com.example.rksmp_pr2_4.navigation.AppNavigation
import com.example.rksmp_pr2_4.presentation.viewmodel.TodoViewModel

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		WindowCompat.setDecorFitsSystemWindows(window, false)

		setContent {
			val context = LocalContext.current
			val database = TodoDatabase.getDatabase(context)
			val preferences = AppPreferences(context)
			val repository = TodoRepositoryImpl(database.todoDao(), preferences)
			val getTasksUseCase = GetTasksUseCase(repository)
			val manageTaskUseCase = ManageTaskUseCase(repository)

			val viewModel: TodoViewModel = viewModel(
				factory = object : androidx.lifecycle.ViewModelProvider.Factory {
					override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
						@Suppress("UNCHECKED_CAST")
						return TodoViewModel(getTasksUseCase, manageTaskUseCase) as T
					}
				}
			)

			DarkTheme {
				val navController = rememberNavController()
				AppNavigation(navController = navController, viewModel = viewModel)
			}
		}
	}
}

@Composable
fun DarkTheme(content: @Composable () -> Unit) {
	val darkColorScheme = darkColorScheme(
		primary = Color(0xFF90CAF9),
		secondary = Color(0xFF80CBC4),
		tertiary = Color(0xFFA5D6A7),
		background = Color(0xFF121212),
		surface = Color(0xFF1E1E1E),
		surfaceVariant = Color(0xFF2C2C2C),
		onPrimary = Color.Black,
		onSecondary = Color.Black,
		onBackground = Color.White,
		onSurface = Color.White,
		onSurfaceVariant = Color(0xFFBDBDBD)
	)

	MaterialTheme(
		colorScheme = darkColorScheme,
		content = content
	)
}