package com.example.rksmp_pr2_1.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rksmp_pr2_1.data.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DiaryViewModel(application: Application) : AndroidViewModel(application) {

	private val _notes = MutableStateFlow<List<Note>>(emptyList())
	val notes: StateFlow<List<Note>> = _notes.asStateFlow()

	private val notesDir: File = application.filesDir

	init {
		loadAllNotes()
	}
	private fun loadAllNotes() {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val files = notesDir.listFiles { file ->
					file.isFile && file.name.endsWith(".txt")
				} ?: emptyArray()

				val loaded = files.mapNotNull { file ->
					parseNoteFile(file)
				}.sortedByDescending { it.timestamp } // новые сверху

				_notes.value = loaded
			}
		}
	}
	private fun parseNoteFile(file: File): Note? {
		return try {
			val lines = file.readLines()
			val title = if (lines.isNotEmpty()) lines[0] else ""
			val content = if (lines.size > 1) lines.subList(1, lines.size).joinToString("\n") else ""
			val timestamp = extractTimestamp(file.name)
			Note(
				fileName = file.name,
				title = title,
				content = content,
				timestamp = timestamp
			)
		} catch (e: Exception) {
			null
		}
	}
	private fun extractTimestamp(fileName: String): Long {
		val nameWithoutExt = fileName.removeSuffix(".txt")
		val underscoreIndex = nameWithoutExt.indexOf('_')
		return if (underscoreIndex > 0) {
			nameWithoutExt.substring(0, underscoreIndex).toLongOrNull() ?: System.currentTimeMillis()
		} else {
			System.currentTimeMillis()
		}
	}
	private fun createFileName(timestamp: Long, title: String): String {
		val safeTitle = title.take(20).replace(Regex("[\\\\/:*?\"<>|]"), "_") // убираем недопустимые символы
		return "${timestamp}_${safeTitle}.txt"
	}
	fun addNote(title: String, content: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val timestamp = System.currentTimeMillis()
				val fileName = createFileName(timestamp, title)
				val file = File(notesDir, fileName)
				file.bufferedWriter().use { writer ->
					writer.write(title)
					writer.newLine()
					writer.write(content)
				}
				val newNote = Note(
					fileName = fileName,
					title = title,
					content = content,
					timestamp = timestamp
				)
				val current = _notes.value.toMutableList()
				current.add(0, newNote)
				_notes.value = current
			}
		}
	}
	fun updateNote(existingNote: Note, newTitle: String, newContent: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val file = File(notesDir, existingNote.fileName)
				if (file.exists()) {
					file.bufferedWriter().use { writer ->
						writer.write(newTitle)
						writer.newLine()
						writer.write(newContent)
					}
					val updatedNote = existingNote.copy(
						title = newTitle,
						content = newContent
					)
					val current = _notes.value.toMutableList()
					val index = current.indexOfFirst { it.fileName == existingNote.fileName }
					if (index != -1) {
						current[index] = updatedNote
						_notes.value = current
					}
				}
			}
		}
	}
	fun deleteNote(fileName: String) {
		viewModelScope.launch {
			withContext(Dispatchers.IO) {
				val file = File(notesDir, fileName)
				if (file.exists()) {
					file.delete()
				}
			}
			val current = _notes.value.toMutableList()
			current.removeAll { it.fileName == fileName }
			_notes.value = current
		}
	}
}