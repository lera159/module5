package com.example.rksmp_pr2_1.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(
	val fileName: String,
	val title: String,
	val content: String,
	val timestamp: Long
) {
	val formattedDate: String
		get() {
			val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
			return sdf.format(Date(timestamp))
		}
	val preview: String
		get() = if (content.length > 35) content.take(35) + "…" else content
}