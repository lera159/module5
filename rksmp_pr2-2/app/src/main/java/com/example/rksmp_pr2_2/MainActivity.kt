package com.example.rksmp_pr2_2

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.LocalImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.rksmp_pr2_2.ui.theme.Rksmp_pr22Theme
import com.google.accompanist.permissions.*
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			Rksmp_pr22Theme {
				PhotoGalleryApp()
			}
		}
	}
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoGalleryApp() {
	val context = LocalContext.current
	val viewModel: PhotoGalleryViewModel = viewModel()
	val photos by viewModel.photos.collectAsStateWithLifecycle()  // правильный способ
	val snackbarHostState = remember { SnackbarHostState() }
	val coroutineScope = rememberCoroutineScope()  // <-- выносим сюда один раз!

	// Camera permission
	val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

	// Storage permission (only for Android < 10)
	val storagePermissionState = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
		rememberPermissionState(Manifest.permission.WRITE_EXTERNAL_STORAGE)
	} else {
		null
	}

	val hasCameraPermission = cameraPermissionState.status.isGranted
	val hasStoragePermission = storagePermissionState?.status?.isGranted ?: true
	val allPermissionsGranted = hasCameraPermission && hasStoragePermission

	// Camera launcher
	val cameraLauncher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.TakePicture(),
		onResult = { success ->
			if (success) {
				viewModel.currentPhotoUri?.let { uri ->
					viewModel.savePhotoFromUri(context, uri)
					viewModel.clearCurrentPhotoUri()
				}
			} else {
				Toast.makeText(context, "Фото не было сохранено", Toast.LENGTH_SHORT).show()
			}
		}
	)

	// Request permissions when needed
	LaunchedEffect(Unit) {
		if (!hasCameraPermission) {
			cameraPermissionState.launchPermissionRequest()
		}
		if (storagePermissionState != null && !hasStoragePermission) {
			storagePermissionState.launchPermissionRequest()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		floatingActionButton = {
			if (allPermissionsGranted) {
				FloatingActionButton(
					onClick = {
						val photoFile = viewModel.createImageFile(context)
						if (photoFile != null) {
							val uri = FileProvider.getUriForFile(
								context,
								"${context.packageName}.fileprovider",
								photoFile
							)
							viewModel.setCurrentPhotoUri(uri)
							cameraLauncher.launch(uri)
						}
					},
					containerColor = MaterialTheme.colorScheme.primary,
					contentColor = MaterialTheme.colorScheme.onPrimary
				) {
					Icon(Icons.Default.CameraAlt, contentDescription = "Сделать фото")
				}
			}
		}
	) { paddingValues ->
		Box(
			modifier = Modifier
				.fillMaxSize()
				.padding(paddingValues)
		) {
			if (!allPermissionsGranted) {
				PermissionRequestScreen(
					onRequestCamera = { cameraPermissionState.launchPermissionRequest() },
					onRequestStorage = { storagePermissionState?.launchPermissionRequest() }
				)
			} else {
				PhotoGridScreen(
					photos = photos,
					onExportToGallery = { photoFile ->
						viewModel.exportPhotoToGallery(context, photoFile) { success ->
							// Используем внешний coroutineScope
							coroutineScope.launch {
								val message = if (success) "Фото добавлено в галерею" else "Ошибка при экспорте фото"
								snackbarHostState.showSnackbar(message)
							}
						}
					},
					onTakeFirstPhoto = {
						val photoFile = viewModel.createImageFile(context)
						if (photoFile != null) {
							val uri = FileProvider.getUriForFile(
								context,
								"${context.packageName}.fileprovider",
								photoFile
							)
							viewModel.setCurrentPhotoUri(uri)
							cameraLauncher.launch(uri)
						}
					}
				)
			}
		}
	}
}

@Composable
fun PermissionRequestScreen(
	onRequestCamera: () -> Unit,
	onRequestStorage: (() -> Unit)?
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			painter = painterResource(id = android.R.drawable.ic_dialog_alert),
			contentDescription = null,
			modifier = Modifier.size(64.dp),
			tint = MaterialTheme.colorScheme.primary
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = "Для работы приложения необходимы разрешения",
			style = MaterialTheme.typography.headlineSmall
		)
		Spacer(modifier = Modifier.height(8.dp))
		Text(
			text = "Камера - для создания фотографий\nХранилище - для сохранения фото",
			style = MaterialTheme.typography.bodyMedium
		)
		Spacer(modifier = Modifier.height(24.dp))

		Button(onClick = onRequestCamera) {
			Text("Разрешить доступ к камере")
		}

		if (onRequestStorage != null) {
			Spacer(modifier = Modifier.height(8.dp))
			Button(onClick = onRequestStorage) {
				Text("Разрешить доступ к хранилищу")
			}
		}
	}
}

@Composable
fun PhotoGridScreen(
	photos: List<File>,
	onExportToGallery: (File) -> Unit,
	onTakeFirstPhoto: () -> Unit
) {
	if (photos.isEmpty()) {
		EmptyGalleryScreen(onTakeFirstPhoto)
	} else {
		LazyVerticalGrid(
			columns = GridCells.Fixed(3),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
			modifier = Modifier.fillMaxSize()
		) {
			items(photos, key = { it.absolutePath }) { photoFile ->
				PhotoGridItem(
					photoFile = photoFile,
					onExport = { onExportToGallery(photoFile) }
				)
			}
		}
	}
}

@Composable
fun EmptyGalleryScreen(onTakePhoto: () -> Unit) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp),
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center
	) {
		Icon(
			imageVector = Icons.Default.CameraAlt,
			contentDescription = null,
			modifier = Modifier.size(80.dp),
			tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
		)
		Spacer(modifier = Modifier.height(16.dp))
		Text(
			text = "У вас пока нет фото",
			style = MaterialTheme.typography.headlineSmall,
			color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
		)
		Spacer(modifier = Modifier.height(8.dp))
		Button(
			onClick = onTakePhoto,
			colors = ButtonDefaults.buttonColors(
				containerColor = MaterialTheme.colorScheme.primary
			)
		) {
			Icon(Icons.Default.Add, contentDescription = null)
			Spacer(modifier = Modifier.width(8.dp))
			Text("Сделать первое фото")
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoGridItem(
	photoFile: File,
	onExport: () -> Unit
) {
	var showMenu by remember { mutableStateOf(false) }
	val context = LocalContext.current
	val imageLoader = LocalImageLoader.current

	Box(
		modifier = Modifier
			.aspectRatio(1f)
			.clip(RoundedCornerShape(8.dp))
			.clickable { showMenu = true }
	) {
		AsyncImage(
			model = ImageRequest.Builder(context)
				.data(photoFile)
				.size(Size.ORIGINAL)
				.build(),
			contentDescription = "Фото",
			imageLoader = imageLoader,
			modifier = Modifier.fillMaxSize(),
			contentScale = ContentScale.Crop
		)

		if (showMenu) {
			DropdownMenu(
				expanded = showMenu,
				onDismissRequest = { showMenu = false }
			) {
				DropdownMenuItem(
					text = { Text("Экспортировать в галерею") },
					onClick = {
						onExport()
						showMenu = false
					}
				)
			}
		}
	}
}