package com.example.rksmp_pr2_2

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PhotoGalleryViewModel : ViewModel() {

	private val _photos = MutableStateFlow<List<File>>(emptyList())
	val photos: StateFlow<List<File>> = _photos.asStateFlow()

	private var _currentPhotoUri: Uri? = null
	val currentPhotoUri: Uri? get() = _currentPhotoUri

	private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

	init {
		loadPhotos()
	}

	fun loadPhotos() {
		viewModelScope.launch(Dispatchers.IO) {
			val picturesDir = getPicturesDirectory()
			val files = picturesDir?.listFiles { file ->
				file.isFile && file.name.endsWith(".jpg", ignoreCase = true)
			}?.sortedByDescending { it.lastModified() } ?: emptyList()
			_photos.value = files
		}
	}

	private fun getPicturesDirectory(): File? {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
			null // будет использован context.getExternalFilesDir
		} else {
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
		}
	}

	fun createImageFile(context: Context): File? {
		return try {
			val timeStamp = dateFormat.format(Date())
			val imageFileName = "IMG_${timeStamp}.jpg"
			val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
			storageDir?.mkdirs()
			File(storageDir, imageFileName)
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}

	fun setCurrentPhotoUri(uri: Uri) {
		_currentPhotoUri = uri
	}

	fun clearCurrentPhotoUri() {
		_currentPhotoUri = null
	}

	fun savePhotoFromUri(context: Context, uri: Uri) {
		viewModelScope.launch(Dispatchers.IO) {
			try {
				val timeStamp = dateFormat.format(Date())
				val fileName = "IMG_${timeStamp}.jpg"
				val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
				picturesDir?.mkdirs()
				val destFile = File(picturesDir, fileName)

				context.contentResolver.openInputStream(uri)?.use { input ->
					FileOutputStream(destFile).use { output ->
						input.copyTo(output)
					}
				}
				val currentList = _photos.value.toMutableList()
				currentList.add(0, destFile)
				_photos.value = currentList
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	fun exportPhotoToGallery(context: Context, photoFile: File, onResult: (Boolean) -> Unit) {
		viewModelScope.launch(Dispatchers.IO) {
			val success = runCatching {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
					val contentValues = ContentValues().apply {
						put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.name)
						put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
						put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/MyGallery")
						put(MediaStore.Images.Media.IS_PENDING, 1)
					}
					val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
					uri?.let {
						context.contentResolver.openOutputStream(it)?.use { output ->
							photoFile.inputStream().use { input -> input.copyTo(output) }
						}
						contentValues.clear()
						contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
						context.contentResolver.update(it, contentValues, null, null)
						true
					} ?: false
				} else {
					val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
					val destDir = File(picturesDir, "MyGallery")
					destDir.mkdirs()
					val destFile = File(destDir, photoFile.name)
					photoFile.inputStream().use { input ->
						FileOutputStream(destFile).use { output -> input.copyTo(output) }
					}
					android.media.MediaScannerConnection.scanFile(context, arrayOf(destFile.absolutePath), null, null)
					true
				}
			}.getOrElse { false }

			withContext(Dispatchers.Main) {
				onResult(success)
			}
		}
	}
}